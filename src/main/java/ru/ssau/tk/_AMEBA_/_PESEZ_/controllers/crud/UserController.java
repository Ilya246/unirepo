package ru.ssau.tk._AMEBA_._PESEZ_.controllers.crud;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.UserRequest;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.IdResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.UserResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.enums.UserType;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.UserService;

import java.util.List;

import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.Log;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @Operation(summary = "Создание пользователя")
    public IdResponse createUser(@RequestBody @Valid UserRequest request ) {
        return new IdResponse(userService.createUser(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Создание пользователя")
    public IdResponse createUser(@RequestParam String username, @RequestParam String password) {
        return new IdResponse(userService.createUser(new UserRequest(UserType.Normal, username, password)));
    }

    @GetMapping("/user")
    @Operation(summary = "Получение пользователя по ID")
    public UserResponse getUser(@RequestParam Long id) {
        return userService.getUser(id);
    }

    @PutMapping
    @Operation(summary = "Обновление пользователя")
    public void updateUser(@RequestParam Long id, @RequestBody @Valid UserRequest request ) {

        userService.updateUser(id, request);
    }

    @DeleteMapping
    @Operation(summary = "Удаление пользователя")
    public void deleteUser(@RequestParam Long id ) {
        userService.deleteUser(id);
    }

    @GetMapping
    @Operation(summary = "Получение всех пользователей")
    public List<UserResponse> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/self")
    @Operation(summary = "Получение текущего id")
    public IdResponse getId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();
        Object credentials = authentication.getCredentials();

        if (principal instanceof UserEntity) {
            return new IdResponse(((UserEntity) principal).getUserId());
        } else if (principal instanceof org.springframework.security.core.userdetails.User) {
            org.springframework.security.core.userdetails.User securityUser =
                    (org.springframework.security.core.userdetails.User) principal;
            String username = securityUser.getUsername();

            // Если есть credentials (пароль), используем findByCredentials
            if (credentials instanceof String) {
                String password = (String) credentials;
                try {
                    UserEntity user = userService.findByCredentials(username, password);
                    return new IdResponse(user.getUserId());
                } catch (Exception e) {
                    // Fallback to findByUsername если findByCredentials не сработал
                    Log.warn("findByCredentials failed, falling back to findByUsername: {}", e.getMessage());
                    UserEntity user = userService.findByUsername(username);
                    return new IdResponse(user.getUserId());
                }
            } else {
                // Используем findByUsername если нет пароля
                UserEntity user = userService.findByUsername(username);
                return new IdResponse(user.getUserId());
            }
        } else if (principal instanceof String) {
            String username = (String) principal;
            UserEntity user = userService.findByUsername(username);
            return new IdResponse(user.getUserId());
        } else {
            throw new RuntimeException("Unknown principal type: " + principal.getClass().getName());
        }
    }

}