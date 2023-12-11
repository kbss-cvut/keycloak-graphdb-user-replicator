package cz.cvut.kbss.keycloak.provider;

import cz.cvut.kbss.keycloak.provider.model.UserAccount;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserProvider;

import java.util.Objects;

public class KeycloakAdapter {

    private final UserProvider userProvider;
    private final RealmProvider realmProvider;

    private final Configuration configuration;

    public KeycloakAdapter(UserProvider userProvider, RealmProvider realmProvider, Configuration configuration) {
        this.userProvider = userProvider;
        this.realmProvider = realmProvider;
        this.configuration = configuration;
    }

    public boolean isDifferentRealm(String realmId) {
        return !Objects.equals(realmProvider.getRealmByName(configuration.getRealmId()).getId(), realmId);
    }

    public UserAccount getUser(String userId, String realmId) {
        final UserModel userModel = userProvider.getUserById(realmProvider.getRealm(realmId), userId);
        return userModel != null ? new UserAccount(userModel) : null;
    }

    public boolean shouldAddAccounts() {
        return configuration.shouldAddAccounts();
    }
}
