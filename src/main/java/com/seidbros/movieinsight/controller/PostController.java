package com.seidbros.movieinsight.controller;


import com.seidbros.movieinsight.dto.DeleteReplyInDto;
import com.seidbros.movieinsight.dto.ForumPostInDto;
import com.seidbros.movieinsight.dto.ForumPostUpdateInDto;
import com.seidbros.movieinsight.dto.ReplyInDto;
import com.seidbros.movieinsight.dto.base.Base;
import com.seidbros.movieinsight.dto.base.BaseList;
import com.seidbros.movieinsight.service.BaseService;
import com.seidbros.movieinsight.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("forum")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final BaseService baseService;


    @PostMapping("add")
    public ResponseEntity<?> addPost(@RequestBody ForumPostInDto  forumPostInDto) {

        Base<?> post = postService.createPost(forumPostInDto);

        return baseService.rest(post);
    }

    @GetMapping("posts")
    public ResponseEntity<?> getAllPosts(@RequestParam("page") int page, @RequestParam("size") int size) {
        BaseList<?> forumPosts = postService.getForumPosts(page, size);
        return baseService.restList(forumPosts);
    }

    @PutMapping("update")
    public ResponseEntity<?> updatePost(@RequestBody ForumPostUpdateInDto forumPostUpdateInDto) {
        Base<?> base = postService.updatePost(forumPostUpdateInDto);
        return baseService.rest(base);
    }

    @DeleteMapping("delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePost(@RequestParam("postId") String postId) {
        Base<?> base = postService.deletePost(postId);
        return baseService.rest(base);
    }

    @GetMapping("byPostId")
    public ResponseEntity<?> getPostById(@RequestParam("postId") String postId) {

        Base<?> postById = postService.findPostById(postId);
        return baseService.rest(postById);
    }

    @GetMapping("byMovieId")
    public ResponseEntity<?> getPostByMovieId(@RequestParam("movieId") String movieId,
                                              @RequestParam("page") int page,
                                              @RequestParam("size") int size) {

        BaseList<?> postById = postService.findPostsByMovieId(movieId, page,size);
        return baseService.restList(postById);
    }

    @GetMapping("replies")
    public ResponseEntity<?> getReplies(@RequestParam("postId") String postId) {

        BaseList<?> postById = postService.getReplies(postId);
        return baseService.restList(postById);
    }

    @GetMapping("deletedReplies")
    public ResponseEntity<?> getDeletedReplies(@RequestParam("postId") String postId) {

        BaseList<?> postById = postService.getDeletedReplies(postId);
        return baseService.restList(postById);
    }

    @PostMapping("reply")
    public ResponseEntity<?> replyPost(@RequestBody ReplyInDto replyInDto) {
        Base<?> base = postService.replyToPost(replyInDto);
        return baseService.rest(base);
    }

    @DeleteMapping("deleteReply")
    public ResponseEntity<?> deleteReply(@RequestBody DeleteReplyInDto deleteReplyInDto) {
        Base<?> base = postService.deleteReply(deleteReplyInDto);
        return baseService.rest(base);
    }

}
