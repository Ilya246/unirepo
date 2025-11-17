package ru.ssau.tk._AMEBA_._PESEZ_.dto.request;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class PointRequest {

     @JsonProperty("functionId")
     Long functionId;

     @JsonProperty("xValue")
     Double xValue;

     @JsonProperty("yValue")
     Double yValue;
}
