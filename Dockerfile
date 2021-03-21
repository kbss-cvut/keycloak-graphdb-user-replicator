FROM jboss/keycloak

USER jboss

ARG DEPLOY_PATH=modelujeme\/sluzby\/al-auth-server

ENV KEYCLOAK_HOME=/opt/jboss/keycloak
ADD target/keycloak-graphdb-user-replicator.jar $KEYCLOAK_HOME/standalone/deployments/
ENV STANDALONE_XML=$KEYCLOAK_HOME/standalone/configuration/standalone.xml
ENV STANDALONE_HA_XML=$KEYCLOAK_HOME/standalone/configuration/standalone-ha.xml
ENV DOMAIN_XML=$KEYCLOAK_HOME/domain/configuration/domain.xml
RUN sed -i -e "s!<web-context>auth<\/web-context>!<web-context>${DEPLOY_PATH}<\/web-context>!" $STANDALONE_XML
RUN sed -i -e "s!<web-context>auth<\/web-context>!<web-context>${DEPLOY_PATH}<\/web-context>!" $STANDALONE_HA_XML
RUN sed -i -e "s!<web-context>auth<\/web-context>!<web-context>${DEPLOY_PATH}<\/web-context>!" $DOMAIN_XML

WORKDIR keycloak-config
ADD spi.xml /
ENV PLACE='<subsystem xmlns="urn:jboss:domain:keycloak-server:1.1">'
RUN SPI=`cat /spi.xml | tr -d '\n'` ; sed -i "s~$PLACE~$PLACE$SPI~g" $STANDALONE_XML