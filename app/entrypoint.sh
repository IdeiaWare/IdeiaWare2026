#!/bin/sh
set -e
: "${DB_HOST:=db}"; : "${DB_PORT:=3306}"; : "${DB_NAME:=lic_bd}"; : "${DB_PASS:=root}"
: "${HBM2DDL:=validate}"; : "${SHOW_SQL:=false}"

LIC=/usr/local/tomcat/webapps/LIC/WEB-INF/classes/hibernate.cfg.xml
TK=/usr/local/tomcat/webapps/toolkit/WEB-INF/toolkit-servlet.xml

sed -i "s#127.0.0.1:3306/lic_bd#${DB_HOST}:${DB_PORT}/${DB_NAME}#g" "$LIC" "$TK"

sed -i "s#connection.password\">[^<]*<#connection.password\">${DB_PASS}<#" "$LIC"
sed -i "s#name=\"password\" value=\"[^\"]*\"#name=\"password\" value=\"${DB_PASS}\"#" "$TK"

sed -i "s#hbm2ddl.auto\">[a-z]*<#hbm2ddl.auto\">${HBM2DDL}<#" "$LIC"
sed -i "s#\"show_sql\">[a-z]*<#\"show_sql\">${SHOW_SQL}<#" "$LIC"
sed -i "s#hibernate.show_sql\">[a-z]*<#hibernate.show_sql\">${SHOW_SQL}<#" "$TK"

exec "$@"
