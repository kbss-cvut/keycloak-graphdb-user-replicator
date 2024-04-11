package cz.cvut.kbss.keycloak.provider;

import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PersistenceFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PersistenceFactory.class);

    static Repositories connect(Configuration configuration) {
        final Repositories repositories = new Repositories();
        final RepositoryManager repositoryManager = connectToRepositoryServer(configuration);
        configuration.getRepositoryIds().forEach(repoId -> {
            LOG.info("Connecting to repository {}.", repoId);
            repositories.add(repositoryManager.getRepository(repoId));
        });
        return repositories;
    }

    private static RepositoryManager connectToRepositoryServer(Configuration configuration) {
        LOG.info("Initializing connection to repository server {}.", configuration.getDbServerUrl());
        final RepositoryManager manager = RepositoryProvider.getRepositoryManager(configuration.getDbServerUrl());
        final RemoteRepositoryManager remoteManager = (RemoteRepositoryManager) manager;
        final String username = configuration.getRepositoryUsername();
        if (username != null) {
            final String password = configuration.getRepositoryPassword();
            remoteManager.setUsernameAndPassword(username, password);
        }
        return manager;
    }
}
