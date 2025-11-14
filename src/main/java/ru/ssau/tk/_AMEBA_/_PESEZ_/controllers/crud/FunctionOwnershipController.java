package ru.ssau.tk._AMEBA_._PESEZ_.controllers.crud;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.FunctionOwnershipRequest;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.FunctionOwnershipResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionOwnershipEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.FunctionOwnershipService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/function-ownership")
@RequiredArgsConstructor
public class FunctionOwnershipController {
    private final FunctionOwnershipService ownershipService;

    @PostMapping
    @Operation(summary = "Создание связи функции и пользователя")
    public FunctionOwnershipResponse createOwnership(@RequestBody @Valid FunctionOwnershipRequest request) {
        return ownershipService.create(request);
    }

    @GetMapping("/user/{userId}/function/{functionId}")
    @Operation(summary = "Получение связи по ID пользователя и функции")
    public FunctionOwnershipResponse getOwnership(
            @PathVariable Long userId,
            @PathVariable Long functionId) {
        return ownershipService.getOwnership(userId, functionId);
    }

    @PutMapping("/user/{userId}/function/{functionId}")
    @Operation(summary = "Обновление связи функции и пользователя")
    public FunctionOwnershipResponse updateOwnership(
            @PathVariable Long userId,
            @PathVariable Long functionId,
            @RequestBody @Valid FunctionOwnershipRequest request) {
        return ownershipService.updateOwnership(userId, functionId, request);
    }

    @DeleteMapping("/user/{userId}/function/{functionId}")
    @Operation(summary = "Удаление связи по ID пользователя и функции")
    public void deleteOwnership(
            @PathVariable Long userId,
            @PathVariable Long functionId) {
        ownershipService.deleteOwnership(userId, functionId);
    }


    @GetMapping
    @Operation(summary = "Получение всех связей")
    public List<FunctionOwnershipEntity> getAllOwnerships() {
        return ownershipService.getAllOwnerships();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Получение всех связей пользователя")
    public List<FunctionOwnershipEntity> getOwnershipsByUserId(@PathVariable Long userId) {
        return ownershipService.getOwnershipsByUserId(userId);
    }

    @GetMapping("/function/{functionId}/owner")
    @Operation(summary = "Получение владельца функции")
    public Optional<UserEntity> getOwnerByFunctionId(@PathVariable Long functionId) {
        return ownershipService.getOwnerByFunctionId(functionId);
    }

    @GetMapping("/user/{userId}/functions")
    @Operation(summary = "Получение функций пользователя с сортировкой по дате")
    public List<FunctionEntity> getUserFunctionsOrdered(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "true") Boolean descending) {
        return ownershipService.getUserFunctionsOrderedByDate(userId, descending);
    }
}
