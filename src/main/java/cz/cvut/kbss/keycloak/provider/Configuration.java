package cz.cvut.kbss.keycloak.provider;

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

    private final String language;

    private final String graphDBServerUrl;

    Configuration(Config.Scope scope) {
        final String components = getProperty("COMPONENTS");
        if (components == null) {
            throw new RuntimeException("Environmental variable 'COMPONENTS' must be set.");
        }
        final Map<String, Object> dbServer =
            (Map<String, Object>) parseComponents(components).get("dbServer");
        final GraphDbUrlParser parser = new GraphDbUrlParser(dbServer.get("url").toString());

        this.realmId = getProperty("REALM_ID");
        this.graphDBServerUrl = isNullOrEmpty(getProperty("DB_SERVER_URL")) ? parser.getGraphdbUrl() : getProperty("DB_SERVER_URL");
        this.repositoryId = isNullOrEmpty(getProperty("DB_SERVER_REPOSITORY_ID")) ? parser.getRepositoryId() : getProperty("DB_SERVER_REPOSITORY_ID");
        this.repositoryUsername = getProperty("REPOSITORY_USERNAME");
        this.repositoryPassword = getProperty("REPOSITORY_PASSWORD");
        this.language = getProperty("language") != null ? getProperty("language") : "en";
        // TODO this.context = getProperty("CONTEXT");
        // TODO this.namespace = getProperty("NAMESPACE");
    }

    private boolean isNullOrEmpty(final String nullOrEmpty) {
        return Objects.isNull(nullOrEmpty) || nullOrEmpty.isEmpty();
    }

    private String getProperty(String key) {
        return System.getenv(key);
    }

    private Map<String, Object> parseComponents(String components) {
        final String componentsDecoded = new String(Base64.getDecoder().decode(components));
        return new Yaml().load(componentsDecoded);
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

    public String getLanguage() {
        return language;
    }

    public String getGraphDBServerUrl() {
        return graphDBServerUrl;
    }
}
