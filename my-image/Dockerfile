
FROM eclipse-temurin:21-jdk
LABEL maintainer="2486165@cognizant.com"
LABEL version="1.0"

WORKDIR /test-app

COPY mini-project.jar /test-app/mini-project.jar

RUN apt-get update && apt-get install -y curl wget

ENV SELENIUM_HUB_URL=http://selenium-hub:4444/wd/hub

EXPOSE 4444

CMD ["java", "-jar", "/test-app/mini-project.jar"]

