package ru.ssau.tk._AMEBA_._PESEZ_.concurrent;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.Point;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.TabulatedFunction;

import java.util.Iterator;

public class SynchronizedTabulatedFunction implements TabulatedFunction {
    private final TabulatedFunction function;

    public SynchronizedTabulatedFunction(TabulatedFunction function) {
        this.function = function;
    }

    @Override
    public int getCount() {
        synchronized (function) {
            return function.getCount();
        }
    }

    @Override
    public double getX(int index) {
        synchronized (function) {
            return function.getX(index);
        }
    }

    @Override
    public double getY(int index) {
        synchronized (function) {
            return function.getY(index);
        }
    }

    @Override
    public void setY(int index, double value) {
        synchronized (function) {
            function.setY(index, value);
        }
    }

    @Override
    public int indexOfX(double x) {
        synchronized (function) {
            return function.indexOfX(x);
        }
    }

    @Override
    public int indexOfY(double y) {
        synchronized (function) {
            return function.indexOfY(y);
        }
    }

    @Override
    public double leftBound() {
        synchronized (function) {
            return function.leftBound();
        }
    }

    @Override
    public double rightBound() {
        synchronized (function) {
            return function.rightBound();
        }
    }

    @Override
    public double apply(double x) {
        synchronized (function) {
            return function.apply(x);
        }
    }

    @Override
    public Iterator<Point> iterator() {
        synchronized (function) {
            return function.iterator();
        }
    }
}
