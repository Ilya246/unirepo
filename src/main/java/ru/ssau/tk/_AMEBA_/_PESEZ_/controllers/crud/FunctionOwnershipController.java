package ru.ssau.tk._AMEBA_._PESEZ_.controllers.crud;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._AMEBA_._PESEZ_.config.SecurityUtils;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.*;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionOwnershipEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.exceptions.CustomException;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.FunctionOwnershipService;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.UserService;

import java.util.List;
import java.util.stream.Collectors;

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
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "user", required = false) Long user, // поддержка обоих параметров
            @RequestParam Long id) {

        Long actualUserId = userId != null ? userId : user;

        if (actualUserId == null) {
            actualUserId = getCurrentUserId(actualUserId);
            Log.info("Using current user ID: {}", actualUserId);
        }

        return ownershipService.getOwnership(actualUserId, id);
    }

    @PutMapping
    @Operation(summary = "Обновление связи функции и пользователя")
    public FunctionOwnershipResponse updateOwnership(
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

        return ownershipService.updateOwnership(actualUserId, actualFunctionId, new FunctionOwnershipRequest(actualUserId, actualFunctionId, name));
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
    public List<FunctionOwnershipResponse> getOwnershipsByUserId(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "user", required = false) Long user) {

        Long actualUserId = userId != null ? userId : user;

        if (actualUserId == null) {
            actualUserId = getCurrentUserId(actualUserId);
            Log.info("Using current user ID: {}", actualUserId);
        }

        List<FunctionOwnershipEntity> entities = ownershipService.getOwnershipsByUserId(actualUserId);
        return entities.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private FunctionOwnershipResponse convertToResponse(FunctionOwnershipEntity entity) {
        FunctionOwnershipResponse response = new FunctionOwnershipResponse();
        response.setUserId(entity.getId().getUserId());
        response.setFunctionId(entity.getId().getFuncId());
        response.setFuncName(entity.getFuncName());
        return response;
    }

    @PostMapping("/own")
    @Operation(summary = "Добавить существующую функцию пользователю")
    public FunctionOwnershipResponse add(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "user", required = false) Long user,
            @RequestParam(value = "id", required = false) Long functionId,
            @RequestParam(value = "functionId", required = false) Long altFunctionId,
            @RequestParam("name") String funcName) {

        Log.info("add called - userId: {}, user: {}, functionId: {}, altFunctionId: {}, name: {}",
                userId, user, functionId, altFunctionId, funcName);

        Long actualUserId = userId != null ? userId : user;
        Long actualFunctionId = functionId != null ? functionId : altFunctionId;

        // Если userId не передан, получаем ID текущего пользователя
        if (actualUserId == null) {
            actualUserId = getCurrentUserId(actualUserId);
            Log.info("Using current user ID: {}", actualUserId);
        }

        if (actualFunctionId == null) {
            throw new CustomException("Function ID is required (use 'id' or 'functionId' parameter)", HttpStatus.BAD_REQUEST);
        }

        try {
            Log.info("Calling service with user ID: {}, function ID: {}, name: {}",
                    actualUserId, actualFunctionId, funcName);
            FunctionOwnershipResponse result = ownershipService.addExistingFunctionToUser(actualUserId, actualFunctionId, funcName);
            Log.info("Service returned: {}", result);
            return result;
        } catch (Exception e) {
            Log.error("Error in add for user {} function {}: {}", actualUserId, actualFunctionId, e.getMessage(), e);
            throw e;
        }
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