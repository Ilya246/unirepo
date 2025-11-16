package ru.ssau.tk._AMEBA_._PESEZ_.dto.request;

import java.io.Serial;

public class OwnedFunctionCreateRequest implements FunctionCreateRequest {
    @Serial
    private static final long serialVersionUID = -7691236631805307030L;

    public final FunctionCreateRequest funcParams;
    public final String name;

    public OwnedFunctionCreateRequest(FunctionCreateRequest funcParams, String name) {
        this.funcParams = funcParams;
        this.name = name;
    }
}
