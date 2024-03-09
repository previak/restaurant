package ru.previak.restaurant.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.previak.restaurant.entities.UserRole;
import ru.previak.restaurant.filters.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeRequests(config ->
                        config
                                .antMatchers("/api/dishes/**").hasAuthority(UserRole.ADMIN.name())
                                .antMatchers("/api/menu_items/**").hasAuthority(UserRole.ADMIN.name())
                                .antMatchers("/api/orders/**").hasAnyAuthority(UserRole.ADMIN.name(), UserRole.USER.name())
                                .antMatchers("/api/statistics").hasAuthority(UserRole.ADMIN.name())
                                .anyRequest().permitAll()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http
                .build();
    }
}
