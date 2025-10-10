package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;

import static org.junit.jupiter.api.Assertions.*;

class StrictTabulatedFunctionTest {
    @Test
    void testArrayTabulatedFunctionWrapper() {
        var arrayFunc = new ArrayTabulatedFunction(new double[]{1.0, 2.0, 3.0}, new double[]{10.0, 20.0, 30.0});
        var strictFunc = new StrictTabulatedFunction(arrayFunc);

        assertEquals(3, strictFunc.getCount());
        assertEquals(1.0, strictFunc.getX(0));
        assertEquals(10.0, strictFunc.getY(0));
        assertEquals(1.0, strictFunc.leftBound());
        assertEquals(3.0, strictFunc.rightBound());
        assertEquals(0, strictFunc.indexOfX(1.0));
        assertEquals(-1, strictFunc.indexOfX(5.0));
        assertEquals(0, strictFunc.indexOfY(10.0));
        assertEquals(-1, strictFunc.indexOfY(50.0));

        strictFunc.setY(1, 25.0);
        assertEquals(25.0, strictFunc.getY(1));
        assertEquals(25.0, arrayFunc.getY(1));
    }

    @Test
    void testLinkedListTabulatedFunctionWrapper() {
        var linkedListFunc = new LinkedListTabulatedFunction(new double[]{1.0, 2.0, 3.0}, new double[]{10.0, 20.0, 30.0});
        var strictFunc = new StrictTabulatedFunction(linkedListFunc);

        assertEquals(3, strictFunc.getCount());
        assertEquals(1.0, strictFunc.getX(0));
        assertEquals(10.0, strictFunc.getY(0));
        assertEquals(1.0, strictFunc.leftBound());
        assertEquals(3.0, strictFunc.rightBound());
        assertEquals(0, strictFunc.indexOfX(1.0));
        assertEquals(-1, strictFunc.indexOfX(5.0));
        assertEquals(0, strictFunc.indexOfY(10.0));
        assertEquals(-1, strictFunc.indexOfY(50.0));

        strictFunc.setY(1, 25.0);
        assertEquals(25.0, strictFunc.getY(1));
        assertEquals(25.0, linkedListFunc.getY(1));
    }

    @Test
    void testApply() {
        var arrayFunc = new ArrayTabulatedFunction(new double[]{1.0, 2.0, 3.0}, new double[]{10.0, 20.0, 30.0});
        var linkedListFunc = new LinkedListTabulatedFunction(new double[]{1.0, 2.0, 3.0}, new double[]{10.0, 20.0, 30.0});

        var strictFunc1 = new StrictTabulatedFunction(arrayFunc);
        var strictFunc2 = new StrictTabulatedFunction(linkedListFunc);

        assertEquals(10.0, strictFunc1.apply(1.0));
        assertEquals(20.0, strictFunc1.apply(2.0));
        assertEquals(30.0, strictFunc1.apply(3.0));

        assertThrows(UnsupportedOperationException.class, () -> strictFunc1.apply(0.5));
        assertThrows(UnsupportedOperationException.class, () -> strictFunc1.apply(1.5));
        assertThrows(UnsupportedOperationException.class, () -> strictFunc1.apply(2.5));
        assertThrows(UnsupportedOperationException.class, () -> strictFunc1.apply(4.0));

        assertEquals(10.0, strictFunc2.apply(1.0));
        assertEquals(20.0, strictFunc2.apply(2.0));
        assertEquals(30.0, strictFunc2.apply(3.0));

        assertThrows(UnsupportedOperationException.class, () -> strictFunc2.apply(0.5));
        assertThrows(UnsupportedOperationException.class, () -> strictFunc2.apply(2.5));
    }

    @Test
    void testMutability() {
        var arrayFunc = new ArrayTabulatedFunction(new double[]{1.0, 2.0, 3.0}, new double[]{10.0, 20.0, 30.0});
        var strictFunc = new StrictTabulatedFunction(arrayFunc);

        strictFunc.setY(0, 15.0);
        assertEquals(15.0, strictFunc.getY(0));
        assertEquals(15.0, arrayFunc.getY(0));

        arrayFunc.setY(1, 25.0);
        assertEquals(25.0, strictFunc.getY(1));
    }

    @Test
    void testStrictAndUnmodifiableCombination() {
        var arrayFunc = new ArrayTabulatedFunction(new double[]{1.0, 2.0, 3.0}, new double[]{10.0, 20.0, 30.0});
        var unmodifiableFunc = new UnmodifiableTabulatedFunction(arrayFunc);
        var strictUnmodifiable = new StrictTabulatedFunction(unmodifiableFunc);

        assertEquals(3, strictUnmodifiable.getCount());
        assertEquals(1.0, strictUnmodifiable.getX(0));
        assertEquals(10.0, strictUnmodifiable.getY(0));

        assertThrows(UnsupportedOperationException.class, () -> strictUnmodifiable.setY(0, 15.0));
        assertThrows(UnsupportedOperationException.class, () -> strictUnmodifiable.apply(1.5));
    }

    @Test
    void testStrictUnmodifiableWithLinkedList() {
        var linkedListFunc = new LinkedListTabulatedFunction(new double[]{1.0, 2.0, 3.0}, new double[]{10.0, 20.0, 30.0});
        var unmodifiableFunc = new UnmodifiableTabulatedFunction(linkedListFunc);
        var strictUnmodifiable = new StrictTabulatedFunction(unmodifiableFunc);

        assertEquals(3, strictUnmodifiable.getCount());
        assertEquals(1.0, strictUnmodifiable.getX(0));
        assertEquals(10.0, strictUnmodifiable.getY(0));

        assertThrows(UnsupportedOperationException.class, () -> strictUnmodifiable.setY(0, 15.0));
        assertThrows(UnsupportedOperationException.class, () -> strictUnmodifiable.apply(1.5));
    }

    @Test
    void testIterator() {
        var arrayFunc = new ArrayTabulatedFunction(new double[]{1.0, 2.0, 3.0}, new double[]{10.0, 20.0, 30.0});
        var strictFunc = new StrictTabulatedFunction(arrayFunc);

        int i = 1;
        for (Point p : strictFunc) {
            assertEquals(p.getX(), i);
            assertEquals(p.getY(), i * 10);
            i++;
        }
    }

}