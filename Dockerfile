FROM maven AS build-stage
COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests

FROM tomcat:9.0.112-jdk17

COPY --from=build-stage /target/*.war /usr/local/tomcat/webapps/ROOT.war

ARG DB_URL=jdbc:postgresql://localhost:5432/function_db
ARG DB_URL_TEST=jdbc:postgresql://localhost:5432/function_db_test
ARG DB_USER=postgres
ARG DB_PASSWORD=postgres
ARG TEST_BENCH=false

ENV DATABASE_URL=${DB_URL}
ENV DATABASE_URL_TEST=${DB_URL_TEST}
ENV DATABASE_USERNAME=${DB_USER}
ENV DATABASE_PASSWORD=${DB_PASSWORD}
ENV DO_TEST_BENCHMARK=${TEST_BENCH}

EXPOSE 8080

CMD ["catalina.sh", "run"]
