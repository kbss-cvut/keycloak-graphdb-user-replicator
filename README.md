# Keycloak GraphDB User Replicator 
[![Build docker](https://github.com/opendata-mvcr/keycloak-graphdb-user-replicator/actions/workflows/docker-build-and-upload.yml/badge.svg)](https://github.com/opendata-mvcr/keycloak-graphdb-user-replicator/actions/workflows/docker-build-and-upload.yml)

Ensures user data are replicated into a GraphDB instance for the purpose of data provenance display. Another feature is
creating GraphDB users corresponding to Keycloak users so that applications authenticated via Keycloak can access
protected GraphDB repositories.

## Implementation notes

This project is a Keycloak event listener Service Provider Interface (SPI) implementation. It receives event notifications
from Keycloak and handles selected user-related ones. More specifically, the following events are processed:

* User registration and creation
* User profile or email update

1. When a user account is created, corresponding user metadata are generated into the configured GraphDB repository. These metadata
consist of user classification and first and last name property values. In addition, a new user is created in the GraphDB user database
so that Keycloak authenticated users can access protected GraphDB repositories. The GraphDB users are without password, so they can be used
   only via Keycloak.
   
2. When a user account is updated, corresponding user metadata are updated in the configured GraphDB repository. If the user's email
has changed, a new matching user account is created in the GraphDB user database as well.
   
Note that due to the nature of the Keycloak events received by the SPI, it is not possible to update or remove GraphDB users. The
original user email is not available in the event, so the corresponding GraphDB user cannot be found and disposed of. However, since
these GraphDB users are accessible solely through Keycloak, the become effectively inaccessible and thus should not represent a security
risk.
If needed, a regular sweep of obsolete user accounts can be done in GraphDB.


## Setup

1. Build project
2. Add the following XML snippet into `$KEYCLOAK_HOME/standalone/configuration/standalone.xml` under the tag
   `<subsystem xmlns="urn:jboss:domain:keycloak-server:1.1">`

```xml

<spi name="eventsListener">
    <provider name="keycloak-graphdb-user-replicator" enabled="true">
        <properties>
            <property name="${property}" value="${value}"/>
        </properties>
    </provider>
</spi>
```

Property values should be configured as necessary. The SPI will then load them on startup.

3. Copy target JAR into `$KEYCLOAK_HOME/standalone/deployments`

### Configuration

The following configuration parameters can (and in some cases must) be provided as environmental variables

| Parameter            | Required | Default value | Description |
| -------------------- | -------- | ------------- | ----------- |
| `COMPONENTS`         | no       | -             | Base64 encoded configuration of DB_SERVER_REPOSITORY_ID, DB_SERVER_URL and REALM_ID through common assembly line configuration|
| `REALM_ID`           | yes      | -             | Identifier of the realm for which events should be processed. |
| `DB_SERVER_URL`      | yes      | -             | URL of the GraphDB server on which user accounts corresponding to keycloak accounts need to be created. |
| `DB_SERVER_REPOSITORY_ID`| yes      | -             | Identifier of the repository into which basic user metadata should be replicated by this SPI. Repository URL will be resolved based on GraphDB server URL and this id. |
| `REPOSITORY_USERNAME`| no       | -             | Username to authenticate with when replicating user metadata into the triple store repository and into the GraphDB user database. |
| `REPOSITORY_PASSWORD`| no       | -             | Password to authenticate with when replicating user metadata into the triple store repository and into the GraphDB user database. |
| `DB_SERVER_CONTEXT`  | no       | -             | Identifier of named graph into which user account metadata will be saved. |
| `NAMESPACE`          | no       | `http://onto.fel.cvut.cz/ontologies/uzivatel/` | Namespace for generating user identifiers. |

Note that the GraphDB user (identifier by `repositoryUsername` and `repositoryPassword`) has to be an admin, 
so that it can add new users into the user database.


## License

Licensed under LGPL v3.0.

Tento repozitář je udržován v rámci projektu OPZ č. CZ.03.4.74/0.0/0.0/15_025/0013983.
![Evropská unie - Evropský sociální fond - Operační program Zaměstnanost](https://data.gov.cz/images/ozp_logo_cz.jpg)
