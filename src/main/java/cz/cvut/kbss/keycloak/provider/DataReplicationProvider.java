package cz.cvut.kbss.keycloak.provider;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.UserProvider;

import java.util.Objects;

public class DataReplicationProvider implements EventListenerProvider {

    private final UserProvider userProvider;
    private final RealmProvider realmProvider;

    private final String realmId;

    public DataReplicationProvider(UserProvider userProvider, RealmProvider realmProvider, String realmId) {
        this.userProvider = userProvider;
        this.realmProvider = realmProvider;
        this.realmId = realmId;
    }

    @Override
    public void onEvent(Event event) {
        if (!Objects.equals(realmId, event.getRealmId())) {
            return;
        }
        if (event.getType() == EventType.UPDATE_PROFILE) {
            // TODO: Update data in triple store
            System.out.println("EVENT: " + toString(event));
        } else if (event.getType() == EventType.UPDATE_EMAIL) {
            // TODO: Update user in GraphDB
            System.out.println("EVENT: " + toString(event));
        }
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        if (!Objects.equals(realmId, event.getRealmId())) {
            return;
        }
        if (event.getOperationType() == OperationType.CREATE) {
            System.out.println("EVENT: " + toString(event));
            // TODO Replicate data into triple store, create user in GraphDB
        }
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
        return new KodiUserAccount(userProvider.getUserById(userId, realmProvider.getRealm(realmId)));
    }

    private String toString(AdminEvent adminEvent) {
        StringBuilder sb = new StringBuilder();

        sb.append("operationType=");
        sb.append(adminEvent.getOperationType());
        sb.append(", realmId=");
        sb.append(adminEvent.getAuthDetails().getClientId());
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
        final String userId = resourceUri.substring(resourceUri.lastIndexOf('/' + 1));
        return getUser(userId, event.getRealmId());
    }

    @Override
    public void close() {
    }
}
