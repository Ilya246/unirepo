package ru.ssau.tk._AMEBA_._PESEZ_.controllers.crud;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.enums.UserType;
import ru.ssau.tk._AMEBA_._PESEZ_.config.SecurityUtils;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AutoController {

    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo() {
        UserEntity user = SecurityUtils.getCurrentUser();

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header("WWW-Authenticate", "Basic realm=\"SECURE-AREA\"")
                    .body("Authentication required");
        }

        UserType userType = SecurityUtils.getCurrentUserType();

        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .body(Map.of(
                        "id", user.getUserId(),
                        "username", user.getUserName(),
                        "userType", userType.toString()
                ));
    }

    @GetMapping("/redirect-by-role")
    public ResponseEntity<String> redirectByRole() {
        UserEntity user = SecurityUtils.getCurrentUser();

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header("WWW-Authenticate", "Basic realm=\"SECURE-AREA\"")
                    .build();
        }

        UserType userType = SecurityUtils.getCurrentUserType();

        if (userType == UserType.Admin) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/api.html")
                    .build();
        } else {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/user.html")
                    .build();
        }
    }
}