package ru.ssau.tk._AMEBA_._PESEZ_.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TabulatedFunctionRequest implements FunctionCreateRequest {
    @JsonProperty("expression")
    String expression;
    @JsonProperty("xFrom")
    Double from;
    @JsonProperty("xTo")
    Double to;
    @JsonProperty("pointCount")
    Integer pointCount;
}
