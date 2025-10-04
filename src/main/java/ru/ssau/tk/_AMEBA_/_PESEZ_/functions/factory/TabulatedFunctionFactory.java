package ru.ssau.tk._AMEBA_._PESEZ_.functions.factory;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.StrictTabulatedFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.TabulatedFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.UnmodifiableTabulatedFunction;

public interface TabulatedFunctionFactory {
    TabulatedFunction create(double[] xValues, double[] yValues);

    default TabulatedFunction createUnmodifiable(double[] xValues, double[] yValues) {
        TabulatedFunction func = create(xValues, yValues);
        return new UnmodifiableTabulatedFunction(func);
    }

    default TabulatedFunction createStrict(double[] xValues, double[] yValues){
        TabulatedFunction func = create(xValues, yValues);
        return new StrictTabulatedFunction(func);
    }

}
