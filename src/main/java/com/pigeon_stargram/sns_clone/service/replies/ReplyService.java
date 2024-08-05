package com.pigeon_stargram.sns_clone.service.replies;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.repository.reply.ReplyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReplyService {

    private final ReplyRepository replyRepository;

    public Reply createReply(User user, Comment comment, String content) {

        Reply reply = Reply.builder()
                .user(user)
                .comment(comment)
                .content(content)
                .build();
        return replyRepository.save(reply);
    }

    public Reply getReply(Long replyId) {
        return replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reply ID"));
    }

    public List<Reply> getReplyListByComment(Long commentId) {
        return replyRepository.findByCommentId(commentId);
    }

    public void editReply(Long replyId, String newContent) {
        Reply reply = getReply(replyId);
        reply.modify(newContent);
    }

    public void deleteReply(Long replyId) {
        replyRepository.deleteById(replyId);
    }
}
