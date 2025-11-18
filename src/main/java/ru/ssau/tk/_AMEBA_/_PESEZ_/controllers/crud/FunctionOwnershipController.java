package ru.ssau.tk._AMEBA_._PESEZ_.controllers.crud;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.*;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionOwnershipEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.FunctionOwnershipService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/owned-functions")
@RequiredArgsConstructor
public class FunctionOwnershipController {
    private final FunctionOwnershipService ownershipService;

    @PostMapping
    @Operation(summary = "Создание связи функции и пользователя")
    public FunctionOwnershipResponse createOwnership(@RequestBody @Valid FunctionOwnershipRequest request) {
        return ownershipService.create(request);
    }

    @GetMapping
    @Operation(summary = "Получение связи по ID пользователя и функции")
    public FunctionOwnershipResponse getOwnership(
            @PathVariable Long userId,
            @PathVariable Long functionId) {
        return ownershipService.getOwnership(userId, functionId);
    }

    @PutMapping
    @Operation(summary = "Обновление связи функции и пользователя")
    public FunctionOwnershipResponse updateOwnership(
            @PathVariable Long userId,
            @PathVariable Long functionId,
            @RequestBody @Valid FunctionOwnershipRequest request) {
        return ownershipService.updateOwnership(userId, functionId, request);
    }

    @DeleteMapping
    @Operation(summary = "Удаление связи по ID пользователя и функции")
    public void deleteOwnership(
            @PathVariable Long userId,
            @PathVariable Long functionId) {
        ownershipService.deleteOwnership(userId, functionId);
    }

    @GetMapping("/user")
    @Operation(summary = "Получение всех связей пользователя")
    public List<FunctionOwnershipEntity> getOwnershipsByUserId(@PathVariable Long userId) {
        return ownershipService.getOwnershipsByUserId(userId);
    }

    @PostMapping("/own")
    @Operation(summary = "Добавить существующую функцию пользователю")
    public FunctionOwnershipResponse add(@PathVariable Long userId,@PathVariable Long functionId,@PathVariable String funcName){
        return ownershipService.addExistingFunctionToUser(userId,functionId,funcName);
    }

    @PostMapping("/math")
    @Operation(summary = "Создать математическую функцию для пользователя")
    public MathFunctionResponse createMath(
            @RequestParam Long userId,
            @RequestBody MathFunctionRequest request
    ) {
        return ownershipService.createOwnedMath(request, userId);
    }


    @PostMapping("/tabulated")
    @Operation(summary = "Создать табулированную функцию для пользователя")
    public TabulatedFunctionResponse createTabulated(
            @RequestParam Long userId,
            @RequestBody TabulatedFunctionRequest request
    ) {
        return ownershipService.createOwnedTabulated(request, userId);
    }

    @PostMapping("/pure-tabulated")
    @Operation(summary = "Создать табулированную функцию для пользователя из готовых массивов значений")
    public FunctionResponse createPure(
            @RequestParam Long userId,
            @RequestBody PureTabulatedRequest request
    ) {
        return ownershipService.createOwnedPure(request, userId);
    }

    @PostMapping("/composite")
    @Operation(summary = "Создать композитную функцию")
    public CompositeFunctionResponse createComposite(
            @RequestParam Long userId,
            @RequestBody CompositeFunctionRequest request
    ) {
        return ownershipService.createOwnedComposite(request, userId);
    }





}
