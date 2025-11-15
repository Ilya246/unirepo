package ru.ssau.tk._AMEBA_._PESEZ_.dto.request;

import ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository;

import java.io.Serial;
import java.io.Serializable;

public class UserChangeRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 5540806536710692591L;

    public final UserRepository.UserType userType;
    public final String username;
    public final String password;

    public UserChangeRequest(UserRepository.UserType userType, String username, String password) {
        this.userType = userType;
        this.username = username;
        this.password = password;
    }
}
