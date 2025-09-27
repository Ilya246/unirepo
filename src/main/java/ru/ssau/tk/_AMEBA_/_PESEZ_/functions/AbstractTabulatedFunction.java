package ru.ssau.tk._AMEBA_._PESEZ_.functions;

public abstract class AbstractTabulatedFunction implements TabulatedFunction {
    protected abstract int floorIndexOfX(double x);

    protected abstract double extrapolateLeft(double x);

    protected abstract double extrapolateRight(double x);

    protected abstract double interpolate(double x, int floorIndex);

    protected double interpolate(double x, double x1, double x2, double y1, double y2) {
        return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
    }

    @Override
    public double apply(double x) {
        if (x < leftBound()) return extrapolateLeft(x);
        if (x > rightBound()) return extrapolateRight(x);

        int pos = indexOfX(x);
        if (pos != -1) return getY(pos);

        int floorPos = floorIndexOfX(x);
        return interpolate(x, floorPos);
    }
}
