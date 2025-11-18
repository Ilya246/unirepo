package ru.ssau.tk._AMEBA_._PESEZ_.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
public class OwnedFunctionCreateRequest implements FunctionCreateRequest {
    @JsonProperty
    public FunctionCreateRequest funcParams;
    @JsonProperty
    public String name;
}