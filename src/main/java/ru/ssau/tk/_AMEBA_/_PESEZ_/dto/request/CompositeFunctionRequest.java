package ru.ssau.tk._AMEBA_._PESEZ_.dto.request;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionOwnershipEntity;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompositeFunctionRequest {
    FunctionEntity innerFunction;
    FunctionEntity outerFunction;
}
