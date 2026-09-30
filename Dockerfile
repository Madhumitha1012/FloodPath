# =========================
# BUILD STAGE
# =========================
FROM maven:3.9.16-eclipse-temurin-26 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src
COPY database ./database

RUN mvn clean package -DskipTests


# =========================
# TOMCAT RUNTIME STAGE
# =========================
FROM eclipse-temurin:26-jre

ENV CATALINA_HOME=/opt/tomcat

# Install required tools
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl ca-certificates tar \
    && rm -rf /var/lib/apt/lists/*

# Install Tomcat 9.0.120
RUN curl -fsSL \
    https://archive.apache.org/dist/tomcat/tomcat-9/v9.0.120/bin/apache-tomcat-9.0.120.tar.gz \
    | tar -xz -C /opt \
    && mv /opt/apache-tomcat-9.0.120 ${CATALINA_HOME}

# Remove Tomcat default applications
RUN rm -rf \
    ${CATALINA_HOME}/webapps/ROOT \
    ${CATALINA_HOME}/webapps/ROOT.war

# Deploy FloodPath as the ROOT application
COPY --from=build /app/target/FloodPath.war \
    ${CATALINA_HOME}/webapps/ROOT.war

# Render uses the PORT environment variable
EXPOSE 10000

# Disable Tomcat shutdown port
# Change HTTP port 8080 -> Render PORT
# Start Tomcat
CMD sed -i 's/port="8005"/port="-1"/' ${CATALINA_HOME}/conf/server.xml \
    && sed -i "s/port=\"8080\"/port=\"${PORT:-10000}\"/" ${CATALINA_HOME}/conf/server.xml \
    && ${CATALINA_HOME}/bin/catalina.sh run