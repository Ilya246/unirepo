package ru.ssau.tk._AMEBA_._PESEZ_.dto.request;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.ssau.tk._AMEBA_._PESEZ_.enums.UserType;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class UserRequest {
    @JsonProperty("userType")
    private UserType userType;
    @JsonProperty("username")
    private String userName;
    @JsonProperty("password")
    private String password;
}
