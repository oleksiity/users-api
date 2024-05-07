FROM eclipse-temurin:21 as builder
EXPOSE 8080
RUN mkdir /opt/app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /opt/app/application.jar
RUN java -Djarmode=layertools -jar /opt/app/application.jar extract

FROM eclipse-temurin:21
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]