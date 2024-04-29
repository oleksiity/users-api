# Users REST API application

## Technology Stack

    Java 21
    Spring Boot 3.2.5
    Maven
    DB - PstgreSQL

## DB setup
    
    cd users-api\src\main\resources\db
    
    docker-compose up -d

## Run the app local

    mvn clean install

    cd users-api\target
    
    java -jar users-api-0.0.1-SNAPSHOT.jar

## Run the tests

    mvn test

## API description

The openAPI documentation could be found by the following link:

http://localhost:8080/swagger-ui/index.html


