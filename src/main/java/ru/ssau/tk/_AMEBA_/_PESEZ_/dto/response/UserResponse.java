package ru.ssau.tk._AMEBA_._PESEZ_.dto.response;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse  {
    private Long userId;
    private Long userType;
    private String username;
    private String passwordHash;
    private Timestamp createdDate;
}
