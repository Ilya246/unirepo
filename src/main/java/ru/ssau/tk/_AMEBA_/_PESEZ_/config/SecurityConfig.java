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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authz -> authz
                        // Разрешаем доступ к статическим ресурсам и login endpoint
                        .requestMatchers(
                                "/",
                                "/heartbeat",
                                "/users/register",
                                "/index.html",
                                "/user.html",
                                "/user.css",
                                "/user.js",
                                "/api.html",
                                "/api.js",
                                "/favicon.ico",
                                "/login" // ← ДОБАВЬТЕ ЭТО
                        ).permitAll()
                        // Все API endpoints требуют аутентификации
                        .requestMatchers("/users/**", "/functions/**", "/owned-functions/**", "/points/**").authenticated()
                        // Все остальные запросы запрещаем
                        .anyRequest().denyAll()
                )
                .httpBasic(httpBasic -> httpBasic
                        .realmName("SECURE-AREA")
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization", "X-Requested-With"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private static class SimpleBasicAuthEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response,
                             AuthenticationException authException) throws IOException {

            // Для API endpoints отправляем Basic Auth заголовок
            if (isApiRequest(request)) {
                response.addHeader("WWW-Authenticate", "Basic realm=\"SECURE-AREA\"");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"Authentication required\"}");
            } else {
                // Для HTML страниц отправляем HTML с инструкцией
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write(createAuthHtmlPage());
            }
        }

        private boolean isApiRequest(HttpServletRequest request) {
            String path = request.getRequestURI();
            return path.startsWith("/api/") ||
                    path.startsWith("/users/") ||
                    path.startsWith("/functions/") ||
                    path.startsWith("/owned-functions/") ||
                    path.startsWith("/points/");
        }

        private String createAuthHtmlPage() {
            return """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <title>Authentication Required</title>
                        <style>
                            body {
                                font-family: Arial, sans-serif;
                                margin: 0;
                                padding: 20px;
                                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                                min-height: 100vh;
                                display: flex;
                                align-items: center;
                                justify-content: center;
                            }
                            .auth-container {
                                background: white;
                                padding: 40px;
                                border-radius: 10px;
                                box-shadow: 0 10px 30px rgba(0,0,0,0.2);
                                text-align: center;
                                max-width: 400px;
                                width: 100%;
                            }
                            h1 {
                                color: #2c3e50;
                                margin-bottom: 20px;
                            }
                            p {
                                color: #7f8c8d;
                                line-height: 1.6;
                                margin-bottom: 20px;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="auth-container">
                            <h1>🔐 Authentication Required</h1>
                            <p>Please enter your username and password in the browser authentication dialog.</p>
                            <p><em>If no dialog appears, the page will refresh automatically.</em></p>
                        </div>
                        <script>
                            // Автоматически обновляем страницу чтобы вызвать диалог аутентификации
                            setTimeout(() => {
                                window.location.reload();
                            }, 2000);
                        </script>
                    </body>
                    </html>
                    """;
        }
    }
}