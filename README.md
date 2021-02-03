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

### Configuration

The following configuration parameters can (and in some cases must) be provided in the SPI properties

| Parameter            | Required | Default value | Description |
| -------------------- | -------- | ------------- | ----------- |
| `realmId`            | yes      | -             | Identifier of the realm for which events should be processed. |
| `namespace`          | no       | `http://onto.fel.cvut.cz/ontologies/uzivatel/` | Namespace for generating user identifiers. |
| `repositoryUrl`      | yes      | -             | Repository into which basic user metadata should be replicated by this SPI. |
| `repositoryUsername` | no       | -             | Username to authenticate with when replicating user metadata into the triple store repository. |
| `repositoryPassword` | no       | -             | Password to authenticate with when replicating user metadata into the triple store repository. |
| `graphDBServerUrl`   | yes      | -             | URL of the GraphDB server on which user accounts corresponding to keycloak accounts need to be created. |
| `graphDBUsername`    | no       | -             | Username to authenticate with when creating user accounts on the GraphDB server. Note that the user needs to have admin privileges. |
| `graphDBPassword`    | no       | -             | Password to authenticate with when creating user accounts on the GraphDB server. |
