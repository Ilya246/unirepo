package ru.ssau.tk._AMEBA_._PESEZ_.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        System.out.println("=== AUTHENTICATION ENTRY POINT TRIGGERED ===");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Auth Exception: " + authException.getMessage());

        // ВСЕГДА отправляем заголовок для Basic Auth
        response.addHeader("WWW-Authenticate", "Basic realm=\"Restricted Area\"");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Authentication required");

        System.out.println("Sent 401 with WWW-Authenticate header");
    }
}
