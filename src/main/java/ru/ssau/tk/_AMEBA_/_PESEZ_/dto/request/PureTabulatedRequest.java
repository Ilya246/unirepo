package ru.ssau.tk._AMEBA_._PESEZ_.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PureTabulatedRequest implements FunctionCreateRequest {
    @JsonProperty("xValues")
    double[] xValues;
    @JsonProperty("yValues")
    double[] yValues;
}
