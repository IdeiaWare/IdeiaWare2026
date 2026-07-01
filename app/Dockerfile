# ---- build (mata D-1/D-3): o Maven compila os 2 wars; ninguem precisa de Maven/JDK local ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src
COPY IdeiaWare-main/    IdeiaWare-main/
COPY Toolkit2025-main/  Toolkit2025-main/
RUN mvn -f IdeiaWare-main/pom.xml   clean package -DskipTests \
 && mvn -f Toolkit2025-main/pom.xml clean package -DskipTests

# ---- runtime (mata D-1=Tomcat 9 garantido, D-2=JDK 21) ----
FROM tomcat:9.0-jdk21-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /src/IdeiaWare-main/target/LIC.war       /tmp/LIC.war
COPY --from=build /src/Toolkit2025-main/target/toolkit.war /tmp/toolkit.war
# Explode os wars ANTES do Tomcat subir (o entrypoint edita os configs nos arquivos extraidos).
RUN cd /usr/local/tomcat/webapps \
 && mkdir LIC toolkit \
 && (cd LIC      && jar -xf /tmp/LIC.war) \
 && (cd toolkit  && jar -xf /tmp/toolkit.war) \
 && rm /tmp/*.war
COPY app/setenv.sh     /usr/local/tomcat/bin/setenv.sh
COPY app/entrypoint.sh /entrypoint.sh
# chmod + remove CR (caso os .sh tenham sido salvos com CRLF no Windows -> shebang quebraria)
RUN chmod +x /usr/local/tomcat/bin/setenv.sh /entrypoint.sh \
 && sed -i 's/\r$//' /usr/local/tomcat/bin/setenv.sh /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
CMD ["catalina.sh", "run"]
