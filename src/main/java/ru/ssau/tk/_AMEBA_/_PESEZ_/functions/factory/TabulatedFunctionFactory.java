package ru.ssau.tk._AMEBA_._PESEZ_.functions.factory;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.TabulatedFunction;

public interface TabulatedFunctionFactory {
    TabulatedFunction create(double[] xValues, double[] yValues);
}
