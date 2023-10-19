package cz.cvut.kbss.keycloak.provider.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.kbss.keycloak.provider.Configuration;
import cz.cvut.kbss.keycloak.provider.exception.GraphDBConnectionException;
import cz.cvut.kbss.keycloak.provider.model.GraphDBUserDto;
import cz.cvut.kbss.keycloak.provider.model.KodiUserAccount;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GraphDBUserDao {

    private static final Logger LOG = LoggerFactory.getLogger(GraphDBUserDao.class);

    private static final String USER_MANAGEMENT_PATH = "rest/security/users/";
    private static final String JSON_MIME_TYPE = "application/json";

    private final Configuration configuration;

    public GraphDBUserDao(Configuration configuration) {
        this.configuration = configuration;
    }

    public void addUser(KodiUserAccount userAccount) {
        final GraphDBUserDto userDto = new GraphDBUserDto();
        userDto.addAccessToRepository(configuration.getRepositoryId());
        postUserToGraphDB(userAccount.getUsername(), userDto);
    }

    private void postUserToGraphDB(String username, GraphDBUserDto user) {
        final ObjectMapper objectMapper = new ObjectMapper();
        final CredentialsProvider provider = new BasicCredentialsProvider();
        if (configuration.getRepositoryUsername() != null) {
            provider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(configuration.getRepositoryUsername(),
                            configuration.getRepositoryPassword()));
        }
        try (final CloseableHttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider)
                                                                 .build()) {
            final HttpPost post = new HttpPost(resolveUserEndpointUrl(username));
            LOG.debug("Creating GraphDB user by POSTing configuration to URL {}.", post.getURI());
            post.setEntity(new StringEntity(objectMapper.writeValueAsString(user)));
            post.addHeader(HttpHeaders.CONTENT_TYPE, JSON_MIME_TYPE);
            CloseableHttpResponse resp = client.execute(post);
            if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
                if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
                    LOG.info("User account {} already exists.", username);
                    return;
                }
                LOG.debug("User creation failed. Response body is: {}", resp.getEntity().toString());
                throw new GraphDBConnectionException(
                        "User creation failed, received status " + resp.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            throw new GraphDBConnectionException("Unable to create GraphDB user account for " + username, e);
        }
    }

    private String resolveUserEndpointUrl(String username) {
        String url = configuration.getGraphDBServerUrl();
        if (!url.endsWith("/")) {
            url += "/";
        }
        return url + USER_MANAGEMENT_PATH + username;
    }
}
