
package ozdemir0ozdemir.restsecurity.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "API-Key";
    private static final String API_SECRET_HEADER = "API-Secret";

    private final AuthenticationManager authenticationManager;
    private final AntPathRequestMatcher matcher = new AntPathRequestMatcher("/api/**");

    private final SecurityContextHolderStrategy securityContextHolderStrategy =
            SecurityContextHolder.getContextHolderStrategy();

    private final SecurityContextRepository securityContextRepository =
            new RequestAttributeSecurityContextRepository();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!matcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader(API_KEY_HEADER);
        String apiSecret = request.getHeader(API_SECRET_HEADER);

        if (!StringUtils.hasText(apiKey) || !StringUtils.hasText(apiSecret)) {
//            throw new RuntimeException("Missing API Key or Secret!");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            SecurityContext context = securityContextHolderStrategy.createEmptyContext();
            Authentication authentication = ApiKeyAuthenticationToken.unAuthenticated(apiKey, apiSecret);
            authentication = this.authenticationManager.authenticate(authentication);
            context.setAuthentication(authentication);

            securityContextHolderStrategy.setContext(context);
            securityContextRepository.saveContext(context, request, response);
        } catch (AuthenticationException ex) {
            response.setStatus(HttpStatus.I_AM_A_TEAPOT.value());
            response.getWriter().write(new ObjectMapper().writeValueAsString(handleUnAuthorizedRequest(ex)));
            return;
        }

        filterChain.doFilter(request, response);
    }

    ProblemDetail handleUnAuthorizedRequest(AuthenticationException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.I_AM_A_TEAPOT, ex.getMessage());

        detail.setProperty("Deneme", "DenemeAciklama");

        detail.setProperty("Deneme2", "DenemeAciklama2");
        return detail;
    }
}

