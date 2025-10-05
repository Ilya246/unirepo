package ru.ssau.tk._AMEBA_._PESEZ_.functions;

import ru.ssau.tk._AMEBA_._PESEZ_.exceptions.ArrayIsNotSortedException;
import ru.ssau.tk._AMEBA_._PESEZ_.exceptions.DifferentLengthOfArraysException;
import ru.ssau.tk._AMEBA_._PESEZ_.operations.TabulatedFunctionOperationService;

import java.util.Iterator;

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

    @Override
    public Iterator<Point> iterator() {
        throw new UnsupportedOperationException();
    }

    public static void checkLengthIsTheSame(double[] xValues, double[] yValues) {
        if (xValues.length != yValues.length) throw new DifferentLengthOfArraysException();
    }

    public static void checkSorted(double[] xValues) {
        for (int i = 1; i < xValues.length; i++) {
            if (xValues[i] <= xValues[i - 1]) throw new ArrayIsNotSortedException();
        }
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(" size = ").append(getCount());
        for (int i=0; i<getCount(); i++){
            sb.append("\n[").append(getX(i)).append("; ").append(getY(i)).append("]");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TabulatedFunction otherTab)) return false;

        int count = otherTab.getCount();
        if (count != otherTab.getCount()) return false;

        Point[] ourPoints = TabulatedFunctionOperationService.asPoints(this);
        Point[] otherPoints = TabulatedFunctionOperationService.asPoints(otherTab);
        for (int i = 0; i < count; ++i) {
            if (!ourPoints[i].equals(otherPoints[i])) return false;
        }

        return true;
    }
}
