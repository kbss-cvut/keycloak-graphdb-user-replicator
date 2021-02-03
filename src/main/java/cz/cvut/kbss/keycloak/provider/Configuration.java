package cz.cvut.kbss.keycloak.provider;

public class Configuration {

    private String realmId;

    private String repositoryUrl;

    private String repositoryUsername;

    private String repositoryPassword;

    private String graphDBServerUrl;

    private String graphDBUsername;

    private String graphDBPassword;

    public String getRealmId() {
        return realmId;
    }

    public void setRealmId(String realmId) {
        this.realmId = realmId;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public String getRepositoryUsername() {
        return repositoryUsername;
    }

    public void setRepositoryUsername(String repositoryUsername) {
        this.repositoryUsername = repositoryUsername;
    }

    public String getRepositoryPassword() {
        return repositoryPassword;
    }

    public void setRepositoryPassword(String repositoryPassword) {
        this.repositoryPassword = repositoryPassword;
    }

    public String getGraphDBServerUrl() {
        return graphDBServerUrl;
    }

    public void setGraphDBServerUrl(String graphDBServerUrl) {
        this.graphDBServerUrl = graphDBServerUrl;
    }

    public String getGraphDBUsername() {
        return graphDBUsername;
    }

    public void setGraphDBUsername(String graphDBUsername) {
        this.graphDBUsername = graphDBUsername;
    }

    public String getGraphDBPassword() {
        return graphDBPassword;
    }

    public void setGraphDBPassword(String graphDBPassword) {
        this.graphDBPassword = graphDBPassword;
    }
}
