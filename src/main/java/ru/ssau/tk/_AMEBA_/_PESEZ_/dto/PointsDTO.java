package ru.ssau.tk._AMEBA_._PESEZ_.dto;

import ru.ssau.tk._AMEBA_._PESEZ_.exceptions.DifferentLengthOfArraysException;

import java.util.Arrays;

public class PointsDTO {
    public final int functionId;
    public final double[] xValues;
    public final double[] yValues;

    public PointsDTO(int functionId, double[] xValues, double[] yValues, boolean copy) {
        if (xValues.length != yValues.length) {
            throw new DifferentLengthOfArraysException("Array length mismatch when creating PointsDTO");
        }

        this.functionId = functionId;
        if (copy) {
            this.xValues = Arrays.copyOf(xValues, xValues.length);
            this.yValues = Arrays.copyOf(yValues, yValues.length);
        } else {
            this.xValues = xValues;
            this.yValues = yValues;
        }
    }
}
