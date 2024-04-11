package cz.cvut.kbss.keycloak.provider;

import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Repositories {

    private final List<Repository> repositories = new ArrayList<>();

    public void add(Repository repository) {
        repositories.add(repository);
    }

    public void execute(Consumer<RepositoryConnection> executor) {
        repositories.forEach(r -> {
            try (final RepositoryConnection conn = r.getConnection()) {
                conn.begin();
                executor.accept(conn);
                conn.commit();
            }
        });
    }

    public void close() {
        repositories.forEach(Repository::shutDown);
    }

    public ValueFactory getValueFactory() {
        assert !repositories.isEmpty();
        return repositories.get(0).getValueFactory();
    }
}
