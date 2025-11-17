package ru.ssau.tk._AMEBA_._PESEZ_.dto.request;

public class TabulatedFunctionCreateRequest implements FunctionCreateRequest {
    public String expression;
    public int pointCount;
    public double xFrom;
    public double xTo;

    public TabulatedFunctionCreateRequest(String expression, int pointCount, double xFrom, double xTo) {
        this.expression = expression;
        this.pointCount = pointCount;
        this.xFrom = xFrom;
        this.xTo = xTo;
    }

    public TabulatedFunctionCreateRequest() {}
}
