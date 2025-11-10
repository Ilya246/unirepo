package ru.ssau.tk._AMEBA_._PESEZ_.entity;

import java.io.Serializable;
import java.util.Objects;

public class PointId implements Serializable {
    private Long function; // соответствует funcId
    private double xValue;

    // Default constructor
    public PointId() {}

    public PointId(Long function, double xValue) {
        this.function = function;
        this.xValue = xValue;
    }

    // Getters and Setters
    public Long getFunction() { return function; }
    public void setFunction(Long function) { this.function = function; }

    public double getxValue() { return xValue; }
    public void setxValue(double xValue) { this.xValue = xValue; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointId pointId = (PointId) o;
        return function == pointId.function &&
                Double.compare(pointId.xValue, xValue) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(function, xValue);
    }

    @Override
    public String toString() {
        return "PointId{function=" + function + ", xValue=" + xValue + "}";
    }
}
