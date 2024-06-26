package cz.cvut.kbss.keycloak.provider.dao;

import cz.cvut.kbss.keycloak.provider.Repositories;
import cz.cvut.kbss.keycloak.provider.model.UserAccount;
import cz.cvut.kbss.keycloak.provider.model.Vocabulary;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAccountDaoTest {

    private static final ValueFactory vf = SimpleValueFactory.getInstance();

    private final Vocabulary vocabulary = new Vocabulary();

    @Mock
    private Repository repository;

    @Mock
    private RepositoryConnection connection;

    private Repositories repositories;

    private UserAccountDao sut;

    @BeforeEach
    void setUp() {
        this.repositories = new Repositories();
        repositories.add(repository);
        when(repository.getConnection()).thenReturn(connection);
        when(repository.getValueFactory()).thenReturn(vf);
        this.sut = new UserAccountDao(repositories, vocabulary, null);
    }

    @AfterEach
    void tearDown() {
        UserAccount.setContext(null);
    }

    @Test
    void persistGeneratesUserMetadataStatementsToDefaultContextWhenNoneIsSpecified() {
        final UserAccount user = initUserAccount();
        sut.persist(user);

        verifyBasicUserMetadataPersist(user, connection);
    }

    private void verifyBasicUserMetadataPersist(UserAccount user, RepositoryConnection connection) {
        final IRI subj = vf.createIRI(user.getUri().toString());
        verify(connection).add(vf.createStatement(subj, RDF.TYPE, vf.createIRI(vocabulary.getType())));
        verify(connection).add(vf.createStatement(subj, vf.createIRI(vocabulary.getFirstName()),
                                                  vf.createLiteral(user.getFirstName())));
        verify(connection).add(
                vf.createStatement(subj, vf.createIRI(vocabulary.getLastName()), vf.createLiteral(user.getLastName())));
        verify(connection).add(
                vf.createStatement(subj, vf.createIRI(vocabulary.getUsername()), vf.createLiteral(user.getUsername())));
    }

    private UserAccount initUserAccount() {
        final UserAccount user = new UserAccount();
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
        final UserAccount user = initUserAccount();
        UserAccount.setContext(context);

        sut.persist(user);
        final IRI subj = vf.createIRI(user.getUri().toString());
        verify(connection).add(vf.createStatement(subj, RDF.TYPE, vf.createIRI(vocabulary.getType())),
                               vf.createIRI(context));
        verify(connection).add(vf.createStatement(subj, vf.createIRI(vocabulary.getFirstName()),
                                                  vf.createLiteral(user.getFirstName())), vf.createIRI(context));
        verify(connection).add(
                vf.createStatement(subj, vf.createIRI(vocabulary.getLastName()), vf.createLiteral(user.getLastName())),
                vf.createIRI(context));
        verify(connection).add(
                vf.createStatement(subj, vf.createIRI(vocabulary.getUsername()), vf.createLiteral(user.getUsername())),
                vf.createIRI(context));
    }

    @Test
    void persistGeneratesEmailStatementWhenEmailPropertyIsConfigured() {
        final UserAccount user = initUserAccount();
        vocabulary.setEmail(FOAF.MBOX.stringValue());

        sut.persist(user);
        final IRI subj = vf.createIRI(user.getUri().toString());
        verifyBasicUserMetadataPersist(user, connection);
        verify(connection).add(
                vf.createStatement(subj, vf.createIRI(vocabulary.getEmail()), vf.createLiteral(user.getEmail())));
    }

    @Test
    void updateRemovesExistingUserMetadataStatementsAndPersistsNewData() {
        final UserAccount user = initUserAccount();

        sut.update(user);
        final IRI subj = vf.createIRI(user.getUri().toString());
        verify(connection).remove(subj, vf.createIRI(vocabulary.getFirstName()), null);
        verify(connection).remove(subj, vf.createIRI(vocabulary.getLastName()), null);
        verify(connection).remove(subj, vf.createIRI(vocabulary.getUsername()), null);
        verifyBasicUserMetadataPersist(user, connection);
    }

    @Test
    void updateRemovesEmailWhenItsPropertyIsConfigured() {
        final UserAccount user = initUserAccount();
        vocabulary.setEmail(FOAF.MBOX.stringValue());

        sut.update(user);
        final IRI subj = vf.createIRI(user.getUri().toString());
        verify(connection).remove(subj, vf.createIRI(vocabulary.getEmail()), null);
        verify(connection).add(
                vf.createStatement(subj, vf.createIRI(vocabulary.getEmail()), vf.createLiteral(user.getEmail())));
    }

    @Test
    void persistSavesStringLiteralsWithLanguageTagWhenItIsConfigured() {
        final String lang = "cs";
        vocabulary.setEmail(FOAF.MBOX.stringValue());
        this.sut = new UserAccountDao(repositories, vocabulary, lang);
        final UserAccount user = initUserAccount();
        sut.persist(user);

        final IRI subj = vf.createIRI(user.getUri().toString());
        verify(connection).add(vf.createStatement(subj, RDF.TYPE, vf.createIRI(vocabulary.getType())));
        verify(connection).add(vf.createStatement(subj, vf.createIRI(vocabulary.getFirstName()),
                                                  vf.createLiteral(user.getFirstName(), lang)));
        verify(connection).add(vf.createStatement(subj, vf.createIRI(vocabulary.getLastName()),
                                                  vf.createLiteral(user.getLastName(), lang)));
        verify(connection).add(vf.createStatement(subj, vf.createIRI(vocabulary.getUsername()),
                                                  vf.createLiteral(user.getUsername(), lang)));
        verify(connection).add(
                vf.createStatement(subj, vf.createIRI(vocabulary.getEmail()), vf.createLiteral(user.getEmail(), lang)));
    }

    @Test
    void persistSkipsNullUserAttributes() {
        final UserAccount user = new UserAccount();
        user.setUri(URI.create(vocabulary.getType() + "/" + UUID.randomUUID()));
        user.setUsername("username");
        sut.persist(user);

        final IRI subj = vf.createIRI(user.getUri().toString());
        verify(connection).add(vf.createStatement(subj, RDF.TYPE, vf.createIRI(vocabulary.getType())));
        verify(connection).add(
                vf.createStatement(subj, vf.createIRI(vocabulary.getUsername()), vf.createLiteral(user.getUsername())));
    }

    @Test
    void persistPersistsUserMetadataIntoAllConfiguredRepositories() {
        final Repository repositoryII = mock(Repository.class);
        final RepositoryConnection connectionII = mock(RepositoryConnection.class);
        when(repositoryII.getConnection()).thenReturn(connectionII);
        repositories.add(repositoryII);
        final UserAccount user = initUserAccount();

        sut.persist(user);
        verifyBasicUserMetadataPersist(user, connection);
        verifyBasicUserMetadataPersist(user, connectionII);
    }

    @Test
    void updateRemovesExistingUserMetadataStatementsAndPersistsNewDataInAllConfiguredRepositories() {
        final Repository repositoryII = mock(Repository.class);
        final RepositoryConnection connectionII = mock(RepositoryConnection.class);
        when(repositoryII.getConnection()).thenReturn(connectionII);
        repositories.add(repositoryII);
        final UserAccount user = initUserAccount();

        sut.update(user);
        final IRI subj = vf.createIRI(user.getUri().toString());
        verify(connection).remove(subj, vf.createIRI(vocabulary.getFirstName()), null);
        verify(connection).remove(subj, vf.createIRI(vocabulary.getLastName()), null);
        verify(connection).remove(subj, vf.createIRI(vocabulary.getUsername()), null);
        verifyBasicUserMetadataPersist(user, connection);
        verify(connectionII).remove(subj, vf.createIRI(vocabulary.getFirstName()), null);
        verify(connectionII).remove(subj, vf.createIRI(vocabulary.getLastName()), null);
        verify(connectionII).remove(subj, vf.createIRI(vocabulary.getUsername()), null);
        verifyBasicUserMetadataPersist(user, connectionII);
    }
}