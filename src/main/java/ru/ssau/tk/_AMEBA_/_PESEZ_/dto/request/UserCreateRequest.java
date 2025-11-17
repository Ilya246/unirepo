package ru.ssau.tk._AMEBA_._PESEZ_.dto.request;

import static ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository.*;

public class UserCreateRequest {
    public UserType userType;
    public String username;
    public String password;

    public UserCreateRequest(UserType userType, String username, String password) {
        this.userType = userType;
        this.username = username;
        this.password = password;
    }

    public UserCreateRequest() {}
}
