package cz.cvut.kbss.keycloak.provider;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class DataReplicationProviderFactory implements EventListenerProviderFactory {

    private String realmId;

    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        return new DataReplicationProvider(keycloakSession.users(), keycloakSession.realms(), realmId);
    }

    @Override
    public void init(Config.Scope scope) {
        this.realmId = scope.get("realmId");
        KodiUserAccount.setNamespace(scope.get("namespace"));
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
    }

    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return "keycloak-graphdb-user-replicator";
    }
}
