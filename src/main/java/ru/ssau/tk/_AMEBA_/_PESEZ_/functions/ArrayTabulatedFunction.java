package ru.ssau.tk._AMEBA_._PESEZ_.functions;

import java.util.Arrays;

public class ArrayTabulatedFunction extends AbstractTabulatedFunction {
    final private double[] xValues, yValues;
    final private int count;

    public ArrayTabulatedFunction(double[] xValues, double[] yValues) {
        if (xValues.length != yValues.length) throw new RuntimeException("xValues and yValues sizes not equal.");
        double px = xValues[0];
        for (int i = 1; i < xValues.length; i++) {
            double cx = xValues[i];
            if (cx <= px) throw new RuntimeException("ArrayTabulatedFunction input array not sorted.");
            px = cx;
        }

        this.count = xValues.length;
        this.xValues = Arrays.copyOf(xValues, count);
        this.yValues = Arrays.copyOf(yValues, count);
    }

    public ArrayTabulatedFunction(MathFunction source, double xFrom, double xTo, int count) {
        xValues = new double[count];
        yValues = new double[count];
        this.count = count;
        if (count == 1) {
            xValues[0] = xFrom;
            yValues[0] = source.apply(xFrom);
            return;
        }

        double step = Math.abs(xTo - xFrom) / (count - 1);
        double x0 = Math.min(xFrom, xTo);
        for (int i = 0; i < count; i++) {
            double x = x0 + step * i;
            xValues[i] = x;
            yValues[i] = source.apply(x);
        }
    }

    @Override
    protected int floorIndexOfX(double x) {
        // по заданию, >x5 - вернуть count (5), меньше x1 - вернуть 0
        //  0    0    1    2    3    5
        // ---x1---x2---x3---x4---x5---
        for (int i = 1; i < count; i++) {
            if (getX(i) >= x) return i - 1;
        }
        return count;
    }

    @Override
    protected double extrapolateLeft(double x) {
        if (count == 1) return getY(0);
        return interpolate(x, getX(0), getX(1), getY(0), getY(1));
    }

    @Override
    protected double extrapolateRight(double x) {
        if (count == 1) return getY(0);
        return interpolate(x, getX(count - 2), getX(count - 1), getY(count - 2), getY(count - 1));
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        if (count == 1) return getY(0);
        return interpolate(x, getX(floorIndex), getX(floorIndex + 1), getY(floorIndex), getY(floorIndex + 1));
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public double getX(int index) {
        return xValues[index];
    }

    @Override
    public double getY(int index) {
        return yValues[index];
    }

    @Override
    public void setY(int index, double value) {
        yValues[index] = value;
    }

    @Override
    public int indexOfX(double x) {
        for (int i = 0; i < count; i++) {
            double val = getX(i);
            if (val >= x) return x == val ? i : -1;
        }
        return -1;
    }

    @Override
    public int indexOfY(double y) {
        for (int i = 0; i < count; i++) {
            if (getY(i) == y) return i;
        }
        return -1;
    }

    @Override
    public double leftBound() {
        return getX(0);
    }

    @Override
    public double rightBound() {
        return getX(count - 1);
    }
}
