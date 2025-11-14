package ru.ssau.tk._AMEBA_._PESEZ_.dto.request;

import java.io.Serial;
import java.io.Serializable;

public class PureTabulatedCreateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 8581644486022925562L;

    public final double[] xValues;
    public final double[] yValues;

    public PureTabulatedCreateRequest(double[] xValues, double[] yValues) {
        this.xValues = xValues;
        this.yValues = yValues;
    }
}
