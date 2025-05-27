FROM maven:3.8.6-openjdk-11 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM tomcat:9.0-jdk11-openjdk
COPY --from=build /app/target/BookstoreAPI.war /usr/local/tomcat/webapps/
EXPOSE 8080
CMD ["catalina.sh", "run"]