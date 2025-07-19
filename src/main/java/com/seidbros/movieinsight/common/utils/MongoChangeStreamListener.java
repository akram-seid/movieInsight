package com.seidbros.movieinsight.common.utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import org.bson.Document;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j; // Ensure this import is present

@Service
@Slf4j
public class MongoChangeStreamListener {

    private final MongoClient mongoClient;
    private final Neo4jSyncService neo4jSyncService;

    public MongoChangeStreamListener(MongoClient mongoClient, Neo4jSyncService neo4jSyncService) {
        this.mongoClient = mongoClient;
        this.neo4jSyncService = neo4jSyncService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startListening() {
        log.info("Starting MongoDB Change Stream Listeners...");

        MongoCollection<Document> moviesCollection = mongoClient
                .getDatabase("movieInsight")
                .getCollection("movies");

        MongoCollection<Document> ratingsCollection = mongoClient
                .getDatabase("movieInsight")
                .getCollection("ratings");

        MongoCollection<Document> usersCollection = mongoClient
                .getDatabase("movieInsight")
                .getCollection("users");

        new Thread(() -> {
            log.info("Movie Change Stream Listener started.");
            try {
                for (ChangeStreamDocument<Document> change : moviesCollection.watch()) {
                    handleMovieChange(change);
                }
            } catch (Exception e) {
                log.error("Movie Change Stream encountered a critical error and stopped. Restart application. Error: {}", e.getMessage(), e);
            }
        }, "MovieChangeStreamThread").start();

        new Thread(() -> {
            log.info("Ratings Change Stream Listener started.");
            try {
                for (ChangeStreamDocument<Document> change : ratingsCollection.watch()) {
                    handleRatingsChange(change);
                }
            } catch (Exception e) {
                log.error("Ratings Change Stream encountered a critical error and stopped. Restart application. Error: {}", e.getMessage(), e);
            }
        }, "RatingsChangeStreamThread").start();

        new Thread(() -> {
            log.info("User Change Stream Listener started.");
            try {
                for (ChangeStreamDocument<Document> change : usersCollection.watch()) {
                    handleUserChange(change);
                }
            } catch (Exception e) {
                log.error("User Change Stream encountered a critical error and stopped. Restart application. Error: {}", e.getMessage(), e);
            }
        }, "UserChangeStreamThread").start();
    }

    private void handleMovieChange(ChangeStreamDocument<Document> change) {
        String operationType = change.getOperationType().getValue();
        Document fullDocument = change.getFullDocument();
        String documentKeyId = (change.getDocumentKey() != null && change.getDocumentKey().containsKey("_id")) ?
                change.getDocumentKey().get("_id").toString() : "N/A";

        log.debug("Movie Change Stream Event: OperationType={}, DocumentKey={}", operationType, documentKeyId);

        try {
            switch (operationType) {
                case "insert":
                case "update":
                case "replace":
                    neo4jSyncService.syncMovie(fullDocument);
                    break;
                default:
                    log.warn("Unhandled movie change stream operation type: {}", operationType);
            }
        } catch (Exception e) {
            log.error("Error processing movie change stream event (Type: {}, Key: {}). This document might be out of sync. Error: {}",
                    operationType, documentKeyId, e.getMessage(), e);
        }
    }

    private void handleUserChange(ChangeStreamDocument<Document> change) {
        String operationType = change.getOperationType().getValue();
        Document fullDocument = change.getFullDocument();
        String documentKeyId = (change.getDocumentKey() != null && change.getDocumentKey().containsKey("_id")) ?
                change.getDocumentKey().get("_id").toString() : "N/A";

        log.debug("User Change Stream Event: OperationType={}, DocumentKey={}", operationType, documentKeyId);

        try {
            switch (operationType) {
                case "insert":
                case "update":
                case "replace":
                    neo4jSyncService.syncUser(fullDocument);
                    break;
                default:
                    log.warn("Unhandled user change stream operation type: {}", operationType);
            }
        } catch (Exception e) {
            log.error("Error processing user change stream event (Type: {}, Key: {}). This document might be out of sync. Error: {}",
                    operationType, documentKeyId, e.getMessage(), e);
        }
    }

    private void handleRatingsChange(ChangeStreamDocument<Document> change) {
        String operationType = change.getOperationType().getValue();
        Document fullDocument = change.getFullDocument();
        String documentKeyId = (change.getDocumentKey() != null && change.getDocumentKey().containsKey("_id")) ?
                change.getDocumentKey().get("_id").toString() : "N/A";

        log.debug("Rating Change Stream Event: OperationType={}, DocumentKey={}", operationType, documentKeyId);

        try {
            switch (operationType) {
                case "insert":
                case "update":
                case "replace":
                    neo4jSyncService.syncRating(fullDocument);
                    break;
                default:
                    log.warn("Unhandled rating change stream operation type: {}", operationType);
            }
        } catch (Exception e) {
            log.error("Error processing rating change stream event (Type: {}, Key: {}). This document might be out of sync. Error: {}",
                    operationType, documentKeyId, e.getMessage(), e);
        }
    }
}