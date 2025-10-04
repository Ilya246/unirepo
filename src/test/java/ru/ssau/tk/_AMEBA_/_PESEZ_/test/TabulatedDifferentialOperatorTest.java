package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.LinkedListTabulatedFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.TabulatedFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.factory.LinkedListTabulatedFunctionFactory;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.factory.TabulatedFunctionFactory;
import ru.ssau.tk._AMEBA_._PESEZ_.operations.TabulatedDifferentialOperator;

import static org.junit.jupiter.api.Assertions.*;

class TabulatedDifferentialOperatorTest {

    @Test
    void testDeriveWithLinearFunctionAndArrayFactory() {
        // Тестируем линейную функцию y = 2x + 3
        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(factory);

        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {3.0, 5.0, 7.0, 9.0, 11.0}; // y = 2x + 3

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);

        // Производная линейной функции постоянна и равна 2
        assertTrue(derivative instanceof ArrayTabulatedFunction);
        assertEquals(5, derivative.getCount());

        for (int i = 0; i < derivative.getCount(); i++) {
            assertEquals(2.0, derivative.getY(i), 1e-10, "At index " + i);
        }
    }
    @Test
    void testDeriveWithLinearFunctionAndLinkedListFactory() {
        // Тестируем линейную функцию y = -x + 5
        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(factory);

        double[] xValues = {-2.0, -1.0, 0.0, 1.0, 2.0};
        double[] yValues = {7.0, 6.0, 5.0, 4.0, 3.0}; // y = -x + 5

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);

        // Производная линейной функции постоянна и равна -1
        assertTrue(derivative instanceof LinkedListTabulatedFunction);
        assertEquals(5, derivative.getCount());

        for (int i = 0; i < derivative.getCount(); i++) {
            assertEquals(-1.0, derivative.getY(i), 1e-10, "At index " + i);
        }
    }
    @Test
    void testDeriveWithQuadraticFunction() {
        // Тестируем квадратичную функцию y = x²
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0}; // y = x²

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);

        // Производная y = x² равна y' = 2x
        // Численные значения:
        // x=0 -> 0, x=1 -> 2, x=2 -> 4, x=3 -> 6, x=4 -> 8
        // Но из-за численного дифференцирования:
        assertEquals(5, derivative.getCount());
        assertEquals(1.0, derivative.getY(0), 1e-10);  // (1-0)/(1-0) = 1
        assertEquals(3.0, derivative.getY(1), 1e-10);  // (4-1)/(2-1) = 3
        assertEquals(5.0, derivative.getY(2), 1e-10);  // (9-4)/(3-2) = 5
        assertEquals(7.0, derivative.getY(3), 1e-10);  // (16-9)/(4-3) = 7
        assertEquals(7.0, derivative.getY(4), 1e-10);  // последнее равно предпоследнему
    }
    @Test
    void testDeriveWithTwoPoints() {
        // Тестируем минимальное количество точек (2)
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        double[] xValues = {1.0, 2.0};
        double[] yValues = {3.0, 7.0}; // y = 4x - 1

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedFunction derivative = operator.derive(function);

        assertEquals(2, derivative.getCount());
        assertEquals(4.0, derivative.getY(0), 1e-10);
        assertEquals(4.0, derivative.getY(1), 1e-10);
    }



}