package ru.ssau.tk._AMEBA_._PESEZ_.operations;

import ru.ssau.tk._AMEBA_._PESEZ_.exceptions.InconsistentFunctionsException;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.factory.*;

public class TabulatedFunctionOperationService {
    TabulatedFunctionFactory factory;

    public TabulatedFunctionOperationService(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    public TabulatedFunctionOperationService() {
        factory = new ArrayTabulatedFunctionFactory();
    }

    public static Point[] asPoints(TabulatedFunction tabulatedFunction) {
        Point[] pts = new Point[tabulatedFunction.getCount()];
        int i = 0;
        for (Point p : tabulatedFunction) {
            pts[i++] = p;
        }
        return pts;
    }

    private TabulatedFunction doOperation(TabulatedFunction a, TabulatedFunction b, BiOperation operation) {
        int count = a.getCount();
        if (count != b.getCount()) throw new InconsistentFunctionsException("Function lengths differ.");

        Point[] aPts = asPoints(a);
        Point[] bPts = asPoints(b);
        double[] xValues = new double[count];
        double[] yValues = new double[count];
        for (int i = 0; i < count; i++) {
            double x = aPts[i].getX(), yA = aPts[i].getY(), yB = bPts[i].getY();
            if (x != bPts[i].getX()) throw new InconsistentFunctionsException("Function x-values differ.");
            xValues[i] = x;
            yValues[i] = operation.apply(yA, yB);
        }
        return factory.create(xValues, yValues);
    }

    public TabulatedFunction add(TabulatedFunction a, TabulatedFunction b) {
        return doOperation(a, b, (double yA, double yB) -> yA + yB);
    }

    public TabulatedFunction subtract(TabulatedFunction a, TabulatedFunction b) {
        return doOperation(a, b, (double yA, double yB) -> yA - yB);
    }

    public TabulatedFunctionFactory getFactory() {
        return factory;
    }

    public void setFactory(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    private interface BiOperation {
        double apply(double u, double v);
    }
}
