package com.seidbros.movieinsight;

import com.seidbros.movieinsight.common.utils.Neo4jSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class MovieInsightApplication  implements CommandLineRunner {
    private final Neo4jSyncService neo4jSyncService;

    public static void main(String[] args) {
        SpringApplication.run(MovieInsightApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        neo4jSyncService.syncMoviesAndGenres();
        System.out.println("Sync complete.");
//        neo4jSyncService.syncUsersAndRatings();
        neo4jSyncService.createCollaborations();
    }
}
