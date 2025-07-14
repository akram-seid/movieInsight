package com.seidbros.movieinsight.repository;

import com.seidbros.movieinsight.model.ForumPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForumPostRepository extends MongoRepository<ForumPost, String> {
    Optional<ForumPost> findForumPostByPostIdIs(String postId);

    Page<ForumPost> findForumPostsByMovieId(String movieId, PageRequest pageRequest);

    Optional<ForumPost> findForumPostByPostId(String postId);
}
