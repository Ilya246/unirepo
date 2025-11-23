package ru.ssau.tk._AMEBA_._PESEZ_.controllers.crud;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.UserRequest;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.UserResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.enums.UserType;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @Operation(summary = "Создание пользователя")
    public Long createUser(@RequestBody @Valid UserRequest request /*@RequestParam String adminUserName,
                                          @RequestParam String adminPassword*/) {
        /*userService.authenticateWithRole(adminUserName, adminPassword, UserType.Admin);*/
        return userService.createUser(request);
    }

    @PostMapping("/register")
    @Operation(summary = "Создание пользователя")
    public Long createUser(@RequestParam String username, @RequestParam String password) {
        return userService.createUser(new UserRequest(UserType.Normal, username, password));
    }

    @GetMapping("/user")
    @Operation(summary = "Получение пользователя по ID")
    public UserResponse getUser(@RequestParam Long id) {
        return userService.getUser(id);
    }

    @PutMapping
    @Operation(summary = "Обновление пользователя")
    public void updateUser(@RequestParam Long id, @RequestBody @Valid UserRequest request /*@RequestParam String adminUserName,
                                          @RequestParam String adminPassword*/) {
        /*userService.authenticateWithRole(adminUserName, adminPassword, UserType.Admin);*/
        userService.updateUser(id, request);
    }

    @DeleteMapping
    @Operation(summary = "Удаление пользователя")
    public void deleteUser(@RequestParam Long id /*@RequestParam String adminUserName,
                                          @RequestParam String adminPassword*/) {
        /* userService.authenticateWithRole(adminUserName, adminPassword, UserType.Admin);*/
        userService.deleteUser(id);
    }

    @GetMapping
    @Operation(summary = "Получение всех пользователей")
    public List<UserResponse> getAllUsers(/*@RequestParam String adminUserName,
                                          @RequestParam String adminPassword*/ ){
        /*userService.authenticateWithRole(adminUserName, adminPassword, UserType.Admin);*/
        return userService.getAllUsers();
    }

}