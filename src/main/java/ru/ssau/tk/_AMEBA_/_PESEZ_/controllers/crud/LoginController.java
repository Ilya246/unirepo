package ru.ssau.tk._AMEBA_._PESEZ_.controllers.crud;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.ssau.tk._AMEBA_._PESEZ_.service.LoginService;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping
    public void login(HttpServletRequest request, HttpServletResponse response,
                      @RequestParam(value = "date", required = false) String date) throws IOException {

        // Генерируем новый date если не передан
        if (date == null) {
            date = UUID.randomUUID().toString();
        }

        // Если уже был запрос аутентификации для этого date и пользователь аутентифицирован
        if (loginService.hasGotLoginFor(date) && request.getUserPrincipal() != null) {
            loginService.removeGotLoginFor(date);
            response.sendRedirect("/");
            return;
        }

        // Добавляем date и показываем аутентификацию
        loginService.addGotLoginFor(date);

        response.setHeader("WWW-Authenticate", "Basic realm=\"REAUTHENTICATION-REQUIRED\"");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write("Please authenticate to continue");
    }

    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Очищаем SecurityContext
        SecurityContextHolder.clearContext();

        // Очищаем сессию
        request.getSession().invalidate();

        // Очищаем все отслеживаемые токены
        loginService.cleanup();

        // Генерируем новый date для переаутентификации
        String newDate = UUID.randomUUID().toString();

        // Редирект на /login с новым date параметром
        response.sendRedirect("/login?date=" + newDate);
    }

    @GetMapping("/force")
    public void forceReauth(HttpServletResponse response) throws IOException {
        // Генерируем новый date для принудительной переаутентификации
        String newDate = UUID.randomUUID().toString();
        response.sendRedirect("/login?date=" + newDate);
    }
}