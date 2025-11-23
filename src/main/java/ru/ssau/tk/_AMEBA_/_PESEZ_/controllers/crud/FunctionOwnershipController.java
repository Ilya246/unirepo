package ru.ssau.tk._AMEBA_._PESEZ_.controllers.crud;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.*;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.exceptions.CustomException;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.*;

import java.util.List;

import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.Log;

@RestController
@RequestMapping("/owned-functions")
@RequiredArgsConstructor
public class FunctionOwnershipController {
    private final FunctionOwnershipService ownershipService;
    private final UserService userService;
    private final CompositeFunctionService compositeService;

    @GetMapping
    @Operation(summary = "Получение связи по ID пользователя и функции")
    public OwnedFunctionResponse getOwnership(
            @RequestParam(value = "user", required = false) Long user, // поддержка обоих параметров
            @RequestParam Long id) {

        Long actualUserId = getCurrentUserId(user);
        return ownershipService.getOwnership(actualUserId, id);
    }

    @PutMapping
    @Operation(summary = "Обновление связи функции и пользователя")
    public void updateOwnership(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "user", required = false) Long user,
            @RequestParam(value = "id", required = false) Long id, // добавил required = false
            @RequestParam(value = "functionId", required = false) Long functionId, // добавил альтернативный параметр
            @RequestParam String name) {

        Long actualUserId = userId != null ? userId : user;
        Long actualFunctionId = id != null ? id : functionId; // поддержка обоих параметров

        if (actualUserId == null) {
            actualUserId = getCurrentUserId(actualUserId);
            Log.info("Using current user ID: {}", actualUserId);
        }

        if (actualFunctionId == null) {
            throw new CustomException("Function ID is required (use 'id' or 'functionId' parameter)", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping
    @Operation(summary = "Удаление связи по ID пользователя и функции")
    public void deleteOwnership(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "user", required = false) Long user,
            @RequestParam Long id) {

        Long actualUserId = userId != null ? userId : user;

        if (actualUserId == null) {
            actualUserId = getCurrentUserId(actualUserId);
            Log.info("Using current user ID: {}", actualUserId);
        }

        ownershipService.deleteOwnership(actualUserId, id);
    }

    @GetMapping("/user")
    @Operation(summary = "Получение всех связей пользователя")
    public List<OwnedFunctionResponse> getOwnershipsByUserId(@RequestParam(value = "user", required = false) Long user) {
        Long actualUserId = getCurrentUserId(user);
        return ownershipService.getOwnershipsByUserId(actualUserId);
    }

    @GetMapping("/composite")
    @Operation(summary = "Получить информацию о комозитной функции пользователя")
    public CompositeFunctionResponse getUserComposite(
            @RequestParam(required = false) Long userId,
            @RequestParam Long funcId
    ) {
        Long actualUserId = getCurrentUserId(userId);
        ownershipService.getOwnership(actualUserId, funcId);
        return compositeService.getFunction(funcId);
    }

    @PostMapping("/own")
    @Operation(summary = "Добавить существующую функцию пользователю")
    public void add(
            @RequestParam(required = false) Long userId,
            @RequestParam("id") Long functionId,
            @RequestParam("name") String funcName) {

        Long actualUserId = getCurrentUserId(userId);
        ownershipService.addExistingFunctionToUser(actualUserId, functionId, funcName);
    }

    @PostMapping("/math")
    @Operation(summary = "Создать математическую функцию для пользователя")
    public Long createMath(
            @RequestParam(required = false) Long userId,
            @RequestBody OwnedFunctionCreateRequest<MathFunctionRequest> request
    ) {
        Long actualUserId = getCurrentUserId(userId);
        return ownershipService.createOwnedMath(request, actualUserId);
    }

    @PostMapping("/tabulated")
    @Operation(summary = "Создать табулированную функцию для пользователя")
    public Long createTabulated(
            @RequestParam(required = false) Long userId,
            @RequestBody OwnedFunctionCreateRequest<TabulatedFunctionRequest> request
    ) {
        Long actualUserId = getCurrentUserId(userId);
        return ownershipService.createOwnedTabulated(request, actualUserId);
    }

    @PostMapping("/pure-tabulated")
    @Operation(summary = "Создать табулированную функцию для пользователя из готовых массивов значений")
    public Long createPure(
            @RequestParam(required = false) Long userId,
            @RequestBody OwnedFunctionCreateRequest<PureTabulatedRequest> request
    ) {
        Long actualUserId = getCurrentUserId(userId);
        return ownershipService.createOwnedPure(request, actualUserId);
    }

    @PostMapping("/composite")
    @Operation(summary = "Создать композитную функцию")
    public Long createComposite(
            @RequestParam(required = false) Long userId,
            @RequestBody OwnedFunctionCreateRequest<CompositeFunctionRequest> request
    ) {
        Long actualUserId = getCurrentUserId(userId);
        return ownershipService.createOwnedComposite(request, actualUserId);
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