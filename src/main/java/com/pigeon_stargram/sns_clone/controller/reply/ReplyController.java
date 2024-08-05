package com.pigeon_stargram.sns_clone.controller.reply;

import com.pigeon_stargram.sns_clone.domain.comment.Comment;
import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.post.PostsDto;
import com.pigeon_stargram.sns_clone.dto.reply.request.RequestAddReply;
import com.pigeon_stargram.sns_clone.dto.reply.request.RequestLikeReply;
import com.pigeon_stargram.sns_clone.repository.user.UserRepository;
import com.pigeon_stargram.sns_clone.service.comment.CommentService;
import com.pigeon_stargram.sns_clone.service.post.PostsService;
import com.pigeon_stargram.sns_clone.service.reply.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping("/api/replies")
@RestController
@RequiredArgsConstructor
public class ReplyController {

    private final PostsService postsService;
    private final CommentService commentService;
    private final ReplyService replyService;
    private final UserRepository userRepository;

    @PostMapping("/add")
    public List<PostsDto> addReply(@RequestBody RequestAddReply requestAddReply) {
        Long commentId = requestAddReply.getCommentId();
        Comment comment = commentService.getCommentEntity(commentId);
        String content = requestAddReply.getReply().getContent();

        Long userId = requestAddReply.getReply().getUserId();
        User user = userRepository.findById(userId).get();
        replyService.createReply(user,comment,content);
        return postsService.getAllPosts();
    }

    @PostMapping("/list/like")
    public List<PostsDto> likeReply(@RequestBody RequestLikeReply requestLikeReply) {
        //테스트용 유저
        User user = userRepository.findById(1L).get();
        Long replyId = requestLikeReply.getReplyId();

        replyService.likeReply(user,replyId);
        return postsService.getAllPosts();
    }
}
