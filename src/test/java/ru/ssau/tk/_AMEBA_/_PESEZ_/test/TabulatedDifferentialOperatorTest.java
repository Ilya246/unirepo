package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.factory.*;
import ru.ssau.tk._AMEBA_._PESEZ_.operations.TabulatedDifferentialOperator;

import static org.junit.jupiter.api.Assertions.*;

class TabulatedDifferentialOperatorTest {

    @Test
    void testDeriveWithLinearFunctionAndArrayFactory() {
        var operator = new TabulatedDifferentialOperator(new ArrayTabulatedFunctionFactory());
        var function = new ArrayTabulatedFunction(new double[]{0.0, 1.0, 2.0, 3.0, 4.0}, new double[]{3.0, 5.0, 7.0, 9.0, 11.0});

        var derivative = operator.derive(function);

        assertInstanceOf(ArrayTabulatedFunction.class, derivative);
        assertEquals(2.0, derivative.getY(0), 1e-10);
        assertEquals(2.0, derivative.getY(1), 1e-10);
        assertEquals(2.0, derivative.getY(2), 1e-10);
        assertEquals(2.0, derivative.getY(3), 1e-10);
        assertEquals(2.0, derivative.getY(4), 1e-10);
    }

    @Test
    void testDeriveWithLinearFunctionAndLinkedListFactory() {
        var operator = new TabulatedDifferentialOperator(new LinkedListTabulatedFunctionFactory());
        var function = new LinkedListTabulatedFunction(new double[]{-2.0, -1.0, 0.0, 1.0, 2.0}, new double[]{7.0, 6.0, 5.0, 4.0, 3.0});

        var derivative = operator.derive(function);

        assertInstanceOf(LinkedListTabulatedFunction.class, derivative);
        assertEquals(-1.0, derivative.getY(0), 1e-10);
        assertEquals(-1.0, derivative.getY(1), 1e-10);
        assertEquals(-1.0, derivative.getY(2), 1e-10);
        assertEquals(-1.0, derivative.getY(3), 1e-10);
        assertEquals(-1.0, derivative.getY(4), 1e-10);
    }

    @Test
    void testDeriveWithQuadraticFunction() {
        var operator = new TabulatedDifferentialOperator();
        var function = new ArrayTabulatedFunction(new double[]{0.0, 1.0, 2.0, 3.0, 4.0}, new double[]{0.0, 1.0, 4.0, 9.0, 16.0});

        var derivative = operator.derive(function);

        assertEquals(1.0, derivative.getY(0), 1e-10);
        assertEquals(3.0, derivative.getY(1), 1e-10);
        assertEquals(5.0, derivative.getY(2), 1e-10);
        assertEquals(7.0, derivative.getY(3), 1e-10);
        assertEquals(7.0, derivative.getY(4), 1e-10);
    }

    @Test
    void testDeriveWithTwoPoints() {
        var operator = new TabulatedDifferentialOperator();
        var function = new ArrayTabulatedFunction(new double[]{1.0, 2.0}, new double[]{3.0, 7.0});

        var derivative = operator.derive(function);

        assertEquals(4.0, derivative.getY(0), 1e-10);
        assertEquals(4.0, derivative.getY(1), 1e-10);
    }

    @Test
    void testGetFactory() {
        var factory = new ArrayTabulatedFunctionFactory();
        var operator = new TabulatedDifferentialOperator(factory);
        TabulatedFunctionFactory gotFactory = operator.getFactory();
        assertSame(factory, gotFactory);
    }

    @Test
    void testSetFactory() {
        var factory = new ArrayTabulatedFunctionFactory();
        var operator = new TabulatedDifferentialOperator(factory);
        assertInstanceOf(ArrayTabulatedFunctionFactory.class, operator.getFactory());

        operator.setFactory(new LinkedListTabulatedFunctionFactory());
        assertInstanceOf(LinkedListTabulatedFunctionFactory.class, operator.getFactory());
    }
}