package ru.ssau.tk._AMEBA_._PESEZ_.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PureTabulatedRequest implements FunctionCreateRequest {
    double[] xValues;
    double[] yValues;
}
