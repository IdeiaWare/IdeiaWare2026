FROM maven:3.9-eclipse-temurin-21 AS build
ENV TZ=America/Sao_Paulo
ARG SKIP_TESTS=false
WORKDIR /src

# BUILD-CACHE: pom.xml isolado numa camada propria -- so baixa dependencia de novo se o pom
# mudar, nao a cada edicao de codigo-fonte (o caso comum durante iteracao local).
COPY IdeiaWare-main/pom.xml   IdeiaWare-main/pom.xml
COPY Toolkit2025-main/pom.xml Toolkit2025-main/pom.xml
RUN mvn -f IdeiaWare-main/pom.xml   dependency:go-offline \
 && mvn -f Toolkit2025-main/pom.xml dependency:go-offline

COPY IdeiaWare-main/    IdeiaWare-main/
COPY Toolkit2025-main/  Toolkit2025-main/
RUN mvn -f IdeiaWare-main/pom.xml   clean package -DskipTests=${SKIP_TESTS} \
 && mvn -f Toolkit2025-main/pom.xml clean package -DskipTests=${SKIP_TESTS}

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
