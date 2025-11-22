package ru.ssau.tk._AMEBA_._PESEZ_.controllers.crud;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._AMEBA_._PESEZ_.config.SecurityUtils;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.*;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionOwnershipEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.FunctionOwnershipService;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.UserService;

import java.util.List;

import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.Log;

@RestController
@RequestMapping("/owned-functions")
@RequiredArgsConstructor
public class FunctionOwnershipController {
    private final FunctionOwnershipService ownershipService;
    private final UserService userService;

    @PostMapping
    @Operation(summary = "Создание связи функции и пользователя")
    public FunctionOwnershipResponse createOwnership(@RequestBody @Valid FunctionOwnershipRequest request) {
        return ownershipService.create(request);
    }

    @GetMapping
    @Operation(summary = "Получение связи по ID пользователя и функции")
    public FunctionOwnershipResponse getOwnership(
            @RequestParam Long userId,
            @RequestParam Long id) {
        return ownershipService.getOwnership(userId, id);
    }

    @PutMapping
    @Operation(summary = "Обновление связи функции и пользователя")
    public FunctionOwnershipResponse updateOwnership(
            @RequestParam Long userId,
            @RequestParam Long id,
            @RequestParam String name) {
        return ownershipService.updateOwnership(userId, id, new FunctionOwnershipRequest(userId, id, name));
    }

    @DeleteMapping
    @Operation(summary = "Удаление связи по ID пользователя и функции")
    public void deleteOwnership(
            @RequestParam Long userId,
            @RequestParam Long id) {
        ownershipService.deleteOwnership(userId, id);
    }

    @GetMapping("/user")
    @Operation(summary = "Получение всех связей пользователя")
    public List<FunctionOwnershipEntity> getOwnershipsByUserId(@RequestParam Long userId) {
        return ownershipService.getOwnershipsByUserId(userId);
    }

    @PostMapping("/own")
    @Operation(summary = "Добавить существующую функцию пользователю")
    public FunctionOwnershipResponse add(@RequestParam Long userId, @RequestParam("id") Long functionId, @RequestParam("name") String funcName) {
        return ownershipService.addExistingFunctionToUser(userId, functionId, funcName);
    }

    @PostMapping("/math")
    @Operation(summary = "Создать математическую функцию для пользователя")
    public MathFunctionResponse createMath(
            @RequestParam(required = false) Long userId,
            @RequestBody OwnedFunctionCreateRequest request
    ) {
        Long actualUserId = getCurrentUserId(userId);
        return ownershipService.createOwnedMath((MathFunctionRequest) request.funcParams, actualUserId);
    }

    @PostMapping("/tabulated")
    @Operation(summary = "Создать табулированную функцию для пользователя")
    public TabulatedFunctionResponse createTabulated(
            @RequestParam(required = false) Long userId,
            @RequestBody OwnedFunctionCreateRequest request
    ) {
        Long actualUserId = getCurrentUserId(userId);
        return ownershipService.createOwnedTabulated((TabulatedFunctionRequest) request.funcParams, actualUserId);
    }

    @PostMapping("/pure-tabulated")
    @Operation(summary = "Создать табулированную функцию для пользователя из готовых массивов значений")
    public FunctionResponse createPure(
            @RequestParam(required = false) Long userId,
            @RequestBody OwnedFunctionCreateRequest request
    ) {
        Long actualUserId = getCurrentUserId(userId);
        return ownershipService.createOwnedPure((PureTabulatedRequest) request.funcParams, actualUserId);
    }

    @PostMapping("/composite")
    @Operation(summary = "Создать композитную функцию")
    public CompositeFunctionResponse createComposite(
            @RequestParam(required = false) Long userId,
            @RequestBody OwnedFunctionCreateRequest request
    ) {
        Long actualUserId = getCurrentUserId(userId);
        return ownershipService.createOwnedComposite((CompositeFunctionRequest) request.funcParams, actualUserId);
    }

    /**
     * Получает ID текущего пользователя. Если передан userId, использует его, иначе берет из контекста безопасности.
     */
    private Long getCurrentUserId(Long userId) {
        if (userId != null) {
            return userId;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();
        Object credentials = authentication.getCredentials();

        if (principal instanceof UserEntity) {
            return ((UserEntity) principal).getUserId();
        } else if (principal instanceof org.springframework.security.core.userdetails.User) {
            org.springframework.security.core.userdetails.User securityUser =
                    (org.springframework.security.core.userdetails.User) principal;
            String username = securityUser.getUsername();

            // Если есть credentials (пароль), используем findByCredentials
            if (credentials instanceof String) {
                String password = (String) credentials;
                try {
                    UserEntity user = userService.findByCredentials(username, password);
                    return user.getUserId();
                } catch (Exception e) {
                    // Fallback to findByUsername если findByCredentials не сработал
                    Log.warn("findByCredentials failed, falling back to findByUsername: {}", e.getMessage());
                    UserEntity user = userService.findByUsername(username);
                    return user.getUserId();
                }
            } else {
                // Используем findByUsername если нет пароля
                UserEntity user = userService.findByUsername(username);
                return user.getUserId();
            }
        } else if (principal instanceof String) {
            String username = (String) principal;
            UserEntity user = userService.findByUsername(username);
            return user.getUserId();
        } else {
            throw new RuntimeException("Unknown principal type: " + principal.getClass().getName());
        }
    }
}