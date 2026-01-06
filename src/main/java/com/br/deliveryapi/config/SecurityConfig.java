package com.br.deliveryapi.config;

import com.br.deliveryapi.enums.Role;
import com.br.deliveryapi.security.CustomUserDetailsService;
import com.br.deliveryapi.security.JwtAuthenticationFilter;
import com.br.deliveryapi.security.JwtUtil;
import com.br.deliveryapi.security.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ROLE_ADMIN = Role.ADMIN.toString();
    private static final String ROLE_CLIENT = Role.CLIENT.toString();

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil, userDetailsService, tokenBlacklistService);

        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()


                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/logout").authenticated()

                        .requestMatchers(HttpMethod.GET, "/address/**").hasRole(ROLE_CLIENT)
                        .requestMatchers(HttpMethod.PUT, "/address/**").hasRole(ROLE_CLIENT)
                        .requestMatchers(HttpMethod.DELETE, "/address/**").hasRole(ROLE_CLIENT)

                        .requestMatchers("/admin/**").hasRole(ROLE_ADMIN)

                        .requestMatchers("/client/**").hasRole(ROLE_CLIENT)

                        .requestMatchers(HttpMethod.PATCH, "/orders/*/*").hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.POST, "/orders/**").hasRole(ROLE_CLIENT)
                        .requestMatchers(HttpMethod.GET, "/orders/**").hasAnyRole(ROLE_CLIENT, ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/orders/**").hasRole(ROLE_CLIENT)
                        .requestMatchers(HttpMethod.PATCH, "/orders/**").hasRole(ROLE_CLIENT)
                        .requestMatchers(HttpMethod.DELETE, "/orders/**").hasRole(ROLE_CLIENT)

                        .requestMatchers(HttpMethod.POST, "/product/**").hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/product/**").hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/product/**").hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.GET, "/product/**").hasAnyRole(ROLE_ADMIN, ROLE_CLIENT)

                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
