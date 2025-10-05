package ru.ssau.tk._AMEBA_._PESEZ_.functions;

import ru.ssau.tk._AMEBA_._PESEZ_.exceptions.InterpolationException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayTabulatedFunction extends AbstractTabulatedFunction implements Insertable, Removable, Serializable {
    @Serial
    private static final long serialVersionUID = 8245354040289733450L;

    private double[] xValues, yValues;
    private int count;

    public ArrayTabulatedFunction(double[] xValues, double[] yValues) {
        if (xValues.length < 2) throw new IllegalArgumentException("Length of tabulated function must be at least 2");
        checkLengthIsTheSame(xValues, yValues);
        checkSorted(xValues);

        this.count = xValues.length;
        this.xValues=Arrays.copyOf(xValues, count);
        this.yValues=Arrays.copyOf(yValues, count);
    }

    public ArrayTabulatedFunction(MathFunction source, double xFrom, double xTo, int count) {
        if (count < 2) {
            throw new IllegalArgumentException("Length of tabulated function must be at least 2");
        }

        xValues = new double[count];
        yValues = new double[count];
        this.count = count;

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
        if (x < xValues[0]) {
            throw new IllegalArgumentException("x is less than left bound");
        }

        for (int i = 1; i < count; i++) {
            if (getX(i) >= x) return i - 1;
        }
        return count;
    }

    @Override
    protected double extrapolateLeft(double x) {
        return interpolate(x, getX(0), getX(1), getY(0), getY(1));
    }

    @Override
    protected double extrapolateRight(double x) {
        return interpolate(x, getX(count - 2), getX(count - 1), getY(count - 2), getY(count - 1));
    }

    @Override
    public double interpolate(double x, int floorIndex) {
        double ourX = getX(floorIndex);
        double nextX = getX(floorIndex + 1);
        if (x < ourX || x > nextX) throw new InterpolationException();
        return interpolate(x, ourX, nextX, getY(floorIndex), getY(floorIndex + 1));
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public double getX(int index) {
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("Index " + index + " is out of bounds [0, " + (count - 1) + "]");
        }
        return xValues[index];
    }

    @Override
    public double getY(int index) {
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("Index " + index + " is out of bounds [0, " + (count - 1) + "]");
        }
        return yValues[index];
    }

    @Override
    public void setY(int index, double value) {
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("Index " + index + " is out of bounds [0, " + (count - 1) + "]");
        }
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

    @Override
    public void insert(double x, double y) {
        if (indexOfX(x)!=-1) {
            yValues[indexOfX(x)]=y;
            return;
        }

        double[] newXValues = new double[count + 1];
        double[] newYValues = new double[count + 1];

        int insertIdx;
        if (x < xValues[0]) insertIdx = 0;
        else if (x > xValues[count - 1]) insertIdx = count;
        else insertIdx = floorIndexOfX(x) + 1;

        System.arraycopy(xValues, 0, newXValues, 0, insertIdx);
        if (insertIdx < count)
            System.arraycopy(xValues, insertIdx, newXValues, insertIdx + 1, count - insertIdx);

        System.arraycopy(yValues, 0, newYValues, 0, insertIdx);
        if (insertIdx < count)
            System.arraycopy(yValues, insertIdx, newYValues, insertIdx + 1, count - insertIdx);

        newXValues[insertIdx] = x;
        newYValues[insertIdx] = y;
        xValues = newXValues;
        yValues = newYValues;
        count++;
    }

    @Override
    public void remove(int index) {
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("Index " + index + " is out of bounds [0, " + (count - 1) + "]");
        }
        double[] newXValues = new double[count - 1];
        double[] newYValues = new double[count - 1];

        System.arraycopy(xValues, 0, newXValues, 0, index);
        System.arraycopy(xValues, index + 1, newXValues, index, count - index - 1);

        System.arraycopy(yValues, 0, newYValues, 0, index);
        System.arraycopy(yValues, index + 1, newYValues, index, count - index - 1);

        xValues = newXValues;
        yValues = newYValues;
        count--;
    }

    @Override
    public Iterator<Point> iterator() {
        class ArrayTabulatedFunctionIterator implements Iterator<Point> {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < count;
            }

            @Override
            public Point next() {
                if (!hasNext()) throw new NoSuchElementException();
                Point point = new Point(getX(i), getY(i));
                i++;
                return point;
            }
        }

        return new ArrayTabulatedFunctionIterator();
    }
}
