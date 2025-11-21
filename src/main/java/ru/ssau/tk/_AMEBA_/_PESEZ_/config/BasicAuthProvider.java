package ru.ssau.tk._AMEBA_._PESEZ_.config;



import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.UserService;


import java.util.Collections;

@Component
@RequiredArgsConstructor
public class BasicAuthProvider implements AuthenticationProvider {
    private final UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        System.out.println("=== BASIC AUTH ATTEMPT ===");
        System.out.println("Username from request: '" + username + "'");

        // Если данные пустые или случайные - показываем окно
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                isRandomCredentials(username) || isRandomCredentials(password)) {
            System.out.println("AUTH: Showing auth dialog");
            throw new BadCredentialsException("Credentials required");
        }

        try {
            UserEntity user = userService.authenticate(username, password);

            if (user == null) {
                System.out.println("AUTH FAILED: User not found or wrong password");
                throw new BadCredentialsException("Invalid credentials");
            }

            // ПРАВИЛЬНОЕ СОЗДАНИЕ ROLES
            String role = user.getTypeId() == 2 ? "ROLE_ADMIN" : "ROLE_USER";
            System.out.println("AUTH SUCCESS: User '" + username + "' with role " + role);

            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

            // Создаем аутентификацию с правильными authorities
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    Collections.singletonList(authority)
            );

            System.out.println("Created authentication with authorities: " + auth.getAuthorities());
            return auth;

        } catch (Exception e) {
            System.out.println("AUTH ERROR: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            throw new BadCredentialsException("Authentication failed", e);
        }
    }

    private boolean isRandomCredentials(String text) {
        if (text == null || text.length() < 8) return false;
        return text.matches("[a-z0-9]{8,}") &&
                !text.contains(" ") &&
                !text.equals("admin") &&
                !text.equals("user") &&
                !text.equals("test");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
