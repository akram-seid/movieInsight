package com.seidbros.movieinsight.common.utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import org.bson.Document;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class MongoChangeStreamListener {

    private final MongoClient mongoClient;
    private final Neo4jSyncService neo4jSyncService;

    public MongoChangeStreamListener(MongoClient mongoClient, Neo4jSyncService neo4jSyncService) {
        this.mongoClient = mongoClient;
        this.neo4jSyncService = neo4jSyncService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startListening() {
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
            for (ChangeStreamDocument<Document> change : moviesCollection.watch()) {
                handleMovieChange(change);
            }
        }).start();

        new Thread(() -> {
            for (ChangeStreamDocument<Document> change : ratingsCollection.watch()) {
                handleRatingsChange(change);
            }
        }).start();

        new Thread(() -> {
            for (ChangeStreamDocument<Document> change : usersCollection.watch()) {
                handleUserChange(change);
            }
        }).start();
    }

    private void handleMovieChange(ChangeStreamDocument<Document> change) {
        Document fullDocument = change.getFullDocument();
        String operationType = change.getOperationType().getValue();
        System.out.println("OperationType: " + operationType);

        switch (operationType) {
            case "insert":
            case "update":
            case "replace":
                neo4jSyncService.syncMovie(fullDocument);
                break;
        }
    }


    private void handleUserChange(ChangeStreamDocument<Document> change) {
        Document fullDocument = change.getFullDocument();
        String operationType = change.getOperationType().getValue();

        switch (operationType) {
            case "insert":
            case "update":
            case "replace":
                neo4jSyncService.syncUser(fullDocument);
                break;
        }
    }

    private void handleRatingsChange(ChangeStreamDocument<Document> change) {
        Document fullDocument = change.getFullDocument();
        String operationType = change.getOperationType().getValue();

        switch (operationType) {
            case "insert":
            case "update":
            case "replace":
                neo4jSyncService.syncRating(fullDocument);
                break;
        }
    }
}
