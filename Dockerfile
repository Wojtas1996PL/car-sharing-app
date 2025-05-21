FROM openjdk:17-jdk-alpine AS builder
WORKDIR /application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
COPY .env /application/.env
RUN java -Djarmode=layertools -jar application.jar extract

FROM openjdk:17-jdk-alpine
WORKDIR /application
COPY .env /application/.env
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
EXPOSE 8080
