package com.pigeon_stargram.sns_clone.controller.post;

import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.post.PostsDto;
import com.pigeon_stargram.sns_clone.dto.post.request.RequestLikePost;
import com.pigeon_stargram.sns_clone.repository.user.UserRepository;
import com.pigeon_stargram.sns_clone.service.post.PostsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RequestMapping("/api/posts")
@RestController
@RequiredArgsConstructor
public class PostsController {

    private final PostsService postsService;
    private final UserRepository userRepository;

    @GetMapping("/list")
    public List<PostsDto> getPosts() {
        //테스트용 유저
        User user = userRepository.findById(1L).get();

        return postsService.getPostsByUser(user);
    }

//    @PostMapping("/editComment")
//    public List<PostsDto2> editComment(@RequestBody EditCommentDto2 dto) {
//        log.info("editComment: {}", dto);
//        testData.postsDtoList.forEach(post -> {
//            if (post.getId().equals(dto.getKey())) {
//                List<CommentDto2> comments = post.getData().getComments();
//
//                if (comments == null) {
//                    comments = new LinkedList<>();
//                    post.getData().setComments(comments);
//                }
//
//
//                comments.add(0, dto.getId());
//            }
//        });
//        return testData.postsDtoList;
//    }

    @PostMapping("/list/like")
    public List<PostsDto> likePost(@RequestBody RequestLikePost requestLikePost) {
        //테스트용 유저
        User user = userRepository.findById(1L).get();

        postsService.likePost(user,requestLikePost.getPostId());
        return postsService.getPostsByUser(user);
    }

}
