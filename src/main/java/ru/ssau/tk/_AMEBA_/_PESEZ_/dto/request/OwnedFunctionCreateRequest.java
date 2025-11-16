package ru.ssau.tk._AMEBA_._PESEZ_.dto.request;

public class OwnedFunctionCreateRequest implements FunctionCreateRequest {
    public final FunctionCreateRequest funcParams;
    public final String name;

    public OwnedFunctionCreateRequest(FunctionCreateRequest funcParams, String name) {
        this.funcParams = funcParams;
        this.name = name;
    }
}
