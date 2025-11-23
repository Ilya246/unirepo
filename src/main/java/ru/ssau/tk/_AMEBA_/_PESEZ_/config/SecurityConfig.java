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
                        .requestMatchers("/", "/index.html", "/js/**", "/css/**", "/images/**", "/favicon.ico").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> httpBasic
                        .realmName("SECURE-AREA")
                        .authenticationEntryPoint(new SimpleBasicAuthEntryPoint())
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

    // Простой EntryPoint который всегда показывает браузерное окно аутентификации
    private static class SimpleBasicAuthEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response,
                             AuthenticationException authException) throws IOException {

            // Всегда отправляем заголовок Basic Auth
            response.addHeader("WWW-Authenticate", "Basic realm=\"SECURE-AREA\"");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("text/html;charset=UTF-8");

            // Простая HTML страница с инструкцией
            String html = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <title>Authentication Required</title>
                        <style>
                            body { 
                                font-family: Arial, sans-serif; 
                                margin: 40px; 
                                background: #f5f5f5;
                            }
                            .container { 
                                max-width: 500px; 
                                margin: 100px auto; 
                                background: white;
                                padding: 30px;
                                border-radius: 8px;
                                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                                text-align: center;
                            }
                            h1 { color: #2c3e50; }
                            p { color: #7f8c8d; line-height: 1.6; }
                        </style>
                    </head>
                    <body>
                        <div class="container">
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
            response.getWriter().write(html);
        }
    }
}