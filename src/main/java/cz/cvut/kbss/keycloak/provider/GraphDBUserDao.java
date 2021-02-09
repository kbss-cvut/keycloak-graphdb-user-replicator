package cz.cvut.kbss.keycloak.provider;

import cz.cvut.kbss.keycloak.provider.model.GraphDBUserDto;
import cz.cvut.kbss.keycloak.provider.model.KodiUserAccount;

public class GraphDBUserDao {

    private final String serverUrl;
    private final String repositoryId;

    public GraphDBUserDao(String serverUrl, String repositoryId) {
        this.serverUrl = serverUrl;
        this.repositoryId = repositoryId;
    }

    public void addUser(KodiUserAccount userAccount) {
        final GraphDBUserDto userDto = new GraphDBUserDto();
        userDto.addAccessToRepository(repositoryId);
        // TODO
    }
}
