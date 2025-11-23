package ru.ssau.tk._AMEBA_._PESEZ_.dto;

import static ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository.*;

import java.sql.Timestamp;

public class UserDTO {
    public final int userId;
    public final UserType userType;
    public final String username;
    public final String passwordHash;
    public final Timestamp createdDate;

    public UserDTO(int userId, UserType type, String username, String passwordHash, Timestamp createdDate) {
        this.userId = userId;
        this.userType = type;
        this.username = username;
        this.passwordHash = passwordHash;
        this.createdDate = createdDate;
    }
}
