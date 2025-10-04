package ru.ssau.tk._AMEBA_._PESEZ_.operations;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.MathFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.TabulatedFunction;

public interface DifferentialOperator <T extends MathFunction>{
    T derive(T function);

}
