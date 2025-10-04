package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;

import static org.junit.jupiter.api.Assertions.*;

class UnmodifiableTabulatedFunctionTest {

    @Test
    void testErrorArrayTable() {
        ArrayTabulatedFunction _func = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{10, 20, 30});
        UnmodifiableTabulatedFunction constFunc = new UnmodifiableTabulatedFunction(_func);

        assertThrows(UnsupportedOperationException.class, () -> constFunc.setY(0, 15));
    }

    @Test
    void testFunctionalityArrayTable() {
        ArrayTabulatedFunction _func = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{10, 20, 30});
        UnmodifiableTabulatedFunction constFunc = new UnmodifiableTabulatedFunction(_func);

        assertEquals(10, constFunc.apply(1));
        assertEquals(30, constFunc.apply(3));
        assertEquals(-5, constFunc.apply(-0.5));
        assertEquals(25, constFunc.apply(2.5));
        assertEquals(45, constFunc.apply(4.5));
        assertEquals(1, constFunc.leftBound());
        assertEquals(3, constFunc.rightBound());
        assertEquals(2, constFunc.getX(1));
        assertEquals(20, constFunc.getY(1));
        assertEquals(1, constFunc.indexOfX(2));
        assertEquals(3, constFunc.getCount());
    }

    @Test
    void testErrorLinkedListTable() {
        LinkedListTabulatedFunction _func = new LinkedListTabulatedFunction(new double[]{1, 2, 3}, new double[]{10, 20, 30});
        UnmodifiableTabulatedFunction constFunc = new UnmodifiableTabulatedFunction(_func);

        assertThrows(UnsupportedOperationException.class, () -> constFunc.setY(0, 15));
    }

    @Test
    void testFunctionalityLinkedListTable() {
        LinkedListTabulatedFunction _func = new LinkedListTabulatedFunction(new double[]{1, 2, 3}, new double[]{10, 20, 30});
        UnmodifiableTabulatedFunction constFunc = new UnmodifiableTabulatedFunction(_func);

        assertEquals(10, constFunc.apply(1));
        assertEquals(30, constFunc.apply(3));
        assertEquals(-5, constFunc.apply(-0.5));
        assertEquals(25, constFunc.apply(2.5));
        assertEquals(45, constFunc.apply(4.5));
        assertEquals(1, constFunc.leftBound());
        assertEquals(3, constFunc.rightBound());
        assertEquals(2, constFunc.getX(1));
        assertEquals(20, constFunc.getY(1));
        assertEquals(1, constFunc.indexOfX(2));
        assertEquals(3, constFunc.getCount());
    }
}