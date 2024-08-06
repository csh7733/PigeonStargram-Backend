package com.pigeon_stargram.sns_clone.service.post;

import com.pigeon_stargram.sns_clone.domain.post.Image;
import com.pigeon_stargram.sns_clone.domain.post.Posts;
import com.pigeon_stargram.sns_clone.domain.post.PostsLike;
import com.pigeon_stargram.sns_clone.domain.user.User;

import com.pigeon_stargram.sns_clone.dto.comment.CommentDto;
import com.pigeon_stargram.sns_clone.dto.post.PostsDto;
import com.pigeon_stargram.sns_clone.repository.post.PostsLikeRepository;
import com.pigeon_stargram.sns_clone.repository.post.PostsRepository;
import com.pigeon_stargram.sns_clone.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostsService {

    private final PostsRepository postsRepository;
    private final PostsLikeRepository postsLikeRepository;
    private final CommentService commentService;

    public Posts createPost(User user, String content) {
        Posts post = new Posts(user,content);
        return postsRepository.save(post);
    }

    public Posts createPost(User user, String content, List<Image> images) {
        Posts post = new Posts(user,content,images);
        return postsRepository.save(post);
    }

    public void editPost(Long postId, String content) {
        Posts post = getPostEntity(postId);
        post.modify(content);
    }

    public void editPost(Long postId, String content, List<Image> images) {
        Posts post = getPostEntity(postId);
        post.modify(content,images);
    }

    public void likePost(User user, Long postId) {
        Posts post = getPostEntity(postId);

        postsLikeRepository.findByUserAndPost(user, post)
                .ifPresentOrElse(
                        existingLike -> {
                            postsLikeRepository.delete(existingLike);
                            post.decrementLikes();
                        },
                        () -> {
                            PostsLike postsLike = new PostsLike(user, post);
                            postsLikeRepository.save(postsLike);
                            post.incrementLikes();
                        }
                );
    }

    public List<PostsDto> getPostsByUser(User user) {
        List<Posts> posts = postsRepository.findByUserId(user.getId());
        return posts.stream()
                .map(post -> {
                    List<CommentDto> comments = commentService.getCommentListByPost(post.getId());
                    return new PostsDto(post, comments);
                })
                .collect(Collectors.toList());
    }

    public List<PostsDto> getAllPosts() {
        List<Posts> posts = postsRepository.findAll();
        return posts.stream()
                .map(post -> {
                    List<CommentDto> comments = commentService.getCommentListByPost(post.getId());
                    return new PostsDto(post, comments);
                })
                .collect(Collectors.toList());
    }

    public Posts getPostEntity(Long postId) {
        return postsRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));
    }
}
