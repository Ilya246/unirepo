package ru.ssau.tk._AMEBA_._PESEZ_.dto.request;

import java.io.Serial;

public class MathFunctionCreateRequest implements FunctionCreateRequest {
    @Serial
    private static final long serialVersionUID = 5375409949798463584L;

    public final String expression;

    public MathFunctionCreateRequest(String expression) {
        this.expression = expression;
    }
}
