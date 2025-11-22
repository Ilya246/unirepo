package ru.ssau.tk._AMEBA_._PESEZ_.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import ru.ssau.tk._AMEBA_._PESEZ_.config.HtmlTemplateLoader;

import java.io.IOException;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/public/**").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> httpBasic
                        .realmName("RESTRICTED-AREA")
                        .authenticationEntryPoint(new BasicAuthEntryPoint())
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    private static class BasicAuthEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response,
                             AuthenticationException authException) throws IOException {

            response.addHeader("WWW-Authenticate", "Basic realm=\"RESTRICTED-AREA\"");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            if (isApiRequest(request)) {
                response.getWriter().write("{\"error\": \"Authentication Required\"}");
            } else {
                // Используем HTML из файла
                String html = createAuthPage();
                response.getWriter().write(html);
            }
        }

        private boolean isApiRequest(HttpServletRequest request) {
            return request.getRequestURI().startsWith("/api/") ||
                   "application/json".equals(request.getHeader("Accept"));
        }

        private String createAuthPage() throws IOException {

                return HtmlTemplateLoader.loadTemplate("basic-auth-page.html",
                        Map.of("browserInfo", "Basic Auth - Sessionless"));

        }
    }
}