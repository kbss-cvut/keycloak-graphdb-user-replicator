package cz.cvut.kbss.keycloak.provider.dao;

import cz.cvut.kbss.keycloak.provider.model.KodiUserAccount;
import cz.cvut.kbss.keycloak.provider.model.Vocabulary;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAccountDaoTest {

    private static final ValueFactory vf = SimpleValueFactory.getInstance();

    private final Vocabulary vocabulary = new Vocabulary();

    @Mock
    private RepositoryConnection connection;

    private UserAccountDao sut;

    @BeforeEach
    void setUp() {
        when(connection.getValueFactory()).thenReturn(vf);
        this.sut = new UserAccountDao(connection, vocabulary);
    }

    @AfterEach
    void tearDown() {
        KodiUserAccount.setContext(null);
    }

    @Test
    void persistGeneratesUserMetadataStatementsToDefaultContextWhenNoneIsSpecified() {
        final KodiUserAccount user = initUserAccount();
        sut.persist(user);

        verifyBasicUserMetadataPersist(user);
    }

    private void verifyBasicUserMetadataPersist(KodiUserAccount user) {
        final IRI subj = vf.createIRI(user.getUri().toString());
        verify(connection).add(vf.createStatement(subj, RDF.TYPE, vf.createIRI(vocabulary.getType())));
        verify(connection).add(vf.createStatement(subj, vf.createIRI(vocabulary.getFirstName()), vf.createLiteral(user.getFirstName())));
        verify(connection).add(vf.createStatement(subj, vf.createIRI(vocabulary.getLastName()), vf.createLiteral(user.getLastName())));
        verify(connection).add(vf.createStatement(subj, vf.createIRI(vocabulary.getUsername()), vf.createLiteral(user.getUsername())));
    }

    private KodiUserAccount initUserAccount() {
        final KodiUserAccount user = new KodiUserAccount();
        user.setUri(URI.create(vocabulary.getType() + "/" + UUID.randomUUID()));
        user.setFirstName("First");
        user.setLastName("Last");
        user.setUsername("username");
        user.setEmail("First.Last@example.org");
        return user;
    }

    @Test
    void persistGeneratesUserMetadataStatementsToContextWhenItIsConfigured() {
        final String context = cz.cvut.kbss.keycloak.provider.Vocabulary.s_i_uzivatel;
        final KodiUserAccount user = initUserAccount();
        KodiUserAccount.setContext(context);

        sut.persist(user);
        final IRI subj = vf.createIRI(user.getUri().toString());
        verify(connection).add(vf.createStatement(subj, RDF.TYPE, vf.createIRI(vocabulary.getType())), vf.createIRI(context));
        verify(connection).add(vf.createStatement(subj, vf.createIRI(vocabulary.getFirstName()), vf.createLiteral(user.getFirstName())), vf.createIRI(context));
        verify(connection).add(vf.createStatement(subj, vf.createIRI(vocabulary.getLastName()), vf.createLiteral(user.getLastName())), vf.createIRI(context));
        verify(connection).add(vf.createStatement(subj, vf.createIRI(vocabulary.getUsername()), vf.createLiteral(user.getUsername())), vf.createIRI(context));
    }

    @Test
    void persistGeneratesEmailStatementWhenEmailPropertyIsConfigured() {
        final KodiUserAccount user = initUserAccount();
        vocabulary.setEmail(FOAF.MBOX.stringValue());

        sut.persist(user);
        final IRI subj = vf.createIRI(user.getUri().toString());
        verifyBasicUserMetadataPersist(user);
        verify(connection).add(vf.createStatement(subj, vf.createIRI(vocabulary.getEmail()), vf.createLiteral(user.getEmail())));
    }

    @Test
    void updateRemovesExistingUserMetadataStatementsAndPersistsNewData() {
        final KodiUserAccount user = initUserAccount();

        sut.update(user);
        final IRI subj = vf.createIRI(user.getUri().toString());
        verify(connection).remove(subj, vf.createIRI(vocabulary.getFirstName()), null);
        verify(connection).remove(subj, vf.createIRI(vocabulary.getLastName()), null);
        verify(connection).remove(subj, vf.createIRI(vocabulary.getUsername()), null);
        verifyBasicUserMetadataPersist(user);
    }

    @Test
    void updateRemovesEmailWhenItsPropertyIsConfigured() {
        final KodiUserAccount user = initUserAccount();
        vocabulary.setEmail(FOAF.MBOX.stringValue());

        sut.update(user);
        final IRI subj = vf.createIRI(user.getUri().toString());
        verify(connection).remove(subj, vf.createIRI(vocabulary.getEmail()), null);
        verify(connection).add(vf.createStatement(subj, vf.createIRI(vocabulary.getEmail()), vf.createLiteral(user.getEmail())));
    }
}