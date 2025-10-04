package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.LinkedListTabulatedFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.TabulatedFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.factory.LinkedListTabulatedFunctionFactory;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListTabulatedFunctionFactoryTest {


    @Test
    void testCreate() {
        LinkedListTabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();

        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};

        TabulatedFunction function = factory.create(xValues, yValues);

        // Проверяем, что созданный объект является LinkedListTabulatedFunction
        assertTrue(function instanceof LinkedListTabulatedFunction);

        // Проверяем корректность данных
        assertEquals(3, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(2.0, function.getX(1));
        assertEquals(3.0, function.getX(2));
        assertEquals(10.0, function.getY(0));
        assertEquals(20.0, function.getY(1));
        assertEquals(30.0, function.getY(2));
    }
}