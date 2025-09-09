FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
COPY applications/app-service/build/libs/solicitudes.jar solicitudes.jar

RUN addgroup -S appuser && adduser -S appuser -G appuser

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"

USER appuser

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS  -jar solicitudes.jar" ]