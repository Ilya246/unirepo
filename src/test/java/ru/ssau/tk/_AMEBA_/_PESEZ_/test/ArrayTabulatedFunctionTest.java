package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ArrayTabulatedFunctionTest {
    @Test
    void test() {
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(new double[] {0, 1, 2, 5}, new double[] {0, -1, 2, -4});

        assertEquals(4, func.getCount());
        assertEquals(0, func.leftBound());
        assertEquals(5, func.rightBound());
        assertEquals(0, func.getX(0));
        assertEquals(0, func.getY(0));
        assertEquals(2, func.getX(2));
        assertEquals(2, func.getY(2));
        func.setY(2, 4);
        assertEquals(4, func.getY(2));
        func.setY(2, 2);
        assertEquals(2, func.getY(2));
        assertEquals(0, func.indexOfX(0));
        assertEquals(2, func.indexOfX(2));
        assertEquals(3, func.indexOfX(5));
        assertEquals(0, func.indexOfY(0));
        assertEquals(2, func.indexOfY(2));
        assertEquals(3, func.indexOfY(-4));

        assertEquals(0, func.apply(0));
        assertEquals(-0.5, func.apply(0.5));
        assertEquals(0.5, func.apply(1.5));
        assertEquals(2, func.apply(2));
        assertEquals(0, func.apply(3));
        assertEquals(-2, func.apply(4));
        assertEquals(-4, func.apply(5));

        func = new ArrayTabulatedFunction(new IdentityFunction(), 1, 5, 4);

        assertEquals(4, func.getCount());
        assertEquals(1, func.leftBound());
        assertEquals(5, func.rightBound());
        assertEquals(1, func.getX(0));
        assertEquals(1, func.getY(0));

        assertEquals(0, func.apply(0));
        assertEquals(0.5, func.apply(0.5));
        assertEquals(0.25, func.apply(0.25));
        assertEquals(2, func.apply(2));
        assertEquals(2.5, func.apply(2.5));
        assertEquals(512, func.apply(512));
        assertEquals(-512, func.apply(-512));

        func = new ArrayTabulatedFunction(new double[] {1}, new double[] {1});

        assertEquals(1, func.getCount());
        assertEquals(1, func.leftBound());
        assertEquals(1, func.rightBound());
        assertEquals(1, func.apply(-1));
        assertEquals(1, func.apply(0));
        assertEquals(1, func.apply(1));
        assertEquals(1, func.apply(2));
    }
    @Test
    void testInsertReplaceExistingX() {
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(
                new double[]{0, 1, 2}, new double[]{0, 10, 20});

        func.insert(1, 100);

        assertEquals(3, func.getCount());
        assertEquals(100, func.getY(1));
    }

    @Test
    void testInsertNewInside() {
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(
                new double[]{0, 2, 4}, new double[]{0, 20, 40});

        func.insert(3, 30);

        assertEquals(4, func.getCount());
        assertEquals(2, func.indexOfX(3));
        assertEquals(30, func.getY(2));
        assertEquals(40, func.getY(3));
    }

    @Test
    void testInsertNewAtStart() {
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(
                new double[]{1, 2, 3}, new double[]{10, 20, 30});

        func.insert(0, 0); // вставка в начало

        assertEquals(4, func.getCount());
        assertEquals(0, func.getX(0));
        assertEquals(0, func.getY(0));
        assertEquals(10, func.getY(1));
    }

    @Test
    void testInsertNewAtEnd() {
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(
                new double[]{1, 2, 3}, new double[]{10, 20, 30});

        func.insert(4, 40);

        assertEquals(4, func.getCount());
        assertEquals(4, func.getX(3));
        assertEquals(40, func.getY(3));
    }

}