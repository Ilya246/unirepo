package ru.ssau.tk._AMEBA_._PESEZ_.dto;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

public class UserDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 2571663376933295050L;

    public final int userId;
    public final int userType;
    public final String username;
    public final String password;
    public final Timestamp createdDate;

    public UserDTO(int userId, int type, String username, String password, Timestamp createdDate) {
        this.userId = userId;
        this.userType = type;
        this.username = username;
        this.password = password;
        this.createdDate = createdDate;
    }
}
