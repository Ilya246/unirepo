package ru.ssau.tk._AMEBA_._PESEZ_.dto.response;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.CompositeFunctionRequest;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.FunctionRequest;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FunctionResponse extends FunctionRequest {
    Long funcId;
}

