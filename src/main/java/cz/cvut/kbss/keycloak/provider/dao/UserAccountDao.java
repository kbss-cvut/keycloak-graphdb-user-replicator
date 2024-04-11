package cz.cvut.kbss.keycloak.provider.dao;

import cz.cvut.kbss.keycloak.provider.Repositories;
import cz.cvut.kbss.keycloak.provider.model.UserAccount;
import cz.cvut.kbss.keycloak.provider.model.Vocabulary;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class UserAccountDao {

    private final ValueFactory vf;

    private final Repositories repositories;

    private final Vocabulary vocabulary;

    private final String repoLang;

    public UserAccountDao(Repositories repositories, Vocabulary vocabulary, String repoLang) {
        this.repositories = repositories;
        this.vf = repositories.getValueFactory();
        this.vocabulary = vocabulary;
        this.repoLang = repoLang;
    }

    public void persist(UserAccount userAccount) {
        Objects.requireNonNull(userAccount);
        repositories.execute(conn -> persistInTransaction(userAccount, conn));
    }

    private void persistInTransaction(UserAccount userAccount, RepositoryConnection connection) {
        if (Objects.isNull(UserAccount.getContext()) || UserAccount.getContext().isEmpty()) {
            generateUserMetadataStatements(userAccount).forEach(connection::add);
        } else {
            generateUserMetadataStatements(userAccount).forEach(
                    s -> connection.add(s, vf.createIRI(UserAccount.getContext())));
        }
    }

    private List<Statement> generateUserMetadataStatements(UserAccount userAccount) {
        final IRI subject = vf.createIRI(userAccount.getUri().toString());
        assert userAccount.getUsername() != null;
        final List<Statement> statements = new ArrayList<>(Arrays.asList(
                vf.createStatement(subject, RDF.TYPE, vf.createIRI(vocabulary.getType())),
                vf.createStatement(subject, vf.createIRI(vocabulary.getUsername()),
                                   stringLiteral(userAccount.getUsername()))
        ));
        createOptionalStatement(subject, vocabulary.getFirstName(),
                                userAccount::getFirstName).ifPresent(statements::add);
        createOptionalStatement(subject, vocabulary.getLastName(),
                                userAccount::getLastName).ifPresent(statements::add);
        if (vocabulary.getEmail() != null) {
            createOptionalStatement(subject, vocabulary.getEmail(),
                                    userAccount::getEmail).ifPresent(statements::add);
        }
        return statements;
    }

    private Optional<Statement> createOptionalStatement(IRI subject, String property, Supplier<String> getter) {
        final String propertyValue = getter.get();
        if (propertyValue == null) {
            return Optional.empty();
        }
        return Optional.of(vf.createStatement(subject, vf.createIRI(property), stringLiteral(propertyValue)));
    }

    private Literal stringLiteral(String value) {
        if (repoLang != null) {
            return vf.createLiteral(value, repoLang);
        } else {
            return vf.createLiteral(value);
        }
    }

    public void update(UserAccount userAccount) {
        Objects.requireNonNull(userAccount);
        repositories.execute(conn -> {
            final IRI subject = vf.createIRI(userAccount.getUri().toString());
            conn.remove(subject, vf.createIRI(vocabulary.getFirstName()), null);
            conn.remove(subject, vf.createIRI(vocabulary.getLastName()), null);
            conn.remove(subject, vf.createIRI(vocabulary.getUsername()), null);
            if (vocabulary.getEmail() != null) {
                conn.remove(subject, vf.createIRI(vocabulary.getEmail()), null);
            }
            persistInTransaction(userAccount, conn);
        });
    }
}
