package ru.ssau.tk._AMEBA_._PESEZ_.dto;

import static ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository.*;

public class FunctionDTO {
    public final int funcId;
    public final FunctionType funcType;
    public final String expression;

    public FunctionDTO(int funcId, int type, String expression) {
        this.funcId = funcId;
        this.funcType = FunctionType.fromInt(type);
        this.expression = expression;
    }
}
