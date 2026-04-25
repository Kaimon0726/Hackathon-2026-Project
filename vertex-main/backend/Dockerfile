FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
CMD ["./gradlew", "bootRun"]