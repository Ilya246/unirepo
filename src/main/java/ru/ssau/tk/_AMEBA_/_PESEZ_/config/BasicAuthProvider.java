package ru.ssau.tk._AMEBA_._PESEZ_.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.UserService;

import jakarta.servlet.http.HttpServletRequest;
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

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String clientIP = getClientIP(request);

        System.out.println("Client IP: " + clientIP);
        if ("localhost".equals(username) && "localhost".equals(password) && isLocalhost(request)) {
            System.out.println("AUTH: Localhost admin user detected from localhost IP");
            return createLocalhostAdmin();
        }

        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            System.out.println("AUTH: Showing auth dialog");
            throw new BadCredentialsException("Credentials required");
        }

        try {
            UserEntity user = userService.authenticate(username, password);

            if (user == null) {
                System.out.println("AUTH FAILED: User not found or wrong password");
                throw new BadCredentialsException("Invalid credentials");
            }

            // CORRECT ROLE CREATION
            String role = user.getTypeId() == 2 ? "ROLE_ADMIN" : "ROLE_USER";
            System.out.println("AUTH SUCCESS: User '" + username + "' with role " + role);

            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

            // Create authentication with correct authorities
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

    private Authentication createLocalhostAdmin() {
        UserEntity localhostUser = new UserEntity();
        localhostUser.setUserId(0L);
        localhostUser.setUserName("localhost");
        localhostUser.setTypeId(2);

        String role = "ROLE_ADMIN";
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

        return new UsernamePasswordAuthenticationToken(
                localhostUser,
                null,
                Collections.singletonList(authority)
        );
    }

    private boolean isLocalhost(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return "127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip);
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}