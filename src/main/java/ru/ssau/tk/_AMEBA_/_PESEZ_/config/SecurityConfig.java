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
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final HtmlTemplateLoader htmlTemplateLoader;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // Разрешаем доступ ко всем статическим ресурсам
                        .requestMatchers("/", "/index.html", "/api", "/js/**", "/css/**", "/images/**", "/favicon.ico").permitAll()
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

    private class BasicAuthEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response,
                             AuthenticationException authException) throws IOException {

            response.addHeader("WWW-Authenticate", "Basic realm=\"RESTRICTED-AREA\"");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            // Проверяем, является ли запрос статическим ресурсом
            if (isStaticResourceRequest(request)) {
                // Для статических ресурсов возвращаем 404 вместо страницы аутентификации
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            if (isApiRequest(request)) {
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Authentication Required\"}");
            } else {
                // Показываем HTML интерфейс для не-API запросов
                response.setContentType("text/html;charset=UTF-8");
                String html = loadApiTestingInterface();
                response.getWriter().write(html);
            }
        }

        private boolean isStaticResourceRequest(HttpServletRequest request) {
            String uri = request.getRequestURI();
            return uri.startsWith("/js/") ||
                   uri.startsWith("/css/") ||
                   uri.startsWith("/images/") ||
                   uri.endsWith(".js") ||
                   uri.endsWith(".css") ||
                   uri.endsWith(".png") ||
                   uri.endsWith(".jpg") ||
                   uri.endsWith(".ico");
        }

        private boolean isApiRequest(HttpServletRequest request) {
            return request.getRequestURI().startsWith("/api/") ||
                   "application/json".equals(request.getHeader("Accept")) ||
                   request.getRequestURI().endsWith(".json");
        }

        private String loadApiTestingInterface() throws IOException {
            try {
                // Загружаем основной HTML файл
                ClassPathResource resource = new ClassPathResource("templates/api.html");
                return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                // Fallback - простая HTML страница
                return createFallbackAuthPage();
            }
        }

        private String createFallbackAuthPage() {
            return """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <title>API Testing Interface</title>
                        <style>
                            body { font-family: Arial, sans-serif; margin: 40px; }
                            .container { max-width: 800px; margin: 0 auto; }
                            .auth-form { background: #f5f5f5; padding: 20px; border-radius: 8px; }
                            input, button { padding: 10px; margin: 5px; }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <h1>API Testing Interface</h1>
                            <div class="auth-form">
                                <h3>Authentication Required</h3>
                                <p>Please enter your credentials to access the API testing interface.</p>
                            </div>
                        </div>
                    </body>
                    </html>
                    """;
        }
    }
}