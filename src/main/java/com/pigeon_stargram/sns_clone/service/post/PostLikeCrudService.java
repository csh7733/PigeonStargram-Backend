package com.pigeon_stargram.sns_clone.service.post;

import com.pigeon_stargram.sns_clone.domain.post.PostLike;
import com.pigeon_stargram.sns_clone.repository.post.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostLikeCrudService {

    private final PostLikeRepository repository;
    private final PostLikeRepository postLikeRepository;

    public Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId) {
        return postLikeRepository.findByUserIdAndPostId(userId, postId);
    }

    public PostLike save(PostLike postLike) {
        return postLikeRepository.save(postLike);
    }

    public void delete(PostLike postLike) {
        postLikeRepository.delete(postLike);
    }

    public Integer countByPostId(Long postId) {
        return repository.countByPostId(postId);
    }
}
