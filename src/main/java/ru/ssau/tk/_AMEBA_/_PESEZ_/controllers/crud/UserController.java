package ru.ssau.tk._AMEBA_._PESEZ_.controllers.crud;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.UserRequest;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.UserResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @Operation(summary = "Создание пользователя")
    public UserResponse createUser(@RequestBody @Valid UserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение пользователя по ID")
    public UserResponse getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновление пользователя")
    public UserResponse updateUser(@PathVariable Long id, @RequestBody @Valid UserRequest request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление пользователя")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping
    @Operation(summary = "Получение всех пользователей")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/type/{typeId}")
    @Operation(summary = "Получение пользователей по типу")
    public List<UserResponse> getUsersByType(@PathVariable Integer typeId) {
        return userService.getUsersByType(typeId);
    }

    @GetMapping("/sorted")
    @Operation(summary = "Получение пользователей с сортировкой по дате")
    public List<UserResponse> getUsersSorted(@RequestParam(defaultValue = "true") Boolean descending) {
        return userService.getUsersSortedByDate(descending);
    }

    @GetMapping("/{userId}/functions")
    @Operation(summary = "Получение функций пользователя")
    public List<FunctionEntity> getUserFunctions(@PathVariable Long userId) {
        return userService.getUserFunctions(userId);
    }
}
