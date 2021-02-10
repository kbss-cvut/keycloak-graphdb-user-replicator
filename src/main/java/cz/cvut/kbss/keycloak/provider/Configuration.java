package cz.cvut.kbss.keycloak.provider;

import org.keycloak.Config;

public class Configuration {

    private final String realmId;

    private final String repositoryId;

    private final String repositoryUsername;

    private final String repositoryPassword;

    private final String language;

    private final String graphDBServerUrl;

    Configuration(Config.Scope scope) {
        this.realmId = scope.get("realmId");
        this.graphDBServerUrl = scope.get("graphDBServerUrl");
        this.repositoryId = scope.get("repositoryId");
        this.repositoryUsername = scope.get("repositoryUsername");
        this.repositoryPassword = scope.get("repositoryPassword");
        this.language = scope.get("language") != null ? scope.get("language") : "en";
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
