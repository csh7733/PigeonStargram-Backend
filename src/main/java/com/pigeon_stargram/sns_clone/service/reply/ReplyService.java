package com.pigeon_stargram.sns_clone.service.reply;

import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import com.pigeon_stargram.sns_clone.domain.reply.ReplyLike;
import com.pigeon_stargram.sns_clone.dto.reply.internal.CreateReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.LikeReplyDto;
import com.pigeon_stargram.sns_clone.dto.reply.internal.ReplyContentDto;
import com.pigeon_stargram.sns_clone.dto.reply.response.ReplyLikeDto;
import com.pigeon_stargram.sns_clone.dto.reply.response.ResponseReplyDto;
import com.pigeon_stargram.sns_clone.repository.reply.ReplyLikeRepository;
import com.pigeon_stargram.sns_clone.repository.reply.ReplyRepository;
import com.pigeon_stargram.sns_clone.service.notification.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReplyService {

    private final NotificationService notificationService;

    private final ReplyRepository replyRepository;
    private final ReplyLikeRepository replyLikeRepository;

    public List<ResponseReplyDto> getRepliesByCommentId(Long commentId) {
        return replyRepository.findByCommentId(commentId).stream()
                .map(Reply::getId)
                .sorted(Comparator.reverseOrder())
                .map(this::getCombinedReply)
                .toList();
    }

    public ResponseReplyDto getCombinedReply(Long replyId) {
        ReplyContentDto replyContentDto = getReplyContent(replyId);
        ReplyLikeDto replyLikeDto = getReplyLike(replyId);
        return new ResponseReplyDto(replyContentDto, replyLikeDto);
    }

    public ReplyContentDto getReplyContent(Long replyId) {
        return new ReplyContentDto(getReplyEntity(replyId));
    }

    public ReplyLikeDto getReplyLike(Long replyId) {
        Integer count = replyLikeRepository.countByReplyId(replyId);
        return new ReplyLikeDto(false, count);
    }


    public Reply createReply(CreateReplyDto dto) {
        Reply reply = Reply.builder()
                .user(dto.getUser())
                .comment(dto.getComment())
                .content(dto.getContent())
                .build();
        replyRepository.save(reply);

        notificationService.save(dto);
        return reply;
    }

//    public ReplyDto getReply(Long replyId) {
//        Reply reply = replyRepository.findById(replyId)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid reply ID"));
//        return new ReplyDto(reply);
//    }

    public List<ResponseReplyDto> getReplyListByComment(Long commentId) {
        return replyRepository.findByCommentId(commentId).stream()
                .map(reply -> {
                    Integer likeCount = replyLikeRepository.countByReplyId(reply.getId());
                    return new ResponseReplyDto(reply, likeCount);
                })
                .toList();
    }

    public void editReply(Long replyId, String newContent) {
        Reply reply = getReplyEntity(replyId);
        reply.modify(newContent);
    }

    public void likeReply(LikeReplyDto dto) {
        Reply reply = getReplyEntity(dto.getReplyId());
        dto.setWriterId(reply.getUser().getId());

        replyLikeRepository.findByUserAndReply(dto.getUser(), reply)
                .ifPresentOrElse(
                        existingLike -> {
                            replyLikeRepository.delete(existingLike);
                        },
                        () -> {
                            ReplyLike replyLike = ReplyLike.builder()
                                    .user(dto.getUser())
                                    .reply(reply)
                                    .build();
                            replyLikeRepository.save(replyLike);
                            notificationService.save(dto);
                        }
                );
    }

    public void deleteReply(Long replyId) {
        replyRepository.deleteById(replyId);
    }

    public void deleteByCommentId(Long commentId) {
        replyRepository.deleteByCommentId(commentId);
    }

    private Reply getReplyEntity(Long replyId) {
        return replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reply ID"));
    }
}
