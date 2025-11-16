package ru.ssau.tk._AMEBA_._PESEZ_.dto.request;

public class TabulatedFunctionCreateRequest implements FunctionCreateRequest {
    public final String expression;
    public final int pointCount;
    public final double xFrom;
    public final double xTo;

    public TabulatedFunctionCreateRequest(String expression, int pointCount, double xFrom, double xTo) {
        this.expression = expression;
        this.pointCount = pointCount;
        this.xFrom = xFrom;
        this.xTo = xTo;
    }
}
