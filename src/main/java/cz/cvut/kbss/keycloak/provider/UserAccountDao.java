package cz.cvut.kbss.keycloak.provider;

import cz.cvut.kbss.keycloak.provider.model.KodiUserAccount;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import java.util.Objects;

public class UserAccountDao {

    private final ValueFactory vf;

    private final RepositoryConnection connection;

    private final String language;

    public UserAccountDao(RepositoryConnection connection, String language) {
        this.connection = connection;
        this.language = language;
        this.vf = connection.getValueFactory();
    }

    public void persist(KodiUserAccount userAccount) {
        Objects.requireNonNull(userAccount);
        final IRI subject = vf.createIRI(userAccount.getUri().toString());
        connection.add(subject, RDF.TYPE, vf.createIRI(Vocabulary.s_i_uzivatel),
                vf.createIRI(KodiUserAccount.getContext()));
        connection.add(subject, vf.createIRI(Vocabulary.s_i_ma_krestni_jmeno),
                vf.createLiteral(userAccount.getFirstName(), language), vf.createIRI(KodiUserAccount.getContext()));
        connection.add(subject, vf.createIRI(Vocabulary.s_i_ma_prijmeni),
                vf.createLiteral(userAccount.getLastName(), language), vf.createIRI(KodiUserAccount.getContext()));
    }

    public void update(KodiUserAccount userAccount) {
        Objects.requireNonNull(userAccount);
        final IRI subject = vf.createIRI(userAccount.getUri().toString());
        connection.remove(connection.getStatements(subject, vf.createIRI(Vocabulary.s_i_ma_krestni_jmeno), null));
        connection.remove(connection.getStatements(subject, vf.createIRI(Vocabulary.s_i_ma_prijmeni), null));
        persist(userAccount);
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
