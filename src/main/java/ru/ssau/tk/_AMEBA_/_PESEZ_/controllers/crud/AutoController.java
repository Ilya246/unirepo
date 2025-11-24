package ru.ssau.tk._AMEBA_._PESEZ_.controllers.crud;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._AMEBA_._PESEZ_.config.SecurityUtils;
import ru.ssau.tk._AMEBA_._PESEZ_.enums.UserType;
import ru.ssau.tk._AMEBA_._PESEZ_.service.LoginService;
import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class AutoController {
    private final LoginService loginService;

    @GetMapping("/login")
    public void getLogin(@RequestParam String date, HttpServletResponse response) throws IOException {
        Log.info("Got login attempt with date {}, user: {}", date, SecurityUtils.getCurrentUserId());
        // Check if we already had login attempt for this date and user has required role
        if (loginService.hasGotLoginFor(date) && SecurityUtils.hasRequiredRole(UserType.Normal)) {
            loginService.removeGotLoginFor(date);
            response.sendRedirect("/user.html");
            return;
        }

        // Add this date to track login attempts and schedule cleanup
        loginService.addGotLoginFor(date);

        // Send Basic Auth challenge
        response.setHeader("WWW-Authenticate", "Basic realm=\"Restricted Area\"");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}