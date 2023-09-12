package cz.cvut.kbss.keycloak.provider;

import org.eclipse.rdf4j.repository.Repository;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataReplicationProviderFactory implements EventListenerProviderFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DataReplicationProviderFactory.class);

    private Configuration configuration;

    private Repository repository;

    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        LOG.info("Creating EventListenerProvider.");
        if (repository == null) {
            // Init persistence factory lazily, because GraphDB won't start until its OIDC provider (Keycloak) is available
            this.repository = PersistenceFactory.connect(configuration);
        }
        return new DataReplicationProvider(
                new KeycloakAdapter(keycloakSession.users(), keycloakSession.realms(), configuration),
                new UserAccountDao(repository.getConnection()),
                new GraphDBUserDao(configuration));
    }

    @Override
    public void init(Config.Scope scope) {
        LOG.info("Loading configuration from scope.");
        this.configuration = new Configuration(scope);
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
    }

    @Override
    public void close() {
        if (repository != null) {
            repository.shutDown();
        }
    }

    @Override
    public String getId() {
        return "keycloak-graphdb-user-replicator";
    }
}
