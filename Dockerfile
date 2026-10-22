FROM eclipse-temurin:25-jdk-noble AS build

WORKDIR /workspace

COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle
RUN ./gradlew dependencies --no-daemon

COPY src ./src
RUN ./gradlew bootJar --no-daemon \
    && cp "$(find build/libs -maxdepth 1 -type f -name '*.jar' ! -name '*-plain.jar' -print -quit)" /workspace/app.jar

FROM eclipse-temurin:25-jre-noble AS runtime

RUN groupadd --system --gid 1001 spring \
    && useradd --system --uid 1001 --gid spring --home-dir /app --shell /usr/sbin/nologin spring

WORKDIR /app
COPY --from=build --chown=spring:spring /workspace/app.jar ./app.jar

USER spring:spring
EXPOSE 8080

# Also expose the Jobrunr dashboard
EXPOSE 8000

ENTRYPOINT ["java", "-jar", "/app/app.jar"]