package ru.ssau.tk._AMEBA_._PESEZ_.dto;

import ru.ssau.tk._AMEBA_._PESEZ_.exceptions.DifferentLengthOfArraysException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;

public class PointsDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 4603473297244125704L;

    public final int funcId;
    public final double[] xValues;
    public final double[] yValues;

    public PointsDTO(int funcId, double[] xValues, double[] yValues, boolean copy) {
        if (xValues.length != yValues.length) {
            throw new DifferentLengthOfArraysException("Array length mismatch when creating PointsDTO");
        }

        this.funcId = funcId;
        if (copy) {
            this.xValues = Arrays.copyOf(xValues, xValues.length);
            this.yValues = Arrays.copyOf(yValues, yValues.length);
        } else {
            this.xValues = xValues;
            this.yValues = yValues;
        }
    }
}
