package ru.ssau.tk._AMEBA_._PESEZ_.dto.response;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.ssau.tk._AMEBA_._PESEZ_.enums.UserType;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse  {
    private Long userId;
    private UserType userType;
    private String username;
    private String passwordHash;
    private Timestamp createdDate;

    public void setUserType(int fromId) {
        userType = UserType.fromId(fromId);
    }
}
