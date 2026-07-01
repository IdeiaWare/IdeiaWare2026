#!/bin/sh
# D-2: JDK 21 + Hibernate 5.x precisa abrir os modulos p/ gerar proxies (LIC usa o pool
# built-in; Toolkit usa c3p0 — os dois precisam).
export CATALINA_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.invoke=ALL-UNNAMED"
