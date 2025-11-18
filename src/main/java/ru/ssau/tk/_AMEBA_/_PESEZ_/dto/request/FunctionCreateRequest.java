package ru.ssau.tk._AMEBA_._PESEZ_.dto.request;

import com.fasterxml.jackson.annotation.*;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.MathFunction;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = OwnedFunctionCreateRequest.class, name = "owned"),
        @JsonSubTypes.Type(value = MathFunction.class, name = "math"),
        @JsonSubTypes.Type(value = PureTabulatedRequest.class, name = "pure"),
        @JsonSubTypes.Type(value = TabulatedFunctionRequest.class, name = "tabulated"),
        @JsonSubTypes.Type(value = CompositeFunctionRequest.class, name = "composite"),
})
public interface FunctionCreateRequest {
}