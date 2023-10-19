package cz.cvut.kbss.keycloak.provider.model;

public class Vocabulary {

    private String type = cz.cvut.kbss.keycloak.provider.Vocabulary.s_i_uzivatel;

    private String firstName = cz.cvut.kbss.keycloak.provider.Vocabulary.s_i_ma_krestni_jmeno;

    private String lastName = cz.cvut.kbss.keycloak.provider.Vocabulary.s_i_ma_prijmeni;

    private String username = cz.cvut.kbss.keycloak.provider.Vocabulary.s_i_ma_uzivatelske_jmeno;

    private String email;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
