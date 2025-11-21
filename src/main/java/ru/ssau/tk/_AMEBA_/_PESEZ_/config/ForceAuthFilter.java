package ru.ssau.tk._AMEBA_._PESEZ_.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ForceAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Для всех запросов к защищенным страницам
        if (!httpRequest.getRequestURI().startsWith("/public/")) {
            // Всегда запрещаем кэширование
            httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            httpResponse.setHeader("Pragma", "no-cache");
            httpResponse.setHeader("Expires", "0");

            // Добавляем заголовок аутентификации если её нет
            if (httpRequest.getHeader("Authorization") == null) {
                httpResponse.setHeader("WWW-Authenticate", "Basic realm=\"ALWAYS-AUTH\"");
            }
        }

        chain.doFilter(request, response);
    }
}
