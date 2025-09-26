package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.MathFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.SqrFunction;

import static org.junit.jupiter.api.Assertions.*;

class SqrFunctionTest {
    @Test
    void apply() {
        MathFunction func;
        func = new SqrFunction();
        int x = 5;
        int actual = x * x;
        assertEquals(func.apply(x), actual);
        x = -5;
        actual = x * x;
        assertEquals(func.apply(x), actual);
        x = 0;
        actual = x * x;
        assertEquals(func.apply(x), actual);
    }
}