package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.factory.*;

import static org.junit.jupiter.api.Assertions.*;

class TabulatedFunctionFactoryTest {

    @Test
    void testCreateArray() {
        var factory = new ArrayTabulatedFunctionFactory();

        TabulatedFunction function = testFactory(factory);

        assertInstanceOf(ArrayTabulatedFunction.class, function);
    }

    @Test
    void testCreateLinkedList() {
        var factory = new LinkedListTabulatedFunctionFactory();

        TabulatedFunction function = testFactory(factory);

        assertInstanceOf(LinkedListTabulatedFunction.class, function);
    }

    @Test
    void testCreateUnmodifiableArray() {
        var factory = new ArrayTabulatedFunctionFactory();

        testUnmodifiable(factory);
    }

    @Test
    void testCreateStrict() {
        var factory = new ArrayTabulatedFunctionFactory();

        testStrict(factory);
    }

    @Test
    void testCreateStrictUnmodifiable() {
        var factory = new ArrayTabulatedFunctionFactory();
        testStrictUnmodifiable(factory);
    }

    @Test
    void testCreateUnmodifiableList() {
        var factory = new LinkedListTabulatedFunctionFactory();

        testUnmodifiable(factory);
    }

    TabulatedFunction testFactory(TabulatedFunctionFactory factory) {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};

        TabulatedFunction function = factory.create(xValues, yValues);
        testFunction(function);

        return function;
    }

    void testUnmodifiable(TabulatedFunctionFactory factory) {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};

        TabulatedFunction function = factory.createUnmodifiable(xValues, yValues);

        testFunction(function);
        assertThrows(UnsupportedOperationException.class, () -> function.setY(0, 5));
    }

    void testStrict(TabulatedFunctionFactory factory){
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        TabulatedFunction function=factory.createStrict(xValues, yValues);

        testFunction(function);
        assertThrows(UnsupportedOperationException.class, () -> function.apply(1.5));

    }

    void testStrictUnmodifiable(TabulatedFunctionFactory factory) {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};

        TabulatedFunction function = factory.createStrictUnmodifiable(xValues, yValues);

        testFunction(function);
        assertThrows(UnsupportedOperationException.class, () -> function.apply(1.5));
        assertThrows(UnsupportedOperationException.class, () -> function.setY(0, 15.0));
    }


    void testFunction(TabulatedFunction function) {
        assertEquals(3, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(2.0, function.getX(1));
        assertEquals(3.0, function.getX(2));
        assertEquals(10.0, function.getY(0));
        assertEquals(20.0, function.getY(1));
        assertEquals(30.0, function.getY(2));
    }
}