package cz.cvut.kbss.keycloak.provider;

import cz.cvut.kbss.keycloak.provider.model.KodiUserAccount;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import org.keycloak.Config;
import org.yaml.snakeyaml.Yaml;

public class Configuration {

    private final String realmId;

    private final String repositoryId;

    private final String repositoryUsername;

    private final String repositoryPassword;

    private final String graphDBServerUrl;

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
            this.graphDBServerUrl = parser.getGraphdbUrl();
            this.repositoryId = parser.getRepositoryId();
            final Map<String, Object> authServer =
                (Map<String, Object>) parseComponents(components).get("al-auth-server");
            final AuthServerParser aParser = new AuthServerParser(authServer.get("url").toString());
            this.realmId = aParser.getRealmId();
        } else {
            this.graphDBServerUrl = getProperty("DB_SERVER_URL");
            this.repositoryId = getProperty("DB_SERVER_REPOSITORY_ID");
            this.realmId = getProperty("REALM_ID");
        }
        this.repositoryUsername = getProperty("REPOSITORY_USERNAME");
        this.repositoryPassword = getProperty("REPOSITORY_PASSWORD");
        this.addAccounts = getBooleanProperty("ADD_ACCOUNTS", true);
        KodiUserAccount.setNamespace(getProperty("NAMESPACE"));
        KodiUserAccount.setContext(getProperty("DB_SERVER_CONTEXT"));
    }

    private static boolean isNullOrEmpty(final String nullOrEmpty) {
        return Objects.isNull(nullOrEmpty) || nullOrEmpty.isEmpty();
    }

    private static String getProperty(String key) {
        return System.getenv(key);
    }

    private static Map<String, Object> parseComponents(String components) {
        final String componentsDecoded = new String(Base64.getDecoder().decode(components));
        return new Yaml().load(componentsDecoded);
    }

    private static boolean getBooleanProperty(String key, boolean defaultValue) {
        final String value = getProperty(key);
        return isNullOrEmpty(value) ? defaultValue : Boolean.parseBoolean(value);
    }

    public String getRealmId() {
        return realmId;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public String getRepositoryUsername() {
        return repositoryUsername;
    }

    public String getRepositoryPassword() {
        return repositoryPassword;
    }

    public String getGraphDBServerUrl() {
        return graphDBServerUrl;
    }

    public boolean shouldAddAccounts() {
        return addAccounts;
    }
}
