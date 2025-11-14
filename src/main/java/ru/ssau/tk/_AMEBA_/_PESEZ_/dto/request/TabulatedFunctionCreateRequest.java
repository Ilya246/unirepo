package ru.ssau.tk._AMEBA_._PESEZ_.dto.request;

import java.io.Serial;
import java.io.Serializable;

public class TabulatedFunctionCreateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 2424081206842985076L;

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
