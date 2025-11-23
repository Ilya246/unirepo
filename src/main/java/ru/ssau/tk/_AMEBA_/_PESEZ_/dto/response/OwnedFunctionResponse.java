package ru.ssau.tk._AMEBA_._PESEZ_.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class OwnedFunctionResponse {
    public FunctionResponse function;
    public FunctionOwnershipResponse ownership;
}
