package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;

import static org.junit.jupiter.api.Assertions.*;

class UnmodifiableTabulatedFunctionTest {
    double[] xValues = new double[]{1, 2, 3};
    double[] yValues = new double[]{10, 20, 30};

    @Test
    void testWrapArrayTable() {
        ArrayTabulatedFunction _func = new ArrayTabulatedFunction(xValues.clone(), yValues.clone());
        UnmodifiableTabulatedFunction constFunc = new UnmodifiableTabulatedFunction(_func);

        realAssert(constFunc);

        assertThrows(UnsupportedOperationException.class, () -> constFunc.setY(0, 15));
    }

    @Test
    void testWrapLinkedListTable() {
        LinkedListTabulatedFunction _func = new LinkedListTabulatedFunction(xValues.clone(), yValues.clone());
        UnmodifiableTabulatedFunction constFunc = new UnmodifiableTabulatedFunction(_func);

        realAssert(constFunc);

        assertThrows(UnsupportedOperationException.class, () -> constFunc.setY(0, 15));
    }

    @Test
    void testWrapStrictTable() {
        LinkedListTabulatedFunction _func = new LinkedListTabulatedFunction(xValues.clone(), yValues.clone());
        StrictTabulatedFunction _strict = new StrictTabulatedFunction(_func);
        UnmodifiableTabulatedFunction wrapFunc = new UnmodifiableTabulatedFunction(_strict);

        integerAssert(wrapFunc);

        assertThrows(UnsupportedOperationException.class, () -> wrapFunc.setY(0, 15));
        assertThrows(UnsupportedOperationException.class, () -> wrapFunc.apply(1.5));
        assertThrows(UnsupportedOperationException.class, () -> wrapFunc.apply(-0.5));
        assertThrows(UnsupportedOperationException.class, () -> wrapFunc.apply(4.5));
    }

    void integerAssert(TabulatedFunction func) {
        int i = 1;
        for (Point p : func) {
            assertEquals(p.getX(), i);
            assertEquals(p.getY(), i * 10);
            i++;
        }
        assertEquals(10, func.apply(1));
        assertEquals(20, func.apply(2));
        assertEquals(30, func.apply(3));
        assertEquals(1, func.leftBound());
        assertEquals(3, func.rightBound());
        assertEquals(2, func.getX(1));
        assertEquals(20, func.getY(1));
        assertEquals(1, func.indexOfX(2));
        assertEquals(1, func.indexOfY(20));
        assertEquals(3, func.getCount());
    }

    void realAssert(TabulatedFunction func) {
        integerAssert(func);
        assertEquals(-5, func.apply(-0.5));
        assertEquals(25, func.apply(2.5));
        assertEquals(45, func.apply(4.5));
    }
}