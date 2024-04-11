# Keycloak GraphDB User Replicator

[![Build docker](https://github.com/kbss-cvut/keycloak-graphdb-user-replicator/actions/workflows/build-docker.yml/badge.svg)](https://github.com/kbss-cvut/keycloak-graphdb-user-replicator/actions/workflows/build-docker.yml)

Replicates basic user data from Keycloak into a GraphDB repository for the purpose of data provenance display.
Another (optional) feature is creating GraphDB users corresponding to Keycloak users so that applications authenticated
via Keycloak can access protected GraphDB repositories.

## Compatibility

This service provider is compatible with Keycloak 18 and later. Previous Keycloak versions were powered by JBoss and had
a different
directory structure as well as means of deploying service providers. Since version 18, Keycloak is powered by Quarkus.

This service provider is also compatible with GraphDB 10 and later. Previous GraphDB versions used RDF4J 3, GraphDB 10
uses
RDF4J 4 which is not compatible with the previous version due to changes in the binary repository connection protocol.

## Implementation notes

This project is a Keycloak event listener Service Provider Interface (SPI) implementation. It receives event
notifications
from Keycloak and handles selected user-related ones. More specifically, the following events are processed:

* User registration and creation
* User profile or email update

1. When a user account is created, corresponding user metadata are generated into the configured GraphDB repository.
   These metadata
   consist of user classification and first and last name property values. In addition, a new user account can be
   created in the GraphDB user database
   so that Keycloak authenticated users can access protected GraphDB repositories. Such user accounts are without
   password, so they can be used
   only via Keycloak.

2. When a user account is updated, corresponding user metadata are updated in the configured GraphDB repository. If the
   user's email
   has changed, a new matching user account is created in the GraphDB user database as well (if this feature is
   enabled).

Note that due to the nature of the Keycloak events received by the SPI, it is not possible to update or remove GraphDB
users. The
original user email is not available in the event, so the corresponding GraphDB user cannot be found and disposed of.
However, since
these GraphDB users are accessible solely through Keycloak, the become effectively inaccessible and thus should not
represent a security
risk.
If needed, a regular sweep of obsolete user accounts can be done in GraphDB.

## Setup

To use the plugin directly in a Keycloak instance, follow these steps:

1. Build project
2. Copy target JAR into `$KEYCLOAK_HOME/providers`
3. Provide required configuration (see below)
   using [one of the supported ways](https://www.keycloak.org/server/configuration).
4. Start Keycloak
5. Go to the `Realm settings` of the relevant realm, open the `Events` tab and add `keycloak-graphdb-user-replicator` to
   the `Event listeners`

In a containerized environment, you can use the Docker image which includes Keycloak and this plugin:

1. Use the Docker image `ghcr.io/kbss-cvut/keycloak-graphdb-user-replicator/keycloak-graphdb:latest`
2. Provide required configuration (see below)
   using [one of the supported ways](https://www.keycloak.org/server/configuration).
3. Start the container/service
4. Go to the `Realm settings` of the relevant realm, open the `Events` tab and add `keycloak-graphdb-user-replicator` to
   the `Event listeners`

### Configuration

The following configuration parameters can (and in some cases must) be provided as environmental variables

| Parameter                    | Required | Default value                                                                              | Description                                                                                                                                                                                                            |
|------------------------------|----------|--------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `COMPONENTS`                 | no       | -                                                                                          | Base64 encoded configuration of DB_SERVER_REPOSITORY_ID, DB_SERVER_URL and REALM_ID through common assembly line configuration.                                                                                        |
| `REALM_ID`                   | yes      | -                                                                                          | Identifier of the realm for which events should be processed.                                                                                                                                                          |
| `DB_SERVER_URL`              | yes      | -                                                                                          | URL of the GraphDB server on which user accounts corresponding to keycloak accounts need to be created.                                                                                                                |
| `DB_SERVER_REPOSITORY_ID`    | yes      | -                                                                                          | Identifier of the repository into which basic user metadata should be replicated by this SPI. Repository URL will be resolved based on GraphDB server URL and this id. Multiple values are supported, separate by `,`. |
| `REPOSITORY_USERNAME`        | no       | -                                                                                          | Username to authenticate with when replicating user metadata into the triple store repository and into the GraphDB user database.                                                                                      |
| `REPOSITORY_PASSWORD`        | no       | -                                                                                          | Password to authenticate with when replicating user metadata into the triple store repository and into the GraphDB user database.                                                                                      |
| `REPOSITORY_LANGUAGE`        | no       | -                                                                                          | Language tag added to string literals representing user metadata. Defaults to nothing, meaning the literals are saved as `xsd:string`                                                                                  |
| `DB_SERVER_CONTEXT`          | no       | -                                                                                          | Identifier of named graph into which user account metadata will be saved.                                                                                                                                              |
| `NAMESPACE`                  | no       | `http://onto.fel.cvut.cz/ontologies/uzivatel/`                                             | Namespace for generating user identifiers.                                                                                                                                                                             |
| `ADD_ACCOUNTS`               | no       | `true`                                                                                     | Allows disabling replication of user accounts to GraphDB's user database for deployments where such a functionality is not required.                                                                                   |
| `VOCABULARY_USER_TYPE`       | no       | `http://onto.fel.cvut.cz/ontologies/slovník/agendový/popis-dat/pojem/uživatel`             | Type to assign the generated user instance in the repository.                                                                                                                                                          |
| `VOCABULARY_USER_FIRST_NAME` | no       | `http://onto.fel.cvut.cz/ontologies/slovník/agendový/popis-dat/pojem/má-křestní-jméno`     | Property used to represent the first name of the generated user instance in the repository.                                                                                                                            |
| `VOCABULARY_USER_LAST_NAME`  | no       | `http://onto.fel.cvut.cz/ontologies/slovník/agendový/popis-dat/pojem/má-příjmení`          | Property used to represent the last name of the generated user instance in the repository.                                                                                                                             |
| `VOCABULARY_USER_USERNAME`   | no       | `http://onto.fel.cvut.cz/ontologies/slovník/agendový/popis-dat/pojem/má-uživatelské-jméno` | Property used to represent the username of the generated user instance in the repository.                                                                                                                              |
| `VOCABULARY_USER_EMAIL`      | no       | -                                                                                          | Property used to represent the email of the generated user instance in the repository. If not specified, the email is not replicated into the repository.                                                              |

Note that the GraphDB user (identifier by `REPOSITORY_USERNAME` and `REPOSITORY_PASSWORD`) has to be an admin,
so that it can add new users into the user database (if this feature is enabled).

## License

Licensed under LGPL v3.0.
