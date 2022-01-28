FROM adoptopenjdk/openjdk11:alpine
ARG JAR_FILE=04-book-service/target/*.jar
COPY ${JAR_FILE} app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar","/app.jar"]