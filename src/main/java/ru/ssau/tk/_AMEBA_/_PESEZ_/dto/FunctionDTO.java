package ru.ssau.tk._AMEBA_._PESEZ_.dto;

import java.io.Serial;
import java.io.Serializable;

public class FunctionDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 6781605240333637936L;

    public final int funcId;
    public final int funcType;
    public final String expression;

    public FunctionDTO(int funcId, int type, String expression) {
        this.funcId = funcId;
        this.funcType = type;
        this.expression = expression;
    }
}
