package ru.ssau.tk._AMEBA_._PESEZ_.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "Points")
public class PointsEntity {
    @Id
    @ManyToOne
    @JoinColumn(name = "func_Id")
    private FunctionEntity function;

    @Id
    @Column(name = "x_Value")
    private double xValue;

    @Column(name = "y_Value")
    private double yValue;

    // Constructors
    public PointsEntity() {}

    public PointsEntity(FunctionEntity function, double xValue, double yValue) {
        this.function = function;
        this.xValue = xValue;
        this.yValue = yValue;
    }

    // Getters and Setters
    public FunctionEntity getFunction() { return function; }
    public void setFunction(FunctionEntity function) { this.function = function; }

    public double get_xValue() { return xValue; }
    public void set_xValue(double xValue) { this.xValue = xValue; }

    public double get_yValue() { return yValue; }
    public void set_yValue(double yValue) { this.yValue = yValue; }

    // Convenience methods
    public String getPointAsString() {
        return "(" + xValue + ", " + yValue + ")";
    }

    public double distanceToOrigin() {
        return Math.sqrt(xValue * xValue + yValue * yValue);
    }

    @Override
    public String toString() {
        return "PointsEntity{funcId=" +
                (function != null ? function.getFuncId() : "null") +
                ", xValue=" + xValue + ", yValue=" + yValue + "}";
    }


}
