package cz.cvut.kbss.keycloak.provider;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphDbUrlParser {
    private static final Pattern regex = Pattern.compile("^(.*)/repositories/(.*)$");

    private String graphdbUrl;

    private String repositoryId;

    public GraphDbUrlParser(String sparqlEndpoint) {
        final Matcher m = regex.matcher(sparqlEndpoint);
        if (!m.matches()) {
            throw new RuntimeException(MessageFormat.format(
                "The URL {0} is not a graphDb SPARQL endpoint URL (conforming to the pattern {1})",
                sparqlEndpoint, regex.pattern()));
        }
        graphdbUrl = m.group(1);
        repositoryId = m.group(2);
    }

    public String getGraphdbUrl() {
        return graphdbUrl;
    }

    public String getRepositoryId() {
        return repositoryId;
    }
}
