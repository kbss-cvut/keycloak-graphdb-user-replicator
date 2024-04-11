package cz.cvut.kbss.keycloak.provider;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphDbUrlParser {
    private static final Pattern regex = Pattern.compile("^(.*)/repositories/(.*)$");

    private String graphdbUrl;

    private final List<String> repositoryIds = new ArrayList<>();

    public GraphDbUrlParser(String sparqlEndpoints) {
        final String[] endpoints = sparqlEndpoints.split(",");
        for (String s : endpoints) {
            final Matcher m = regex.matcher(s);
            if (!m.matches()) {
                throw new RuntimeException(MessageFormat.format(
                        "The URL {0} is not a graphDb SPARQL endpoint URL (conforming to the pattern {1})",
                        s, regex.pattern()));
            }
            this.graphdbUrl = m.group(1);
            repositoryIds.add(m.group(2));
        }
    }

    public String getGraphdbUrl() {
        return graphdbUrl;
    }

    public List<String> getRepositoryIds() {
        return repositoryIds;
    }
}
