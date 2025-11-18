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
    public FunctionOwnershipResponse add(@RequestParam Long userId,@RequestParam("id") Long functionId,@RequestParam("name") String funcName){
        return ownershipService.addExistingFunctionToUser(userId,functionId,funcName);
    }

    @PostMapping("/math")
    @Operation(summary = "Создать математическую функцию для пользователя")
    public MathFunctionResponse createMath(
            @RequestParam Long userId,
            @RequestBody OwnedFunctionCreateRequest request
    ) {
        return ownershipService.createOwnedMath((MathFunctionRequest) request.funcParams, userId);
    }


    @PostMapping("/tabulated")
    @Operation(summary = "Создать табулированную функцию для пользователя")
    public TabulatedFunctionResponse createTabulated(
            @RequestParam Long userId,
            @RequestBody OwnedFunctionCreateRequest request
    ) {
        return ownershipService.createOwnedTabulated((TabulatedFunctionRequest) request.funcParams, userId);
    }

    @PostMapping("/pure-tabulated")
    @Operation(summary = "Создать табулированную функцию для пользователя из готовых массивов значений")
    public FunctionResponse createPure(
            @RequestParam Long userId,
            @RequestBody OwnedFunctionCreateRequest request
    ) {
        return ownershipService.createOwnedPure((PureTabulatedRequest) request.funcParams, userId);
    }

    @PostMapping("/composite")
    @Operation(summary = "Создать композитную функцию")
    public CompositeFunctionResponse createComposite(
            @RequestParam Long userId,
            @RequestBody OwnedFunctionCreateRequest request
    ) {
        return ownershipService.createOwnedComposite((CompositeFunctionRequest) request.funcParams, userId);
    }





}
