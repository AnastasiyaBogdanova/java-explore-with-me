FROM openjdk:21-jdk-slim
ARG JAR_FILE=target/ewm-main-service-*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]