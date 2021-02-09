package cz.cvut.kbss.keycloak.provider.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public class GraphDBUserDto {

    private static final String ROLE_USER = "ROLE_USER";
    private static final String WRITE_REPO = "WRITE_REPO_";
    private static final String READ_REPO = "READ_REPO_";

    private final AppSettings appSettings;

    private final Collection<String> grantedAuthorities;

    public GraphDBUserDto() {
        this.appSettings = new AppSettings();
        this.grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(ROLE_USER);
    }

    public AppSettings getAppSettings() {
        return appSettings;
    }

    public Collection<String> getGrantedAuthorities() {
        return grantedAuthorities;
    }

    public void addAccessToRepository(String repositoryId) {
        Objects.requireNonNull(repositoryId);
        grantedAuthorities.add(WRITE_REPO + repositoryId);
        grantedAuthorities.add(READ_REPO + repositoryId);
    }

    static class AppSettings {
        private final boolean DEFAULT_SAMEAS;
        private final boolean DEFAULT_INFERENCE;
        private final boolean EXECUTE_COUNT;
        private final boolean IGNORE_SHARED_QUERIES;

        AppSettings() {
            this.DEFAULT_SAMEAS = true;
            this.DEFAULT_INFERENCE = true;
            this.EXECUTE_COUNT = true;
            this.IGNORE_SHARED_QUERIES = false;
        }

        public boolean isDEFAULT_SAMEAS() {
            return DEFAULT_SAMEAS;
        }

        public boolean isDEFAULT_INFERENCE() {
            return DEFAULT_INFERENCE;
        }

        public boolean isEXECUTE_COUNT() {
            return EXECUTE_COUNT;
        }

        public boolean isIGNORE_SHARED_QUERIES() {
            return IGNORE_SHARED_QUERIES;
        }
    }
}
