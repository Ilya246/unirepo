package ru.ssau.tk._AMEBA_._PESEZ_.controllers.crud;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.enums.UserType;
import ru.ssau.tk._AMEBA_._PESEZ_.config.SecurityUtils;
import ru.ssau.tk._AMEBA_._PESEZ_.config.HtmlTemplateLoader;

import java.util.Map;

import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.Log;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class AutoController {

    @GetMapping("/")
    public ResponseEntity<String> home(HttpServletRequest request) {
        UserEntity user = SecurityUtils.getCurrentUser();

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header("WWW-Authenticate", "Basic realm=\"SECURE-AREA\"")
                    .body(createAuthRequiredPage());
        }

        UserType userType = SecurityUtils.getCurrentUserType();

        try {
            // Выбираем шаблон в зависимости от типа пользователя
            String templateName = determineTemplateByUserType(userType);

            // Загружаем HTML из файла и подставляем переменные
            String html = HtmlTemplateLoader.loadTemplate(templateName,
                    Map.of(
                            "username", user.getUserName(),
                            "role", userType.toString(),
                            "userName", user.getUserName(), // Дублируем для JavaScript
                            "userRole", userType.toString(),  // Дублируем для JavaScript
                            "contextPath", request.getContextPath() // Добавляем contextPath для корректных ссылок
                    ));

            return ResponseEntity.ok()
                    .header("Cache-Control", "no-cache, no-store, must-revalidate")
                    .header("Pragma", "no-cache")
                    .header("Expires", "0")
                    .body(html);
        } catch (Exception e) {
            // Fallback на старый код если файл не найден
            Log.error("Error loading template: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error loading template: " + e.getMessage());
        }
    }

    @GetMapping("/force-new-auth")
    public ResponseEntity<String> forceNewAuth() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header("WWW-Authenticate", "Basic realm=\"FORCE-NEW-LOGIN\"")
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .body("Please authenticate with new credentials");
    }

    @GetMapping("/clear-auth-cache")
    public ResponseEntity<String> clearAuthCache() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header("WWW-Authenticate", "Basic realm=\"CLEARED-AUTH\"")
                .header("Cache-Control", "no-cache")
                .body("Auth cache cleared");
    }

    private String determineTemplateByUserType(UserType userType) {
        switch (userType) {
            case Admin:
                return "api.html";
            case Normal:
            default:
                return "user.html";
        }
    }

    private String createAuthRequiredPage() {
        try {
            return HtmlTemplateLoader.loadTemplate("auth-required.html");
        } catch (Exception e) {
            Log.debug("Failed to load auth-required.html: " + e.getMessage());

            return "<html><body><h1>Authentication Required</h1><p>Please authenticate.</p></body></html>";
        }
    }
}