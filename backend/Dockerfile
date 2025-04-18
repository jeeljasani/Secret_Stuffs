FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /build

COPY mvnw .
COPY .mvn/ .mvn/
COPY pom.xml .
COPY src/ src/

RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre-jammy

ARG UID=10001
RUN adduser --disabled-password --gecos "" --home "/nonexistent" --shell "/sbin/nologin" --no-create-home --uid "${UID}" appuser
USER appuser

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app.jar"]