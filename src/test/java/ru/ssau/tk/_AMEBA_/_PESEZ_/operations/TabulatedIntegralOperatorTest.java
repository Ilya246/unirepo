package ru.ssau.tk._AMEBA_._PESEZ_.operations;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;

import static org.junit.jupiter.api.Assertions.*;

class TabulatedIntegralOperatorTest {

    @Test
    void testIntegrateUnitFunction() {
        var func = new ArrayTabulatedFunction(new UnitFunction(), 0, 100000000, 100000001);
        var integrator = new TabulatedIntegralOperator(1000000);

        var time1 = System.nanoTime();
        double result = integrator.integrate(func);
        System.out.println("Time taken: " + (double)(System.nanoTime() - time1) / 1e9);

        assertEquals(100000000, result);
    }
}