FROM maven:3.9.9-amazoncorretto-21 as builder
WORKDIR /user/src/myapp
COPY . .
RUN mvn clean package


FROM azul/zulu-openjdk:21-latest
VOLUME /tmp
COPY --from=builder /user/src/myapp/target/buuking-*.jar app.jar
COPY --from=builder /user/src/myapp/target/opentelemetry-javaagent.jar .
CMD ["sh", "-c", "java $JAVA_TOOL_OPTIONS -jar /app.jar" ]