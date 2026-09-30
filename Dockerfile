FROM maven:3.9.16-eclipse-temurin-26 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src
COPY database ./database

RUN mvn clean package -DskipTests

FROM eclipse-temurin:26-jre

ENV CATALINA_HOME=/opt/tomcat

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl ca-certificates tar \
    && rm -rf /var/lib/apt/lists/*

RUN curl -fsSL \
    https://archive.apache.org/dist/tomcat/tomcat-9/v9.0.120/bin/apache-tomcat-9.0.120.tar.gz \
    | tar -xz -C /opt \
    && mv /opt/apache-tomcat-9.0.120 ${CATALINA_HOME}

COPY --from=build /app/target/FloodPath.war ${CATALINA_HOME}/webapps/ROOT.war

EXPOSE 10000

CMD sed -i "s/port=\"8080\"/port=\"${PORT:-10000}\"/" ${CATALINA_HOME}/conf/server.xml \
    && ${CATALINA_HOME}/bin/catalina.sh run