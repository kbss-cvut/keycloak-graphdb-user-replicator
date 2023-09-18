package cz.cvut.kbss.keycloak.provider.model;

import cz.cvut.kbss.keycloak.provider.Vocabulary;
import org.keycloak.models.UserModel;

import java.net.URI;
import java.util.Objects;

public class KodiUserAccount {

    private static String namespace = "http://onto.fel.cvut.cz/ontologies/uzivatel/";

    private static String context = null;

    private static String type = Vocabulary.s_i_uzivatel;

    private URI uri;

    private String firstName;

    private String lastName;

    private String username;

    public KodiUserAccount() {
    }

    public KodiUserAccount(UserModel userModel) {
        Objects.requireNonNull(userModel);
        this.uri = URI.create(namespace + userModel.getId());
        this.firstName = userModel.getFirstName();
        this.lastName = userModel.getLastName();
        this.username = userModel.getUsername();
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static void setNamespace(String namespace) {
        if (namespace != null) {
            KodiUserAccount.namespace = namespace;
        }
    }

    public static void setContext(String context) {
        KodiUserAccount.context = context;
    }

    public static String getContext() {
        return context;
    }

    public static void setType(String type) {
        if (type != null) {
            KodiUserAccount.type = type;
        }
    }

    public static String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "KodiUserAccount{" +
                "<" + uri + "> " +
                firstName + " " + lastName +
                ", username='" + username + '\'' +
                '}';
    }
}
