package ru.ssau.tk._AMEBA_._PESEZ_.test;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.AbstractTabulatedFunction;

public class MockTabulatedFunction extends AbstractTabulatedFunction {
    final double x1, y1, x2, y2;

    public MockTabulatedFunction(double x1, double y1, double x2, double y2) {
        boolean swap = x2 < x1;
        this.x1 = swap ? x2 : x1;
        this.y1 = swap ? y2 : y1;
        this.x2 = swap ? x1 : x2;
        this.y2 = swap ? y1 : y2;
    }

    @Override
    protected int floorIndexOfX(double x) {
        return x > x2 ? 2 : 0;
    }

    @Override
    protected double extrapolateLeft(double x) {
        return interpolate(x, x1, x2, y1, y2);
    }

    @Override
    protected double extrapolateRight(double x) {
        return interpolate(x, x1, x2, y1, y2);
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        if (floorIndex != 0) throw new IndexOutOfBoundsException("Tried interpolate() out of bounds (" + floorIndex + ")");
        return interpolate(x, getX(floorIndex), getX(floorIndex + 1), getY(floorIndex), getY(floorIndex + 1));
    }

    @Override
    public double interpolate(double x, double x1, double x2, double y1, double y2) {
        return super.interpolate(x, x1, x2, y1, y2);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public double getX(int index) {
        return switch (index) {
            case 0 -> x1;
            case 1 -> x2;
            default -> throw new IndexOutOfBoundsException("Tried getX() out of bounds (" + index + ")");
        };
    }

    @Override
    public double getY(int index) {
        return switch (index) {
            case 0 -> y1;
            case 1 -> y2;
            default -> throw new IndexOutOfBoundsException("Tried getY() out of bounds (" + index + ")");
        };
    }

    @Override
    public void setY(int index, double value) {
        throw new RuntimeException("Tried setY()");
    }

    @Override
    public int indexOfX(double x) {
        if (x == x1) return 0;
        if (x == x2) return 1;
        return -1;
    }

    @Override
    public int indexOfY(double y) {
        if (y == y1) return 0;
        if (y == y2) return 1;
        return -1;
    }

    @Override
    public double leftBound() {
        return x1;
    }

    @Override
    public double rightBound() {
        return x2;
    }
}
