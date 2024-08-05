package com.pigeon_stargram.sns_clone.service.reply;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import com.pigeon_stargram.sns_clone.domain.reply.ReplyLike;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.reply.ReplyDto;
import com.pigeon_stargram.sns_clone.repository.reply.ReplyLikeRepository;
import com.pigeon_stargram.sns_clone.repository.reply.ReplyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final ReplyLikeRepository replyLikeRepository;

    public void createReply(User user, Comment comment, String content) {
        Reply reply = Reply.builder()
                .user(user)
                .comment(comment)
                .content(content)
                .build();

        replyRepository.save(reply);
    }

    public ReplyDto getReply(Long replyId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reply ID"));
        return new ReplyDto(reply);
    }

    public List<ReplyDto> getReplyListByComment(Long commentId) {
        List<Reply> replies = replyRepository.findByCommentId(commentId);
        return replies.stream()
                .map(ReplyDto::new)
                .collect(Collectors.toList());
    }

    public ReplyDto editReply(Long replyId, String newContent) {
        Reply reply = getReplyEntity(replyId);
        reply.modify(newContent);
        return new ReplyDto(reply);
    }

    public void likeReply(User user, Long replyId) {
        Reply reply = getReplyEntity(replyId);

        Optional<ReplyLike> existingLike = replyLikeRepository.findByUserAndReply(user,reply);

        if (existingLike.isPresent()) {
            replyLikeRepository.delete(existingLike.get());
            reply.decrementLikes();
        } else {
            ReplyLike replyLike = ReplyLike.builder()
                    .user(user)
                    .reply(reply)
                    .build();
            replyLikeRepository.save(replyLike);
            reply.incrementLikes();
        }
    }

    public void deleteReply(Long replyId) {
        replyRepository.deleteById(replyId);
    }

    private Reply getReplyEntity(Long replyId) {
        return replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reply ID"));
    }
}
