package ru.ssau.tk._AMEBA_._PESEZ_.operations;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;

import static org.junit.jupiter.api.Assertions.*;

class TabulatedIntegralOperatorTest {

    @Test
    void testIntegrateUnitFunction() {
        var func = new ArrayTabulatedFunction(new UnitFunction(), 0, 100000000, 100000001);
        var integrator = new TabulatedIntegralOperator(1000000);

        double result = integrator.integrate(func);

        assertEquals(100000000, result);
    }

    @Test
    void testIntegrateIdentityFunction() {
        var func = new ArrayTabulatedFunction(new IdentityFunction(), 0, 5, 6);
        var integrator = new TabulatedIntegralOperator(3);

        double result = integrator.integrate(func);

        // ∫x dx от 0 до 5
        assertEquals(12.5, result, 1e-9);
    }

    @Test
    void testIntegrateSqrFunction() {
        var func = new ArrayTabulatedFunction(new SqrFunction(), 0, 3, 4);
        var integrator = new TabulatedIntegralOperator(2);

        double result = integrator.integrate(func);

        // ∫x² dx от 0 до 3
        assertEquals(9.5, result, 1e-1);
    }

    @Test
    void testIntegrateConstantFunction() {
        var func = new ArrayTabulatedFunction(new ConstantFunction(2.5), -2, 2, 5);
        var integrator = new TabulatedIntegralOperator(3);

        double result = integrator.integrate(func);
        // ∫2.5 dx от -2 до 2
        assertEquals(10, result, 1e-9);
    }

    @Test
    void testIntegrateLinearFunction() {
        MathFunction linear = x -> 2 * x + 3; // f(x) = 2x + 3
        var func = new ArrayTabulatedFunction(linear, 1, 4, 4);
        var integrator = new TabulatedIntegralOperator(2);

        double result = integrator.integrate(func);
        // ∫(2x + 3) dx от 1 до 4
        assertEquals(24, result, 1e-2);
    }

    @Test
    void testIntegrateSinFunction() {
        MathFunction sin = Math::sin;
        var func = new ArrayTabulatedFunction(sin, 0, Math.PI, 100);
        var integrator = new TabulatedIntegralOperator(20);

        double result = integrator.integrate(func);

        // ∫sin(x) dx от 0 до π
        assertEquals(2, result, 1e-2);
    }

    @Test
    void testIntegrateDifferentPartSizes() {
        var func = new ArrayTabulatedFunction(new IdentityFunction(), 0, 10, 1001);

        // Тестируем с разными размерами частей
        var integrator1 = new TabulatedIntegralOperator(10);
        var integrator2 = new TabulatedIntegralOperator(100);
        var integrator3 = new TabulatedIntegralOperator(1000);

        double result1 = integrator1.integrate(func);
        double result2 = integrator2.integrate(func);
        double result3 = integrator3.integrate(func);

        assertEquals(50, result1, 1e-2); // ∫x dx от 0 до 10
        assertEquals(50, result2, 1e-2);
        assertEquals(50, result3, 1e-2);
    }

    @Test
    void testIntegrateLinkedListTabulatedFunction() {
        double[] xValues = {0, 1, 2, 3};
        double[] yValues = {0, 1, 4, 9};
        var func = new LinkedListTabulatedFunction(xValues, yValues);
        var integrator = new TabulatedIntegralOperator(2);

        double result = integrator.integrate(func);

        // ∫x² dx от 0 до 3
        assertEquals(9.5, result, 1e-9);
    }

    @Test
    void testIntegrateArrayTabulatedFunction() {

        var func1 = new ArrayTabulatedFunction(new UnitFunction(), 0, 1, 2);
        var func2 = new ArrayTabulatedFunction(new IdentityFunction(), -5, 5, 11);
        var integrator = new TabulatedIntegralOperator(1);

        double result = integrator.integrate(func1);
        assertEquals(1, result, 1e-9);

        integrator = new TabulatedIntegralOperator(5);
        result = integrator.integrate(func2);
        assertEquals(0, result, 1e-9);
    }




}