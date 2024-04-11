package cz.cvut.kbss.keycloak.provider;

import cz.cvut.kbss.keycloak.provider.model.UserAccount;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import cz.cvut.kbss.keycloak.provider.model.Vocabulary;
import org.keycloak.Config;
import org.yaml.snakeyaml.Yaml;

public class Configuration {

    private final String realmId;

    private final List<String> repositoryIds;

    private final String repositoryUsername;

    private final String repositoryPassword;

    private final String repositoryLanguage;

    private final String dbServerUrl;

    private final Vocabulary vocabulary;

    private final boolean addAccounts;

    Configuration(Config.Scope scope) {
        final String components = getProperty("COMPONENTS");
        if (isNullOrEmpty(getProperty("DB_SERVER_URL"))
            || isNullOrEmpty(getProperty("DB_SERVER_REPOSITORY_ID"))
            || isNullOrEmpty(getProperty("REALM_ID"))) {
            if (components == null) {
                throw new RuntimeException("Environmental variable 'COMPONENTS' "
                    + "or DB_SERVER_URL and DB_SERVER_REPOSITORY_ID must be set.");
            }
            final Map<String, Object> dbServer =
                (Map<String, Object>) parseComponents(components).get("al-db-server");
            final GraphDbUrlParser parser = new GraphDbUrlParser(dbServer.get("url").toString());
            this.dbServerUrl = parser.getGraphdbUrl();
            this.repositoryIds = parser.getRepositoryIds();
            final Map<String, Object> authServer =
                (Map<String, Object>) parseComponents(components).get("al-auth-server");
            final AuthServerParser aParser = new AuthServerParser(authServer.get("url").toString());
            this.realmId = aParser.getRealmId();
        } else {
            this.dbServerUrl = getProperty("DB_SERVER_URL");
            this.repositoryIds = Arrays.asList(getProperty("DB_SERVER_REPOSITORY_ID").split(","));
            this.realmId = getProperty("REALM_ID");
        }
        this.repositoryUsername = getProperty("REPOSITORY_USERNAME");
        this.repositoryPassword = getProperty("REPOSITORY_PASSWORD");
        this.repositoryLanguage = getOptionalProperty("REPOSITORY_LANGUAGE").orElse(null);
        this.addAccounts = getBooleanProperty("ADD_ACCOUNTS", true);
        UserAccount.setNamespace(getProperty("NAMESPACE"));
        UserAccount.setContext(getProperty("DB_SERVER_CONTEXT"));
        this.vocabulary = initVocabulary();
    }

    private static boolean isNullOrEmpty(final String nullOrEmpty) {
        return Objects.isNull(nullOrEmpty) || nullOrEmpty.isEmpty();
    }

    private static String getProperty(String key) {
        return System.getenv(key);
    }

    private static Optional<String> getOptionalProperty(String key) {
        return Optional.ofNullable(System.getenv(key));
    }

    private static Map<String, Object> parseComponents(String components) {
        final String componentsDecoded = new String(Base64.getDecoder().decode(components));
        return new Yaml().load(componentsDecoded);
    }

    private static boolean getBooleanProperty(String key, boolean defaultValue) {
        final Optional<String> value = getOptionalProperty(key);
        return value.map(Boolean::parseBoolean).orElse(defaultValue);
    }

    private static Vocabulary initVocabulary() {
        final Vocabulary vocabulary = new Vocabulary();
        getOptionalProperty("VOCABULARY_USER_TYPE").ifPresent(vocabulary::setType);
        getOptionalProperty("VOCABULARY_USER_FIRST_NAME").ifPresent(vocabulary::setFirstName);
        getOptionalProperty("VOCABULARY_USER_LAST_NAME").ifPresent(vocabulary::setLastName);
        getOptionalProperty("VOCABULARY_USER_USERNAME").ifPresent(vocabulary::setUsername);
        getOptionalProperty("VOCABULARY_USER_EMAIL").ifPresent(vocabulary::setEmail);
        return vocabulary;
    }

    public String getRealmId() {
        return realmId;
    }

    public List<String> getRepositoryIds() {
        return repositoryIds;
    }

    public String getRepositoryUsername() {
        return repositoryUsername;
    }

    public String getRepositoryPassword() {
        return repositoryPassword;
    }

    public String getRepositoryLanguage() {
        return repositoryLanguage;
    }

    public String getDbServerUrl() {
        return dbServerUrl;
    }

    public boolean shouldAddAccounts() {
        return addAccounts;
    }

    public Vocabulary getVocabulary() {
        return vocabulary;
    }
}
