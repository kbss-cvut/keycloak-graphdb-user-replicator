package cz.cvut.kbss.keycloak.provider;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataReplicationProviderFactory implements EventListenerProviderFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DataReplicationProviderFactory.class);

    private final Configuration configuration = new Configuration();

    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        return new DataReplicationProvider(keycloakSession.users(), keycloakSession.realms(), configuration);
    }

    @Override
    public void init(Config.Scope scope) {
        LOG.debug("Loading configuration from scope.");
        KodiUserAccount.setNamespace(scope.get("namespace"));
        configuration.setRealmId(scope.get("realmId"));
        configuration.setRepositoryUrl(scope.get("repositoryUrl"));
        configuration.setRepositoryUsername(scope.get("repositoryUsername"));
        configuration.setRepositoryPassword(scope.get("repositoryPassword"));
        configuration.setGraphDBServerUrl(scope.get("graphDBServerUrl"));
        configuration.setGraphDBUsername(scope.get("graphDBUsername"));
        configuration.setGraphDBPassword(scope.get("graphDBPassword"));
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
    }

    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return "keycloak-graphdb-user-replicator";
    }
}
