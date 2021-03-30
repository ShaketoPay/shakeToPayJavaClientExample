FROM maven:3.6-jdk-11 as BUILD

COPY src /usr/project/src
COPY pom.xml /usr/project
COPY client.props /usr/project/client.properties
RUN mvn -f /usr/project/pom.xml clean package


FROM openjdk:11

COPY --from=BUILD /usr/project/target/exampleClient.jar /usr/run/exampleClient.jar
COPY --from=BUILD /usr/project/client.properties /usr/run/client.properties

EXPOSE 5000

ENTRYPOINT ["java", "-jar", "/usr/run/exampleClient.jar", "--spring.config.location=file:/usr/run/client.properties"]