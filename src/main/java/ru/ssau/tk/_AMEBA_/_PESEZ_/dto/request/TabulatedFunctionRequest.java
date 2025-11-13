package ru.ssau.tk._AMEBA_._PESEZ_.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TabulatedFunctionRequest {
    String expression;
    Double from;
    Double to;
    Integer pointCount;
}
