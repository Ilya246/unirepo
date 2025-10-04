package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.LinkedListTabulatedFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.StrictTabulatedFunction;

import static org.junit.jupiter.api.Assertions.*;

class StrictTabulatedFunctionTest {
    @Test
    void testArrayTabulatedFunctionWrapper() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};

        ArrayTabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues, yValues);
        StrictTabulatedFunction strictFunc = new StrictTabulatedFunction(arrayFunc);

        // Проверяем делегирование методов
        assertEquals(3, strictFunc.getCount());
        assertEquals(1.0, strictFunc.getX(0));

        assertEquals(10.0, strictFunc.getY(0));

        assertEquals(1.0, strictFunc.leftBound());
        assertEquals(3.0, strictFunc.rightBound());

        assertEquals(0, strictFunc.indexOfX(1.0));
        assertEquals(-1, strictFunc.indexOfX(5.0));

        assertEquals(0, strictFunc.indexOfY(10.0));
        assertEquals(-1, strictFunc.indexOfY(50.0));

        // Проверяем изменение значения
        strictFunc.setY(1, 25.0);
        assertEquals(25.0, strictFunc.getY(1));
        assertEquals(25.0, arrayFunc.getY(1));
    }

    @Test
    void testLinkedListTabulatedFunctionWrapper() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};

        LinkedListTabulatedFunction linkedListFunc = new LinkedListTabulatedFunction(xValues, yValues);
        StrictTabulatedFunction strictFunc = new StrictTabulatedFunction(linkedListFunc);

        // Проверяем делегирование методов
        assertEquals(3, strictFunc.getCount());
        assertEquals(1.0, strictFunc.getX(0));

        assertEquals(10.0, strictFunc.getY(0));

        assertEquals(1.0, strictFunc.leftBound());
        assertEquals(3.0, strictFunc.rightBound());

        assertEquals(0, strictFunc.indexOfX(1.0));
        assertEquals(-1, strictFunc.indexOfX(5.0));

        assertEquals(0, strictFunc.indexOfY(10.0));
        assertEquals(-1, strictFunc.indexOfY(50.0));

        // Проверяем изменение значения
        strictFunc.setY(1, 25.0);
        assertEquals(25.0, strictFunc.getY(1));
        assertEquals(25.0, linkedListFunc.getY(1));
    }

    @Test
    void testApply() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};

        ArrayTabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues, yValues);
        StrictTabulatedFunction strictFunc1 = new StrictTabulatedFunction(arrayFunc);

        LinkedListTabulatedFunction linkedListFunc = new LinkedListTabulatedFunction(xValues, yValues);
        StrictTabulatedFunction strictFunc2 = new StrictTabulatedFunction(linkedListFunc);

        //Массивы
        assertEquals(10.0, strictFunc1.apply(1.0));
        assertEquals(20.0, strictFunc1.apply(2.0));
        assertEquals(30.0, strictFunc1.apply(3.0));

        assertThrows(UnsupportedOperationException.class, () -> strictFunc1.apply(0.5));
        assertThrows(UnsupportedOperationException.class, () -> strictFunc1.apply(1.5));
        assertThrows(UnsupportedOperationException.class, () -> strictFunc1.apply(2.5));
        assertThrows(UnsupportedOperationException.class, () -> strictFunc1.apply(4.0));

        //Списки
        assertEquals(10.0, strictFunc2.apply(1.0));
        assertEquals(20.0, strictFunc2.apply(2.0));
        assertEquals(30.0, strictFunc2.apply(3.0));

        assertThrows(UnsupportedOperationException.class, () -> strictFunc2.apply(0.5));
        assertThrows(UnsupportedOperationException.class, () -> strictFunc2.apply(2.5));
    }

    @Test
    void testMutability() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};

        ArrayTabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues, yValues);
        StrictTabulatedFunction strictFunc = new StrictTabulatedFunction(arrayFunc);

        // Изменения через обёртку должны отражаться в оригинале
        strictFunc.setY(0, 15.0);
        assertEquals(15.0, strictFunc.getY(0));
        assertEquals(15.0, arrayFunc.getY(0));

        // Изменения в оригинале должны отражаться в обёртке
        arrayFunc.setY(1, 25.0);
        assertEquals(25.0, strictFunc.getY(1));
    }



}