FROM openjdk:17-alpine
VOLUME /tmp
EXPOSE 9999
ADD target/Authorization-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]