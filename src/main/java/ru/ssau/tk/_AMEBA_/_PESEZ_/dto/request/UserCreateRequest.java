package ru.ssau.tk._AMEBA_._PESEZ_.dto.request;

import static ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository.*;

import java.io.Serial;
import java.io.Serializable;

public class UserCreateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 9057979862667368546L;

    public final UserType userType;
    public final String username;
    public final String password;

    public UserCreateRequest(UserType userType, String username, String password) {
        this.userType = userType;
        this.username = username;
        this.password = password;
    }
}
