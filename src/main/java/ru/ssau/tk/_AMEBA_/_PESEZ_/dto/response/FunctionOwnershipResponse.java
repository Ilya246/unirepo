package ru.ssau.tk._AMEBA_._PESEZ_.dto.response;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FunctionOwnershipResponse {
    @JsonProperty("userId")
    Long userId;
    @JsonProperty("funcId")
    Long functionId;
    @JsonProperty("createdDate")
    Timestamp createdDate;
    @JsonProperty("funcName")
    String funcName;
}
