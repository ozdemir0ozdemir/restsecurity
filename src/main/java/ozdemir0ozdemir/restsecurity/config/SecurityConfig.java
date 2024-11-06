package ozdemir0ozdemir.restsecurity.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationConfiguration configuration) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(request -> {
            request.requestMatchers("/api/data").authenticated();
            request.anyRequest().permitAll();
        });

        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(Customizer.withDefaults());

        http.sessionManagement(session -> {
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        });


        // Add custom api-key provider to authentication manager
        ProviderManager providerManager = (ProviderManager) configuration.getAuthenticationManager();
        providerManager.getProviders().add(new ApiKeyAuthenticationProvider());

        // pass the authentication manager to the custom api-key filter
        ApiKeyAuthFilter filter = new ApiKeyAuthFilter(providerManager);

        http.addFilterAfter(filter, BasicAuthenticationFilter.class);


        return http.build();
    }

    // For basic auth
    @Bean
    UserDetailsService userDetailsService() {
        return username -> User.builder()
                .username(username)
                .password("{noop}password")
                .authorities("USER")
                .build();
    }


}
