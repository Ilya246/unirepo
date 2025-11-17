package ru.ssau.tk._AMEBA_._PESEZ_.dto.request;

import com.fasterxml.jackson.annotation.*;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = OwnedFunctionCreateRequest.class, name = "owned"),
        @JsonSubTypes.Type(value = MathFunctionCreateRequest.class, name = "math"),
        @JsonSubTypes.Type(value = PureTabulatedCreateRequest.class, name = "pure"),
        @JsonSubTypes.Type(value = TabulatedFunctionCreateRequest.class, name = "tabulated"),
        @JsonSubTypes.Type(value = CompositeFunctionCreateRequest.class, name = "composite"),
})
public interface FunctionCreateRequest {
}
