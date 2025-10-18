package ru.ssau.tk._AMEBA_._PESEZ_.operations;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.MathFunction;

// Считает интеграл от функции типа T, возможно другого типа
public interface IntegralOperator <T extends MathFunction, R> {
    R integrate(T function);
}
