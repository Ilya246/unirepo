package ru.ssau.tk._AMEBA_._PESEZ_.functions;

import static org.junit.jupiter.api.Assertions.*;

class SqrFunctionTest {
    @org.junit.jupiter.api.Test
    void apply() {
        MathFunction func;
        func = new SqrFunction();
        int x = 5;
        int actual = x * x;
        assertEquals(func.apply(x), actual);
    }
}