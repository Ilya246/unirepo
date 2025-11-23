package ru.ssau.tk._AMEBA_._PESEZ_.dto.response;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.ssau.tk._AMEBA_._PESEZ_.enums.FunctionType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FunctionResponse {
    @JsonProperty("funcId")
    Long funcId;
    @JsonProperty("funcType")
    FunctionType typeId;
    @JsonProperty("expression")
    String expression;

}

