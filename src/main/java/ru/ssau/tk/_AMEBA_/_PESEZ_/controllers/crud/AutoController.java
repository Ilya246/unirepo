package ru.ssau.tk._AMEBA_._PESEZ_.controllers.crud;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.enums.UserType;
import ru.ssau.tk._AMEBA_._PESEZ_.config.SecurityUtils;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class AutoController {


    @GetMapping("/")
    public ResponseEntity<String> home(HttpServletRequest request) {
        UserEntity user = SecurityUtils.getCurrentUser();

        if (user == null) {
            // Всегда возвращаем 401 для принудительной аутентификации
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header("WWW-Authenticate", "Basic realm=\"SECURE-AREA\"")
                    .body(createAuthRequiredPage());
        }

        UserType userType = SecurityUtils.getCurrentUserType();

        // Создаем страницу с мета-тегами, предотвращающими кэширование
        String html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
                <meta http-equiv="Pragma" content="no-cache">
                <meta http-equiv="Expires" content="0">
                <title>Secure Area</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        margin: 40px;
                        background: #f5f5f5;
                    }
                    .container {
                        max-width: 800px;
                        margin: 0 auto;
                        background: white;
                        padding: 30px;
                        border-radius: 10px;
                        box-shadow: 0 5px 15px rgba(0,0,0,0.1);
                    }
                    .logout-btn {
                        background: #dc3545;
                        color: white;
                        padding: 10px 20px;
                        text-decoration: none;
                        border-radius: 5px;
                        display: inline-block;
                        margin: 10px 0;
                    }
                    .session-info {
                        background: #e7f3ff;
                        padding: 15px;
                        border-radius: 5px;
                        margin: 20px 0;
                    }
                </style>
                <script>
                    // Предотвращаем кэширование страницы
                    window.onpageshow = function(event) {
                        if (event.persisted) {
                            window.location.reload();
                        }
                    };
                    
                    // Принудительный logout
                    function forceLogout() {
                        // Очищаем все хранилища
                        localStorage.clear();
                        sessionStorage.clear();
                        
                        // Делаем запрос для сброса аутентификации
                        fetch('/force-new-auth', {
                            method: 'GET',
                            headers: {
                                'Authorization': 'Basic ' + btoa('logout:logout')
                            },
                            credentials: 'include'
                        }).then(() => {
                            // Перезагружаем с принудительной аутентификацией
                            window.location.href = '/?forceAuth=' + Date.now();
                        });
                    }
                </script>
            </head>
            <body>
                <div class="container">
                    <h1>🔒 Secure Dashboard</h1>
                    <div class="session-info">
                        <p><strong>User:</strong> %s</p>
                        <p><strong>Role:</strong> %s</p>
                        <p><strong>Session:</strong> STATELESS (Auth required every time)</p>
                    </div>
                    
                    <h3>Welcome to the secure area!</h3>
                    <p>This page requires authentication on every visit.</p>
                    
                    <button onclick="forceLogout()" class="logout-btn">Force New Login</button>
                    <p><small>Next time you visit this page, browser will ask for credentials again.</small></p>
                </div>
            </body>
            </html>
            """.formatted(user.getUserName(), userType.toString());

        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(html);
    }

    // Специальный endpoint для сброса кэша аутентификации
    @GetMapping("/force-new-auth")
    public ResponseEntity<String> forceNewAuth() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header("WWW-Authenticate", "Basic realm=\"FORCE-NEW-LOGIN\"")
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .body("Please authenticate with new credentials");
    }

    // Endpoint для принудительного сброса
    @GetMapping("/clear-auth-cache")
    public ResponseEntity<String> clearAuthCache() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header("WWW-Authenticate", "Basic realm=\"CLEARED-AUTH\"")
                .header("Cache-Control", "no-cache")
                .body("Auth cache cleared");
    }

    private String createAuthRequiredPage() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Authentication Required</title>
                <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
                <script>
                    // Автоматически пытаемся вызвать диалог аутентификации
                    setTimeout(() => {
                        // Используем fetch с неправильными credentials
                        fetch('/', {
                            headers: {'Authorization': 'Basic invalid'}
                        }).catch(() => {
                            // Игнорируем ошибку - нам нужно вызвать диалог
                        });
                    }, 500);
                </script>
            </head>
            <body>
                <h1>Please authenticate...</h1>
                <p>Browser should show login dialog automatically.</p>
            </body>
            </html>
            """;
    }
}