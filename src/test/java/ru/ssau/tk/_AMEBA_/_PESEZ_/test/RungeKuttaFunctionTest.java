package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.RungeKuttaFunction;

import static org.junit.jupiter.api.Assertions.*;

class RungeKuttaFunctionTest {

    @Test
    void apply() {
        RungeKuttaFunction rk = new RungeKuttaFunction(2);

        double val = rk.apply(2);
        double actual = 2*2*2;
        assertTrue(Math.abs(val - actual) < 0.05);

        val = rk.apply(5);
        actual = 2*5*5;
        assertTrue(Math.abs(val - actual) < 0.1);

        val = rk.apply(100);
        actual = 2*100*100;
        assertTrue(Math.abs(val - actual) < 0.2);

        rk.t_0 = -1;

        val = rk.apply(-5);
        actual = 2*5*5;
        assertTrue(Math.abs(val - actual) < 0.1);
    }
}