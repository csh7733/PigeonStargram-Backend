package com.pigeon_stargram.sns_clone.service.reply;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import com.pigeon_stargram.sns_clone.domain.reply.ReplyLike;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.reply.response.ReplyDto;
import com.pigeon_stargram.sns_clone.repository.reply.ReplyLikeRepository;
import com.pigeon_stargram.sns_clone.repository.reply.ReplyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final ReplyLikeRepository replyLikeRepository;

    public Reply createReply(User user, Comment comment, String content) {
        Reply reply = Reply.builder()
                .user(user)
                .comment(comment)
                .content(content)
                .build();
        replyRepository.save(reply);

        return reply;
    }

//    public ReplyDto getReply(Long replyId) {
//        Reply reply = replyRepository.findById(replyId)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid reply ID"));
//        return new ReplyDto(reply);
//    }

    public List<ReplyDto> getReplyListByComment(Long commentId) {
        return replyRepository.findByCommentId(commentId).stream()
                .map(ReplyDto::new)
                .collect(Collectors.toList());
    }

    public void editReply(Long replyId, String newContent) {
        Reply reply = getReplyEntity(replyId);
        reply.modify(newContent);
    }

    public void likeReply(User user, Long replyId) {
        Reply reply = getReplyEntity(replyId);

        replyLikeRepository.findByUserAndReply(user, reply)
                .ifPresentOrElse(
                        existingLike -> {
                            replyLikeRepository.delete(existingLike);
                            reply.decrementLikes();
                        },
                        () -> {
                            ReplyLike replyLike = ReplyLike.builder()
                                    .user(user)
                                    .reply(reply)
                                    .build();
                            replyLikeRepository.save(replyLike);
                            reply.incrementLikes();
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
