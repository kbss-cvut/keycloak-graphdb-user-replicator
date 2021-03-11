package cz.cvut.kbss.keycloak.provider;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthServerParser {
    private static final Pattern regex = Pattern.compile("^(.*)/realms/(.*)$");

    private String realmId;

    private String authServerUrl;

    public AuthServerParser(String authServerAuthUrl) {
        final Matcher m = regex.matcher(authServerAuthUrl);
        if (!m.matches()) {
            throw new RuntimeException(MessageFormat.format(
                "The URL {0} is not a authserver realm URL (conforming to the pattern {1})",
                authServerAuthUrl, regex.pattern()));
        }
        authServerUrl = m.group(1);
        realmId = m.group(2);
    }

    public String getRealmId() {
        return realmId;
    }

    public String getAuthServerUrl() {
        return authServerUrl;
    }
}
