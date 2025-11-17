package ru.ssau.tk._AMEBA_._PESEZ_.dto.request;

public class PureTabulatedCreateRequest implements FunctionCreateRequest {
    public double[] xValues;
    public double[] yValues;

    public PureTabulatedCreateRequest(double[] xValues, double[] yValues) {
        this.xValues = xValues;
        this.yValues = yValues;
    }

    public PureTabulatedCreateRequest() {}
}
