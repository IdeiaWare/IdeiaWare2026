#!/bin/sh
set -e
# Templating dos configs de banco via env (resolve o host 127.0.0.1 hardcoded + senha + hbm2ddl/
# show_sql) SEM rebuildar a imagem. Os segredos de CODIGO (SENDGRID_API_KEY/AES_KEY/
# CAIXA_HMAC_SECRET) NAO precisam de sed: o codigo le direto via System.getenv (vem do container).
: "${DB_HOST:=db}"; : "${DB_PORT:=3306}"; : "${DB_NAME:=lic_bd}"; : "${DB_PASS:=root}"
: "${HBM2DDL:=validate}"; : "${SHOW_SQL:=false}"

LIC=/usr/local/tomcat/webapps/LIC/WEB-INF/classes/hibernate.cfg.xml
TK=/usr/local/tomcat/webapps/toolkit/WEB-INF/toolkit-servlet.xml

# host + nome do banco (mantem o resto da query string: useSSL/charset/serverTimezone)
sed -i "s#127.0.0.1:3306/lic_bd#${DB_HOST}:${DB_PORT}/${DB_NAME}#g" "$LIC" "$TK"

# senha do banco
sed -i "s#connection.password\">[^<]*<#connection.password\">${DB_PASS}<#" "$LIC"
sed -i "s#name=\"password\" value=\"[^\"]*\"#name=\"password\" value=\"${DB_PASS}\"#" "$TK"

# hbm2ddl (LIC) + show_sql (LIC e Toolkit)
sed -i "s#hbm2ddl.auto\">[a-z]*<#hbm2ddl.auto\">${HBM2DDL}<#" "$LIC"
sed -i "s#\"show_sql\">[a-z]*<#\"show_sql\">${SHOW_SQL}<#" "$LIC"
sed -i "s#hibernate.show_sql\">[a-z]*<#hibernate.show_sql\">${SHOW_SQL}<#" "$TK"

exec "$@"
