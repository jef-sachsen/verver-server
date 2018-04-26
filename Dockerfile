# Create a jdk container as a builder
FROM openjdk:8u162-jdk-stretch as builder
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

# Create working directory
RUN mkdir -p /usr/app
WORKDIR /usr/app

# Copy the source from the local machine to the builder container and build
COPY ./src /usr/app/src
COPY pom.xml /usr/app

RUN mvn package -Ddockerfile.skip

# Copy the artifact
RUN cp /usr/app/target/mv-*.jar /usr/app/app.jar

FROM openjdk:8-jdk-alpine
COPY --from=builder /usr/app/app.jar /opt/app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/opt/app.jar"]
EXPOSE 8080