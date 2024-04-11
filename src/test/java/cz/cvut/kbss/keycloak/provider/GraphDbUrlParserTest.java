package cz.cvut.kbss.keycloak.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import java.util.Collections;

public class GraphDbUrlParserTest {

    @Test
    public void parsesGraphDbRepositoryUrlCorrectly() {
        final String sparqlEndpointUrl = "https://localhost/služby/graphdb/repositories/kodi";
        final GraphDbUrlParser parser = new GraphDbUrlParser(sparqlEndpointUrl);
        assertEquals(Collections.singletonList("kodi"), parser.getRepositoryIds());
        assertEquals("https://localhost/služby/graphdb", parser.getGraphdbUrl());
    }

    @Test
    public void throwsExceptionOnNonGraphDbRepositoryUrl() {
        final String sparqlEndpointUrl = "https://localhost/služby/kodi";
        assertThrows(RuntimeException.class, () -> new GraphDbUrlParser(sparqlEndpointUrl));
    }
}
