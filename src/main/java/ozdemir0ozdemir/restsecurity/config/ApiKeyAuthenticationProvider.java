package ozdemir0ozdemir.restsecurity.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import java.util.Collections;

public class ApiKeyAuthenticationProvider implements AuthenticationProvider {


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String apiKey = (String) authentication.getPrincipal();
        String apiSecret = (String) authentication.getCredentials();
        System.out.println("MY PROVIDER");
        if ("valid-api-key".equals(apiKey) && "valid-api-secret".equals(apiSecret)) {
            return ApiKeyAuthenticationToken.authenticated(
                    apiKey,
                    apiSecret,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );
        }

        throw new AuthenticationException("Authentication Not Verified: " + authentication ){};
    }

    @Override
    public boolean supports(Class<?> authentication) {
        System.out.println("API-Key-------------------------------");
        return ApiKeyAuthenticationToken.class.equals(authentication);
    }
}
