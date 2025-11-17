package ru.ssau.tk._AMEBA_._PESEZ_.dto.response;

import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.PointRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

import ru.ssau.tk._AMEBA_._PESEZ_.entity.PointsEntity;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PointResponse extends PointRequest {
    Long functionId;
    Double xValue;

}
