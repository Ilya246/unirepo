package ru.ssau.tk._AMEBA_._PESEZ_.controllers.crud;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.UserRequest;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.UserResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.enums.UserType;
import ru.ssau.tk._AMEBA_._PESEZ_.service.UserServiceImpl;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.UserService;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AutoController {

    private final UserService userService;

    @PostMapping("/login")
    public UserResponse login(@RequestParam String userName,
                              @RequestParam String password) {
        UserEntity user = userService.authenticate(userName, password);
        UserType role=userService.toType(user.getTypeId());
        return userService.convertToResponse(user);
    }



}
