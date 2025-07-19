package com.seidbros.movieinsight.mongo.repository;

import com.seidbros.movieinsight.mongo.model.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findUserByEmail( String email);
    Optional<User> findUserByUsername(String username);

    List<User> findUsersBySyncedIs(boolean synced);

    }
