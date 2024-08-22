package com.pigeon_stargram.sns_clone.service.reply;

import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import com.pigeon_stargram.sns_clone.domain.reply.ReplyLike;
import com.pigeon_stargram.sns_clone.exception.reply.ReplyNotFoundException;
import com.pigeon_stargram.sns_clone.repository.reply.ReplyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.REPLY_NOT_FOUND_ID;

@RequiredArgsConstructor
@Transactional
@Service
public class ReplyCrudService {

    private final ReplyRepository repository;

    public Reply findById(Long replyId) {
        return repository.findById(replyId)
                .orElseThrow(() -> new ReplyNotFoundException(REPLY_NOT_FOUND_ID));
    }

    public List<Reply> findByCommentId(Long commentId) {
        return repository.findByCommentId(commentId);
    }

    public Reply save(Reply reply) {
        return repository.save(reply);
    }

    public void deleteById(Long replyId) {
        repository.deleteById(replyId);
    }

    public void deleteAllByCommentId(Long commentId) {
        repository.deleteAllByCommentId(commentId);
    }

}
