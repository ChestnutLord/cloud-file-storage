# Build
FROM eclipse-temurin:24-jdk-alpine AS build
WORKDIR /app

COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew

COPY src ./src
RUN ./gradlew bootJar -x test --no-daemon \
    && find build/libs -name '*.jar' ! -name '*-plain.jar' -exec cp {} /app/app.jar \;

# Runtime
FROM eclipse-temurin:24-jre-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=build /app/app.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
