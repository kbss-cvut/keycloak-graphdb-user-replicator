package cz.cvut.kbss.keycloak.provider.dao;

import cz.cvut.kbss.keycloak.provider.model.KodiUserAccount;
import cz.cvut.kbss.keycloak.provider.model.Vocabulary;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UserAccountDao {

    private final ValueFactory vf;

    private final RepositoryConnection connection;

    private final Vocabulary vocabulary;

    public UserAccountDao(RepositoryConnection connection, Vocabulary vocabulary) {
        this.connection = connection;
        this.vf = connection.getValueFactory();
        this.vocabulary = vocabulary;
    }

    public void persist(KodiUserAccount userAccount) {
        Objects.requireNonNull(userAccount);
        connection.begin();
        persistInTransaction(userAccount);
        connection.commit();
    }

    private void persistInTransaction(KodiUserAccount userAccount) {
        if (Objects.isNull(KodiUserAccount.getContext()) || KodiUserAccount.getContext().isEmpty()) {
            generateUserMetadataStatements(userAccount).forEach(connection::add);
        } else {
            generateUserMetadataStatements(userAccount).forEach(
                    s -> connection.add(s, vf.createIRI(KodiUserAccount.getContext())));
        }
    }

    private List<Statement> generateUserMetadataStatements(KodiUserAccount userAccount) {
        final IRI subject = vf.createIRI(userAccount.getUri().toString());
        final List<Statement> statements = new ArrayList<>(Arrays.asList(
                vf.createStatement(subject, RDF.TYPE, vf.createIRI(vocabulary.getType())),
                vf.createStatement(subject, vf.createIRI(vocabulary.getFirstName()),
                                   vf.createLiteral(userAccount.getFirstName())),
                vf.createStatement(subject, vf.createIRI(vocabulary.getLastName()),
                                   vf.createLiteral(userAccount.getLastName())),
                vf.createStatement(subject, vf.createIRI(vocabulary.getUsername()),
                                   vf.createLiteral(userAccount.getUsername()))
        ));
        if (vocabulary.getEmail() != null) {
            statements.add(vf.createStatement(subject, vf.createIRI(vocabulary.getEmail()),
                                              vf.createLiteral(userAccount.getEmail())));
        }
        return statements;
    }

    public void update(KodiUserAccount userAccount) {
        Objects.requireNonNull(userAccount);
        connection.begin();
        final IRI subject = vf.createIRI(userAccount.getUri().toString());
        connection.remove(subject, vf.createIRI(vocabulary.getFirstName()), null);
        connection.remove(subject, vf.createIRI(vocabulary.getLastName()), null);
        connection.remove(subject, vf.createIRI(vocabulary.getUsername()), null);
        if (vocabulary.getEmail() != null) {
            connection.remove(subject, vf.createIRI(vocabulary.getEmail()), null);
        }
        persistInTransaction(userAccount);
        connection.commit();
    }

    public void close() {
        connection.close();
    }
}
