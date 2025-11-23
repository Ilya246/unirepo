package ru.ssau.tk._AMEBA_._PESEZ_.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.PointRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PointsResponse {
    Long functionId;
    @JsonProperty("xValues")
    double[] xValues;
    @JsonProperty("yValues")
    double[] yValues;
}
