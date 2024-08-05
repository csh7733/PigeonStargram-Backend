package com.pigeon_stargram.sns_clone.service.post;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.post.Image;
import com.pigeon_stargram.sns_clone.domain.post.Posts;
import com.pigeon_stargram.sns_clone.domain.post.PostsLike;
import com.pigeon_stargram.sns_clone.domain.user.User;

import com.pigeon_stargram.sns_clone.repository.post.PostsLikeRepository;
import com.pigeon_stargram.sns_clone.repository.post.PostsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostsService {

    private final PostsRepository postsRepository;
    private final PostsLikeRepository postsLikeRepository;

    public Posts createPost(User user, String content) {
        Posts post = new Posts(user,content);
        return postsRepository.save(post);
    }

    public Posts createPost(User user, String content, List<Image> images) {
        Posts post = new Posts(user,content,images);
        return postsRepository.save(post);
    }

    public void updatePost(Long postId, String content) {
        Posts post = getPostEntity(postId);
        post.modify(content);
    }

    public void updatePost(Long postId, String content, List<Image> images) {
        Posts post = getPostEntity(postId);
        post.modify(content,images);
    }

    public void likePost(User user, Long postId) {
        Posts post = getPostEntity(postId);
        Optional<PostsLike> existingLike = postsLikeRepository.findByUserAndPost(user, post);
        if (existingLike.isPresent()) {
            postsLikeRepository.delete(existingLike.get());
            post.decrementLikes();
        } else {
            PostsLike postsLike = new PostsLike(user, post);
            postsLikeRepository.save(postsLike);
            post.incrementLikes();
        }
        postsRepository.save(post);
    }


    private Posts getPostEntity(Long postId) {
        return postsRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));
    }
}
