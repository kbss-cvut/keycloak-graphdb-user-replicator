# Keycloak GraphDB User Replicator 
[![Build docker](https://github.com/datagov-cz/keycloak-graphdb-user-replicator/actions/workflows/docker-build-and-upload.yml/badge.svg)](https://github.com/datagov-cz/keycloak-graphdb-user-replicator/actions/workflows/docker-build-and-upload.yml)

Ensures user data are replicated into a GraphDB instance for the purpose of data provenance display. Another feature is
creating GraphDB users corresponding to Keycloak users so that applications authenticated via Keycloak can access
protected GraphDB repositories.

## Compatibility

This service provider is compatible with Keycloak 18 and later. Previous versions were powered by JBoss and had a different
directory structure as well as means of deploying service providers. Since version 18, Keycloak is powered by Quarkus.

This service provider is also compatible with GraphDB 10 and later. Previous GraphDB versions used RDF4J 3, GraphDB 10 uses
RDF4J 4 which is not compatible with the previous version due to changes in the binary repository connection protocol.

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
2. Copy target JAR into `$KEYCLOAK_HOME/providers`
3. Provide required configuration (see below) using [one of the supported ways](https://www.keycloak.org/server/configuration).
4. Start Keycloak
5. Go to the `Realm settings` of the relevant realm, open the `Events` tab and add `keycloak-graphdb-user-replicator` to the `Event listeners`

### Configuration

The following configuration parameters can (and in some cases must) be provided as environmental variables

| Parameter                 | Required | Default value                                  | Description                                                                                                                                                            |
|---------------------------|----------|------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `COMPONENTS`              | no       | -                                              | Base64 encoded configuration of DB_SERVER_REPOSITORY_ID, DB_SERVER_URL and REALM_ID through common assembly line configuration.                                        |
| `REALM_ID`                | yes      | -                                              | Identifier of the realm for which events should be processed.                                                                                                          |
| `DB_SERVER_URL`           | yes      | -                                              | URL of the GraphDB server on which user accounts corresponding to keycloak accounts need to be created.                                                                |
| `DB_SERVER_REPOSITORY_ID` | yes      | -                                              | Identifier of the repository into which basic user metadata should be replicated by this SPI. Repository URL will be resolved based on GraphDB server URL and this id. |
| `REPOSITORY_USERNAME`     | no       | -                                              | Username to authenticate with when replicating user metadata into the triple store repository and into the GraphDB user database.                                      |
| `REPOSITORY_PASSWORD`     | no       | -                                              | Password to authenticate with when replicating user metadata into the triple store repository and into the GraphDB user database.                                      |
| `DB_SERVER_CONTEXT`       | no       | -                                              | Identifier of named graph into which user account metadata will be saved.                                                                                              |
| `NAMESPACE`               | no       | `http://onto.fel.cvut.cz/ontologies/uzivatel/` | Namespace for generating user identifiers.                                                                                                                             |

Note that the GraphDB user (identifier by `repositoryUsername` and `repositoryPassword`) has to be an admin, 
so that it can add new users into the user database.


## License

Licensed under LGPL v3.0.

Tento repozitář je udržován v rámci projektu OPZ č. CZ.03.4.74/0.0/0.0/15_025/0013983.
![Evropská unie - Evropský sociální fond - Operační program Zaměstnanost](https://data.gov.cz/images/ozp_logo_cz.jpg)
