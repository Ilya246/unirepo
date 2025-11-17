package ru.ssau.tk._AMEBA_._PESEZ_.dto.request;

public class MathFunctionCreateRequest implements FunctionCreateRequest {
    public String expression;

    public MathFunctionCreateRequest(String expression) {
        this.expression = expression;
    }

    public MathFunctionCreateRequest() {}
}
