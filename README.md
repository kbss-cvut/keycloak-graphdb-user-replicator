# Keycloak GraphDB User Replicator

Ensures user data are replicated into a GraphDB instance for the purpose of data provenance display. Another feature
is creating GraphDB users corresponding to Keycloak users so that applications authenticated via Keycloak can asses
protected GraphDB repositories.

## Setup

1. Build project
2. Add the following XML snippet into `$KEYCLOAK_HOME/standalone/configuration/standalone.xml` under the tag
   `<subsystem xmlns="urn:jboss:domain:keycloak-server:1.1">`
   
```xml
<spi name="eventsListener">
    <provider name="keycloak-graphdb-user-replicator" enabled="true">
        <properties>
            <property name="realmId" value="kodi"/>
            <property name="namespace" value="http://onto.fel.cvut.cz/ontologies/uzivatel/"/>
        </properties>
    </provider>
</spi>
```
Property values should be configured as necessary. The SPI will then load them on startup.
3. Copy target JAR into `$KEYCLOAK_HOME/standalone/deployments`
