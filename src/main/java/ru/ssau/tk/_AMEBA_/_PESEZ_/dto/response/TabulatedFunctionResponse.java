package ru.ssau.tk._AMEBA_._PESEZ_.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TabulatedFunctionResponse {
    Long funcId;
    String expression;
    Double from;
    Double to;
    Integer pointCount;
    Integer actualPointCount;
}
