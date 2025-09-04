package co.com.pragma.bootcamp.security.config;

import co.com.pragma.bootcamp.usecase.token.TokenUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    public static final String CLIENT = "CLIENT";
    private final ReactiveAuthenticationManager authenticationManager;
    public static final String ADMIN = "ADMIN";
    public static final String ADVISOR = "ADVISOR";
    private final TokenUseCase tokenUseCase;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        AuthenticationWebFilter jwtFilter = new JwtAuthenticationFilter(authenticationManager, tokenUseCase);
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/swagger-ui/**").permitAll()
                        .pathMatchers("/v3/api-docs/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/applications/**").hasAnyRole(ADMIN, ADVISOR, CLIENT)
                        .pathMatchers(HttpMethod.GET, "/api/v1/applications/**").hasAnyRole(ADMIN, ADVISOR)
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .build();
    }
}