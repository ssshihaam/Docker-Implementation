FROM maven:3.9-eclipse-temurin-21

WORKDIR /test-app

COPY pom.xml .
COPY src ./src
COPY testng.xml .

RUN mvn dependency:resolve

CMD ["mvn", "test"]