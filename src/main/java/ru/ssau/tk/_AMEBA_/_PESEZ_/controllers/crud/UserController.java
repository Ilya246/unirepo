package ru.ssau.tk._AMEBA_._PESEZ_.controllers.crud;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._AMEBA_._PESEZ_.config.SecurityUtils;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.UserRequest;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.IdResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.UserResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.enums.UserType;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.UserService;

import java.sql.Timestamp;
import java.util.List;

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
    public UserResponse getId(){
        UserEntity current = SecurityUtils.getCurrentUser();
        if (current == null) return null;
        return new UserResponse(current.getUserId(),
                UserType.fromId(current.getTypeId()),
                current.getUserName(),
                current.getPassword(),
                Timestamp.from(current.getCreatedDate().toInstant()));
    }

}