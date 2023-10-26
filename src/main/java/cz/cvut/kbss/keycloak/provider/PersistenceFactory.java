package cz.cvut.kbss.keycloak.provider;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PersistenceFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PersistenceFactory.class);

    static Repository connect(Configuration configuration) {
        LOG.info("Initializing connection to repository {}.", configuration.getRepositoryId());
        final String url = configuration.getDbServerUrl() + "/repositories/" + configuration.getRepositoryId();
        return connectToRemoteRepository(url, configuration);
    }

    private static Repository connectToRemoteRepository(String repoUri, Configuration configuration) {
        final RepositoryManager manager = RepositoryProvider.getRepositoryManagerOfRepository(repoUri);
        final RemoteRepositoryManager remoteManager = (RemoteRepositoryManager) manager;
        final String username = configuration.getRepositoryUsername();
        if (username != null) {
            final String password = configuration.getRepositoryPassword();
            remoteManager.setUsernameAndPassword(username, password);
        }
        return manager.getRepository(configuration.getRepositoryId());
    }
}
