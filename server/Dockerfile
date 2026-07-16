FROM eclipse-temurin:21-jre-jammy
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
# force rebuild 2026-07-16
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]