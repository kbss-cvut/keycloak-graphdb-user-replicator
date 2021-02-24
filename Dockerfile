FROM jboss/keycloak

USER jboss

ARG CONTEXT=modelujeme/služby/authServer

ENV KEYCLOAK_HOME=/opt/jboss/keycloak
ADD target/keycloak-graphdb-user-replicator.jar $KEYCLOAK_HOME/standalone/deployments/
ENV STANDALONE_XML=$KEYCLOAK_HOME/standalone/configuration/standalone.xml
ENV PLACE='<subsystem xmlns="urn:jboss:domain:keycloak-server:1.1">'
ENV CONTEXT=\/auth
RUN sed -i -e 's!<web-context>auth</web-context>!<web-context>${CONTEXT}</web-context>!' $STANDALONE_XML
WORKDIR keycloak-config
ADD spi.xml /
RUN SPI=`cat /spi.xml | tr -d '\n'` ; sed -i "s~$PLACE~$PLACE$SPI~g" $STANDALONE_XML