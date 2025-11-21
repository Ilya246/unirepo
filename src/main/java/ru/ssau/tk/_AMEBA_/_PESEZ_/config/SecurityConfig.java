package ru.ssau.tk._AMEBA_._PESEZ_.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Arrays;


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
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Ключевая настройка!
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    // Кастомный EntryPoint для принудительного запроса аутентификации
    private static class BasicAuthEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response,
                             AuthenticationException authException) throws IOException {

            // Всегда отправляем заголовок аутентификации
            response.addHeader("WWW-Authenticate", "Basic realm=\"RESTRICTED-AREA\"");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            // Для AJAX/API запросов
            if (isApiRequest(request)) {
                response.getWriter().write("{\"error\": \"Authentication Required\"}");
            } else {
                // Для браузера - показываем HTML страницу
                response.getWriter().write(createAuthPage());
            }
        }

        private boolean isApiRequest(HttpServletRequest request) {
            return request.getRequestURI().startsWith("/api/") ||
                    "application/json".equals(request.getHeader("Accept"));
        }

        private String createAuthPage() {
            return """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Authentication Required</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            margin: 0;
                            padding: 0;
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            min-height: 100vh;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                        }
                        .auth-container {
                            background: white;
                            padding: 40px;
                            border-radius: 15px;
                            box-shadow: 0 15px 35px rgba(0,0,0,0.1);
                            text-align: center;
                            max-width: 500px;
                        }
                        .auth-icon {
                            font-size: 48px;
                            margin-bottom: 20px;
                        }
                        .retry-btn {
                            background: #667eea;
                            color: white;
                            padding: 12px 30px;
                            border: none;
                            border-radius: 25px;
                            cursor: pointer;
                            font-size: 16px;
                            margin: 10px;
                        }
                        .info-box {
                            background: #f8f9fa;
                            padding: 15px;
                            border-radius: 8px;
                            margin: 20px 0;
                            text-align: left;
                        }
                    </style>
                    <script>
                        function forceAuth() {
                            // Создаем запрос с неправильными credentials чтобы сбросить кэш
                            fetch('/clear-auth-cache', {
                                credentials: 'include'
                            }).then(() => {
                                // Перезагружаем страницу
                                location.reload(true);
                            });
                        }
                        
                        // Автоматически пытаемся сбросить кэш при загрузке
                        setTimeout(forceAuth, 1000);
                    </script>
                </head>
                <body>
                    <div class="auth-container">
                        <div class="auth-icon">🔐</div>
                        <h1>Authentication Required</h1>
                        <div class="info-box">
                            <p><strong>Please check your browser for a login dialog.</strong></p>
                            <p>If no dialog appears or you see cached credentials:</p>
                            <ol>
                                <li>Cancel the login dialog</li>
                                <li>Click "Retry Authentication" below</li>
                                <li>Enter your credentials again</li>
                            </ol>
                        </div>
                        <button onclick="forceAuth()" class="retry-btn">Retry Authentication</button>
                        <p><small>Browser: %s</small></p>
                    </div>
                </body>
                </html>
                """.formatted(getBrowserInfo());
        }

        private String getBrowserInfo() {
            return "Basic Auth - Sessionless";
        }
    }}
