package ru.ssau.tk._AMEBA_._PESEZ_.operations;

import ru.ssau.tk._AMEBA_._PESEZ_.concurrent.SynchronizedTabulatedFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.Point;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.TabulatedFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.factory.TabulatedFunctionFactory;

public class TabulatedDifferentialOperator implements DifferentialOperator<TabulatedFunction>{

    TabulatedFunctionFactory factory;

    public TabulatedDifferentialOperator(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }
    public TabulatedDifferentialOperator() {
        factory = new ArrayTabulatedFunctionFactory();
    }

    public TabulatedFunctionFactory getFactory() {
        return factory;
    }

    public void setFactory(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    @Override
    public TabulatedFunction derive(TabulatedFunction function) {
        //Получаем точки
        Point[] points = TabulatedFunctionOperationService.asPoints(function);
        int count = points.length;

        double[] xValues = new double[count];
        double[] yValues = new double[count];

        for (int i = 0; i < count; i++) {
            xValues[i] = points[i].getX();
        }

        // Для первых n-1 точек используем правую разностную производную
        for (int i = 0; i < count - 1; i++) {
            yValues[i] = (points[i + 1].getY() - points[i].getY()) / (points[i + 1].getX() - points[i].getX());
        }
        // Для последней точки используем левую разностную производную:
        yValues[count - 1] = yValues[count - 2];

        return factory.create(xValues, yValues);
    }

    public TabulatedFunction deriveSynchronously(TabulatedFunction function) {
        SynchronizedTabulatedFunction syncFunc;
        if (function instanceof SynchronizedTabulatedFunction _syncFunc) syncFunc = _syncFunc;
        else syncFunc = new SynchronizedTabulatedFunction(function);

        return syncFunc.doSynchronously(this::derive);
    }
}
