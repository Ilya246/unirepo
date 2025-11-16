package ru.ssau.tk._AMEBA_._PESEZ_.dto.request;

public class PureTabulatedCreateRequest implements FunctionCreateRequest {
    public final double[] xValues;
    public final double[] yValues;

    public PureTabulatedCreateRequest(double[] xValues, double[] yValues) {
        this.xValues = xValues;
        this.yValues = yValues;
    }
}
