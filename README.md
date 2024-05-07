# Users REST API application

## Technology Stack

    Java 21
    Spring Boot 3.2.5
    Maven
    Docker
    DB - PostgreSQL

## DB setup
    
    cd users-api\src\main\resources\db
    
    docker-compose up -d

## Run the app local

    mvn clean package

    cd users-api\target
    
    java -jar users-api-0.0.1-SNAPSHOT.jar

    API will be available on the localhost:8080

## Run the app locally in the docker container

    cd users-api\

    mvn clean package
    
    docker-compose up -d

    API will be available on the localhost:8080

## Run the tests

    mvn test

## API description

The open API documentation can be found at the following link:

http://localhost:8080/swagger-ui/index.html



