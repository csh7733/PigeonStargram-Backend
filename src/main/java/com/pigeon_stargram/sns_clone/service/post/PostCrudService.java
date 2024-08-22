package com.pigeon_stargram.sns_clone.service.post;

import com.pigeon_stargram.sns_clone.domain.post.Post;
import com.pigeon_stargram.sns_clone.exception.post.PostNotFoundException;
import com.pigeon_stargram.sns_clone.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.pigeon_stargram.sns_clone.exception.ExceptionMessageConst.POST_NOT_FOUND_ID;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostCrudService {

    private final PostRepository repository;

    public Post findById(Long postId) {
        return repository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(POST_NOT_FOUND_ID));
    }

    public List<Post> findByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    public List<Post> findByUserIdAndCreatedDateAfter(Long userId,
                                                      LocalDateTime createdDate) {
        return repository.findByUserIdAndCreatedDateAfter(userId, createdDate);
    }

    public Post save(Post post) {
        return repository.save(post);
    }

    public void deleteById(Long postId) {
        repository.deleteById(postId);
    }
}
