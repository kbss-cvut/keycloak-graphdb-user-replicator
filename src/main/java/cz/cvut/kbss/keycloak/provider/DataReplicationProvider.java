package cz.cvut.kbss.keycloak.provider;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Supplier;

public class DataReplicationProvider implements EventListenerProvider {

    private static final Logger LOG = LoggerFactory.getLogger(DataReplicationProvider.class);

    private final Configuration configuration;

    private UserProvider userProvider;
    private RealmProvider realmProvider;

    private UserAccountDao userAccountDao;

    public DataReplicationProvider(Configuration configuration) {
        this.configuration = configuration;
    }

    public void setUserProvider(UserProvider userProvider) {
        this.userProvider = userProvider;
    }

    public void setRealmProvider(RealmProvider realmProvider) {
        this.realmProvider = realmProvider;
    }

    public void setUserAccountDao(UserAccountDao userAccountDao) {
        this.userAccountDao = userAccountDao;
    }

    @Override
    public void onEvent(Event event) {
        if (isDifferentRealm(event.getRealmId())) {
            return;
        }
        switch (event.getType()) {
            case UPDATE_PROFILE:
                updateUser(resolveUser(event));
                break;
            case UPDATE_EMAIL:
                // TODO: Update user in GraphDB
                logEvent(() -> toString(event));
                break;
            case REGISTER:
                // TODO Replicate data into triple store, create user in GraphDB
                // This is in case user self-registration is supported
                newUser(resolveUser(event));
            default:
                break;
        }
    }

    private boolean isDifferentRealm(String eventRealmId) {
        return !Objects.equals(configuration.getRealmId(), eventRealmId);
    }

    private void newUser(KodiUserAccount userAccount) {
        LOG.info("Generating new user metadata into triple store for user {}", userAccount);
        userAccountDao.transactional(() -> userAccountDao.persist(userAccount));
    }

    private void updateUser(KodiUserAccount userAccount) {
        LOG.info("Updating metadata of user {} in triple store", userAccount);
        userAccountDao.transactional(() -> userAccountDao.update(userAccount));
    }

    private void logEvent(Supplier<String> toString) {
        LOG.info("EVENT: {}", toString.get());
    }

    private String toString(Event event) {
        StringBuilder sb = new StringBuilder();

        sb.append("type=");
        sb.append(event.getType());
        sb.append(", realmId=");
        sb.append(event.getRealmId());
        sb.append(", clientId=");
        sb.append(event.getClientId());
        sb.append(", user=");
        sb.append(resolveUser(event));

        if (event.getError() != null) {
            sb.append(", error=");
            sb.append(event.getError());
        }

        return sb.toString();
    }

    private KodiUserAccount resolveUser(Event event) {
        return getUser(event.getUserId(), event.getRealmId());
    }

    private KodiUserAccount getUser(String userId, String realmId) {
        final UserModel userModel = userProvider.getUserById(userId, realmProvider.getRealm(realmId));
        return userModel != null ? new KodiUserAccount(userModel) : null;
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        if (isDifferentRealm(event.getRealmId())) {
            return;
        }
        switch (event.getOperationType()) {
            case CREATE:
                // TODO create user in GraphDB
                newUser(resolveUser(event));
                break;
            case UPDATE:
                // TODO If email changed, also update GraphDB user
                updateUser(resolveUser(event));
                break;
            case DELETE:
                // TODO Remove user from GraphDB
                logEvent(() -> toString(event));
                break;
            default:
                break;
        }
    }

    private String toString(AdminEvent adminEvent) {
        StringBuilder sb = new StringBuilder();

        sb.append("operationType=");
        sb.append(adminEvent.getOperationType());
        sb.append(", realmId=");
        sb.append(adminEvent.getAuthDetails().getClientId());
        sb.append(", adminId=");
        sb.append(adminEvent.getAuthDetails().getUserId());
        sb.append(", user=");
        sb.append(resolveUser(adminEvent));

        if (adminEvent.getError() != null) {
            sb.append(", error=");
            sb.append(adminEvent.getError());
        }

        return sb.toString();
    }

    private KodiUserAccount resolveUser(AdminEvent event) {
        final String resourceUri = event.getResourcePath();
        final String userId = resourceUri.substring(resourceUri.lastIndexOf('/') + 1);
        return getUser(userId, event.getRealmId());
    }

    @Override
    public void close() {
        userAccountDao.close();
    }
}
