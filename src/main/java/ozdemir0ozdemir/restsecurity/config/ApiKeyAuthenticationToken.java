package ozdemir0ozdemir.restsecurity.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public final class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {

    private final String apiKey;
    private final String apiSecret;

    private ApiKeyAuthenticationToken(final String apiKey,
                                      final String apiSecret) {
        super(null);
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        setAuthenticated(false);
    }

    private ApiKeyAuthenticationToken(final String apiKey,
                                      final String apiSecret,
                                      final Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        super.setAuthenticated(true);
    }

    public static ApiKeyAuthenticationToken unAuthenticated(final String apiKey,
                                                            final String apiSecret) {
        return new ApiKeyAuthenticationToken(apiKey, apiSecret);
    }

    public static ApiKeyAuthenticationToken authenticated(final String apiKey,
                                                          final String apiSecret,
                                                          final Collection<? extends GrantedAuthority> authorities) {
        return new ApiKeyAuthenticationToken(apiKey, apiSecret, authorities);
    }


    @Override
    public String getCredentials() {
        return this.apiSecret;
    }

    @Override
    public String getPrincipal() {
        return this.apiKey;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {
        if (isAuthenticated) {
            throw new IllegalArgumentException("""
                    Cannot set this token to trusted. Use constructor which takes a
                    GrantedAuthority list instead!""");
        }
        super.setAuthenticated(false);
    }

}
