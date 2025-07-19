# MovieInsight API

Welcome to MovieInsight\! This is a comprehensive backend service that provides movie information, analytics, and social features like forums and ratings. It leverages both **MongoDB** for storing movie data and user-generated content, and **Neo4j** for graph-based analytics like collaborator networks and recommendations.

-----

## Prerequisites

Before you begin, ensure you have the following installed on your system:

  * **Java (JDK 21 or later)**
  * **Apache Maven**
  * **MongoDB**: The application is configured to connect to a MongoDB replica set.
  * **Neo4j**: A running Neo4j instance is required for graph database functionalities.

-----

## Configuration

All application configurations can be found in the `src/main/resources/application.yml` file.

### 1\. MongoDB Configuration:

The application expects a connection to a MongoDB replica set named `rs0` running on `localhost:27017`, `localhost:27018`, and `localhost:27019`. The database name is `movieInsight`.

```yaml
spring:
  data:
    mongodb:
      field-naming-strategy: org.springframework.data.mapping.model.SnakeCaseFieldNamingStrategy
      auto-index-creation: true
      host: localhost
      port: 27017
      additional-hosts: [ localhost:27018, localhost:27019]
      database: movieInsight
      replica-set-name: rs0
```

### 2\. Neo4j Configuration:

The application connects to a Neo4j instance on `bolt://localhost:7687` with the default username `neo4j` and password `Root@1234`. Please update these credentials if your setup is different.

```yaml
  neo4j:
    uri: bolt://localhost:7687
    authentication:
      username: neo4j
      password: your_password
```

-----

## How to Run the Application

### 1\. Start Your Databases:

Ensure that both your MongoDB replica set and Neo4j server are running before starting the application.

### 2\. Build the Project:

Navigate to the root directory of the project and run the following Maven command to build the application:

```bash
mvn clean install
```

### 3\. Run the Application:

Once the build is complete, you can start the application using the following command:

```bash
java -jar target/MovieInsight-0.0.1-SNAPSHOT.jar
```

The server will start on port **8080**.

-----

## Data Synchronization

The application includes a service to synchronize data from MongoDB to Neo4j. This is useful for populating the graph database with movie, user, and relationship data.

  * The `Neo4jSyncService` handles the synchronization of movies, genres, users, and ratings.
  * It also includes functionality to create collaboration relationships between actors in the Neo4j database.
  * The application uses MongoDB Change Streams to listen for data changes and sync them to Neo4j in real-time.

-----

## API Documentation

This project uses SpringDoc to automatically generate OpenAPI 3.0 documentation. Once the application is running, you can access the Swagger UI at the following URL:

[http://localhost:8080/swagger-ui.html](https://www.google.com/search?q=http://localhost:8080/swagger-ui.html)

The API is secured with JWT. You can obtain a token by using the `/user/login` endpoint and use it to authorize your requests in the Swagger UI.

I hope this helps you get started with the MovieInsight application\!
