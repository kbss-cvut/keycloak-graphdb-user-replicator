package cz.cvut.kbss.keycloak.provider;

import cz.cvut.kbss.keycloak.provider.dao.GraphDBUserDao;
import cz.cvut.kbss.keycloak.provider.dao.UserAccountDao;
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

    private Repositories repositories;

    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        LOG.info("Creating EventListenerProvider.");
        if (repositories == null) {
            // Init persistence factory lazily to ensure the target db server is up and running
            this.repositories = PersistenceFactory.connect(configuration);
        }
        return new DataReplicationProvider(
                new KeycloakAdapter(keycloakSession.users(), keycloakSession.realms(), configuration),
                new UserAccountDao(repositories, configuration.getVocabulary(),
                                   configuration.getRepositoryLanguage()),
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
        if (repositories != null) {
            repositories.close();
        }
    }

    @Override
    public String getId() {
        return "keycloak-graphdb-user-replicator";
    }
}
