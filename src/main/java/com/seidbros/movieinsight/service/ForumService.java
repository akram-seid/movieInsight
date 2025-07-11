package com.seidbros.movieinsight.service;


import com.seidbros.movieinsight.dto.DeleteReplyInDto;
import com.seidbros.movieinsight.dto.ForumPostInDto;
import com.seidbros.movieinsight.dto.ForumPostUpdateInDto;
import com.seidbros.movieinsight.dto.ReplyInDto;
import com.seidbros.movieinsight.dto.base.Base;
import com.seidbros.movieinsight.dto.base.BaseList;
import com.seidbros.movieinsight.mapper.ForumPostMapper;
import com.seidbros.movieinsight.model.DeletedReply;
import com.seidbros.movieinsight.model.ForumPost;
import com.seidbros.movieinsight.model.Reply;
import com.seidbros.movieinsight.repository.ForumPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForumService {

    private final BaseService baseService;
    private final MongoTemplate mongoTemplate;
    private final ForumPostRepository forumPostRepository;
    private final ForumPostMapper forumPostMapper;


    public Base<?> createPost(ForumPostInDto forumPostInDto) {
        try {
            ForumPost forumPost = forumPostMapper.fromForumPostInDto(forumPostInDto);
            forumPost.setTimestamp(LocalDateTime.now());
            forumPost.setPostId(UUID.randomUUID().toString());
            forumPostRepository.save(forumPost);
            return baseService.success(forumPostMapper.toForumPostOutDto(forumPost));
        } catch (Exception e) {
            e.printStackTrace();
            return baseService.error();
        }

    }

    public BaseList<?> getForumPosts(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ForumPost> forumPosts = forumPostRepository.findAll(pageRequest);
        return baseService.success(
                forumPostMapper.toForumPostOutDtoList(
                        forumPosts.getContent().stream()
                                .sorted(Comparator.comparing(ForumPost::getTimestamp))
                                .toList()
                ),
                forumPosts.getTotalElements(),
                forumPosts.getTotalPages()
        );
    }

    public Base<?> updatePost(ForumPostUpdateInDto forumPostUpdateInDto) {
        Optional<ForumPost> postOptional = forumPostRepository.findForumPostByPostIdIs(forumPostUpdateInDto.postId());

        if (postOptional.isPresent()) {
            ForumPost forumPost = postOptional.get();
            forumPost.setContent(forumPostUpdateInDto.content());
            ForumPost updatedPost = forumPostRepository.save(forumPost);
            return baseService.success(forumPostMapper.toForumPostOutDto(updatedPost));
        }
        return baseService.error("Post not found");
    }

    public Base<?> deletePost(String postId) {
        Optional<ForumPost> postOptional = forumPostRepository.findForumPostByPostIdIs(postId);
        if (postOptional.isPresent()) {
            try {
                forumPostRepository.delete(postOptional.get());
                return baseService.success("Post deleted successfully");

            } catch (Exception e) {
                e.printStackTrace();
                return baseService.error();
            }
        }
        return baseService.error("Post not found");

    }

    public Base<?> findPostById(String postId) {
        Optional<ForumPost> postOptional = forumPostRepository.findForumPostByPostIdIs(postId);
        if (postOptional.isPresent()) {
            return baseService.success(forumPostMapper.toForumPostOutDto(postOptional.get()));
        }
        return baseService.error("Post not found");
    }

    public BaseList<?> findPostsByMovieId(String movieId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ForumPost> forumPostsByMovieId = forumPostRepository.findForumPostsByMovieId(movieId, pageRequest);
        if (forumPostsByMovieId.isEmpty()) {
            return baseService.listError("There are no posts found");
        }
        return baseService.success(forumPostMapper.toForumPostOutDtoList(forumPostsByMovieId.getContent()), forumPostsByMovieId.getTotalElements(), forumPostsByMovieId.getTotalPages());
    }

    public Base<?> replyToPost(ReplyInDto replyInDto) {
        Optional<ForumPost> forumPostByPostId = forumPostRepository.findForumPostByPostId(replyInDto.postId());
        if (forumPostByPostId.isPresent()) {
            Reply reply = Reply.builder().replyId(UUID.randomUUID().toString()).timestamp(LocalDateTime.now()).userId(replyInDto.userId()).comment(replyInDto.comment()).build();

            ForumPost forumPost = forumPostByPostId.get();
            forumPost.getReplies().add(reply);
            forumPostRepository.save(forumPost);
            return baseService.success(reply);
        }
        return baseService.error("Post not found");
    }

    public Base<?> deleteReply(DeleteReplyInDto deleteReplyInDto) {
        Optional<ForumPost> optionalPost = forumPostRepository.findForumPostByPostId(deleteReplyInDto.postId());

        if (optionalPost.isEmpty()) {
            return baseService.error("Post not found");
        }

        ForumPost forumPost = optionalPost.get();

        Optional<Reply> replyOpt = forumPost.getReplies().stream().filter(r -> r.getReplyId().equals(deleteReplyInDto.replyId())).findFirst();

        if (replyOpt.isEmpty()) {
            return baseService.error("Reply not found");
        }

        Reply reply = replyOpt.get();

        forumPost.getReplies().remove(reply);
        DeletedReply deleted = new DeletedReply();
        deleted.setOriginalAuthor(reply.getUserId());
        deleted.setOriginalMessage(reply.getComment());
        deleted.setDeletedAt(LocalDateTime.now());
        deleted.setModerator(deleteReplyInDto.moderator());
        deleted.setDeletionReason(deleteReplyInDto.reason() != null ? deleteReplyInDto.reason() : "No reason provided");

        forumPost.getDeletedReplies().add(deleted);
        forumPostRepository.save(forumPost);
        return baseService.success("Reply deleted successfully");

    }

    public BaseList<?> getReplies(String postId) {
        Optional<ForumPost> optionalPost = forumPostRepository.findForumPostByPostId(postId);
        if (optionalPost.isPresent()) {
            ForumPost forumPost = optionalPost.get();
            if (forumPost.getReplies().isEmpty()) {
                return baseService.listError("There are no replies found");
            }
            return baseService.success(forumPost.getReplies());
        }
        return baseService.listError("Post not found");

    }

    public BaseList<?> getDeletedReplies(String postId) {
        Optional<ForumPost> optionalPost = forumPostRepository.findForumPostByPostId(postId);
        if (optionalPost.isPresent()) {
            ForumPost forumPost = optionalPost.get();
            if (forumPost.getDeletedReplies().isEmpty()) {
                return baseService.listError("There are no replies found");
            }
            return baseService.success(forumPost.getDeletedReplies());
        }
        return baseService.listError("Post not found");

    }


}
