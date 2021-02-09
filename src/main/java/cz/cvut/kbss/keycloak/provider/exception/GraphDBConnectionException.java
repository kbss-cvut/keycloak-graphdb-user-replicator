package cz.cvut.kbss.keycloak.provider.exception;

public class GraphDBConnectionException extends RuntimeException {

    public GraphDBConnectionException(String message) {
        super(message);
    }

    public GraphDBConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
