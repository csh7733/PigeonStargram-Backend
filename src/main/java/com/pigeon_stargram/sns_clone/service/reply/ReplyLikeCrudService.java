package com.pigeon_stargram.sns_clone.service.reply;

import com.pigeon_stargram.sns_clone.domain.reply.Reply;
import com.pigeon_stargram.sns_clone.domain.reply.ReplyLike;
import com.pigeon_stargram.sns_clone.repository.reply.ReplyLikeRepository;
import com.pigeon_stargram.sns_clone.repository.reply.ReplyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class ReplyLikeCrudService {

    private final ReplyLikeRepository repository;

    public Optional<ReplyLike> findByUserIdAndReplyId(Long userId,
                                                      Long replyId) {
        return repository.findByUserIdAndReplyId(userId, replyId);
    }

    public ReplyLike save(ReplyLike replyLike) {
        return repository.save(replyLike);
    }

    public void delete(ReplyLike replyLike) {
        repository.delete(replyLike);
    }

    public Integer countByReplyId(Long replyId) {
        return repository.countByReplyId(replyId);
    }
}
