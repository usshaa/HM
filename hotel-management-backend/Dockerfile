FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/hotel-management-backend-1.0.0.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar
