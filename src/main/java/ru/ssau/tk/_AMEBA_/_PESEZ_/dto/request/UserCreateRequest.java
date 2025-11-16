package ru.ssau.tk._AMEBA_._PESEZ_.dto.request;

import static ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository.*;

public class UserCreateRequest {
    public final UserType userType;
    public final String username;
    public final String password;

    public UserCreateRequest(UserType userType, String username, String password) {
        this.userType = userType;
        this.username = username;
        this.password = password;
    }
}
