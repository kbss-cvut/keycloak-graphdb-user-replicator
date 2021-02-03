package cz.cvut.kbss.keycloak.provider;

import org.eclipse.rdf4j.repository.RepositoryConnection;

import java.util.Objects;

public class UserAccountDao {

    private final RepositoryConnection connection;

    public UserAccountDao(RepositoryConnection connection) {
        this.connection = connection;
    }

    public void persist(KodiUserAccount userAccount) {
        Objects.requireNonNull(userAccount);
        // TODO
    }

    public void update(KodiUserAccount userAccount) {
        Objects.requireNonNull(userAccount);
        // TODO
    }

    public void transactional(Runnable procedure) {
        connection.begin();
        procedure.run();
        connection.commit();
    }

    public void close() {
        connection.close();
    }
}
