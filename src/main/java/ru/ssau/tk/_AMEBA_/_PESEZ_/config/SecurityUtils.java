package ru.ssau.tk._AMEBA_._PESEZ_.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.enums.UserType;

public class SecurityUtils {
    public static UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserEntity) {
            return (UserEntity) principal;
        }

        return null;
    }

    /**
     * Проверить роль пользователя через Spring Security authorities
     */
    public static boolean hasRequiredRole(UserType requiredRole) {
        if (requiredRole == UserType.Normal && getCurrentUserType() != null) return true;
        return getCurrentUserType() == requiredRole;
    }

    /**
     * Получить тип пользователя
     */
    public static UserType getCurrentUserType() {
        UserEntity user = getCurrentUser();
        if (user == null) return null;

        return user.getTypeId() == 2 ? UserType.Admin : UserType.Normal;
    }

    /**
     * Проверить, является ли пользователь админом
     */
    public static boolean isAdmin() {
        return hasRequiredRole(UserType.Admin);
    }

    /**
     * Проверить, является ли пользователь обычным пользователем
     */
    public static boolean isNormalUser() {
        return hasRequiredRole(UserType.Normal);
    }

    /**
     * Проверить, аутентифицирован ли пользователь
     */
    public static boolean isAuthenticated() {
        return getCurrentUser() != null;
    }

    /**
     * Получить ID текущего пользователя
     */
    public static Long getCurrentUserId() {
        UserEntity user = getCurrentUser();
        return user != null ? user.getUserId() : null;
    }

    /**
     * Получить имя текущего пользователя
     */
    public static String getCurrentUsername() {
        UserEntity user = getCurrentUser();
        return user != null ? user.getUserName() : null;
    }
}
