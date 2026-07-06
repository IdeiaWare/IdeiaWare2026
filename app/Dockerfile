FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src
COPY IdeiaWare-main/    IdeiaWare-main/
COPY Toolkit2025-main/  Toolkit2025-main/
RUN mvn -f IdeiaWare-main/pom.xml   clean package \
 && mvn -f Toolkit2025-main/pom.xml clean package

FROM tomcat:9.0-jdk21-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /src/IdeiaWare-main/target/LIC.war       /tmp/LIC.war
COPY --from=build /src/Toolkit2025-main/target/toolkit.war /tmp/toolkit.war
RUN cd /usr/local/tomcat/webapps \
 && mkdir LIC toolkit \
 && (cd LIC      && jar -xf /tmp/LIC.war) \
 && (cd toolkit  && jar -xf /tmp/toolkit.war) \
 && rm /tmp/*.war
COPY app/setenv.sh     /usr/local/tomcat/bin/setenv.sh
COPY app/entrypoint.sh /entrypoint.sh
RUN chmod +x /usr/local/tomcat/bin/setenv.sh /entrypoint.sh \
 && sed -i 's/\r$//' /usr/local/tomcat/bin/setenv.sh /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
CMD ["catalina.sh", "run"]
