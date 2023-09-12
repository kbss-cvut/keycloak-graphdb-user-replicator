FROM maven:3-eclipse-temurin-17 as provider-builder

COPY pom.xml pom.xml

RUN mvn -B de.qaware.maven:go-offline-maven-plugin:resolve-dependencies

COPY src src

RUN mvn package -B

FROM quay.io/keycloak/keycloak:22.0 as kc-builder

ENV KEYCLOAK_HOME=/opt/keycloak
COPY --from=provider-builder target/keycloak-graphdb-user-replicator.jar ${KEYCLOAK_HOME}/providers/
RUN ${KEYCLOAK_HOME}/bin/kc.sh build

FROM quay.io/keycloak/keycloak:22.0

COPY --from=kc-builder /opt/keycloak /opt/keycloak

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
