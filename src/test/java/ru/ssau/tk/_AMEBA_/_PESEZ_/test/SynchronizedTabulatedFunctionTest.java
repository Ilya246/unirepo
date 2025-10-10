package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;
import ru.ssau.tk._AMEBA_._PESEZ_.concurrent.SynchronizedTabulatedFunction;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class SynchronizedTabulatedFunctionTest {
    double[] xValues = new double[]{1, 2, 3};
    double[] yValues = new double[]{10, 20, 30};

    @Test
    void testWrapArrayTable() {
        ArrayTabulatedFunction _func = new ArrayTabulatedFunction(xValues.clone(), yValues.clone());
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(_func);

        realAssert(syncFunc);
    }

    @Test
    void testWrapLinkedListTable() {
        LinkedListTabulatedFunction _func = new LinkedListTabulatedFunction(xValues.clone(), yValues.clone());
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(_func);

        realAssert(syncFunc);
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
        func.setY(0, 5);
        assertEquals(5, func.getY(0));
        func.setY(0, 10);
    }

    void realAssert(TabulatedFunction func) {
        integerAssert(func);
        assertEquals(-5, func.apply(-0.5));
        assertEquals(25, func.apply(2.5));
        assertEquals(45, func.apply(4.5));
    }

    @Test
    void testIteratorWithArrayFunction() {
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(
                new double[]{1.0, 2.0, 3.0},
                new double[]{10.0, 20.0, 30.0}
        );
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        Iterator<Point> iterator = syncFunction.iterator();

        assertTrue(iterator.hasNext());
        Point point1 = iterator.next();
        assertEquals(1.0, point1.getX(), 1e-10);
        assertEquals(10.0, point1.getY(), 1e-10);

        assertTrue(iterator.hasNext());
        Point point2 = iterator.next();
        assertEquals(2.0, point2.getX(), 1e-10);
        assertEquals(20.0, point2.getY(), 1e-10);

        assertTrue(iterator.hasNext());
        Point point3 = iterator.next();
        assertEquals(3.0, point3.getX(), 1e-10);
        assertEquals(30.0, point3.getY(), 1e-10);

        assertFalse(iterator.hasNext());
    }

    @Test
    void testIteratorWithLinkedListFunction() {
        TabulatedFunction linkedListFunction = new LinkedListTabulatedFunction(
                new double[]{0.5, 1.5, 2.5},
                new double[]{5.0, 15.0, 25.0}
        );
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(linkedListFunction);

        int count = 0;
        for (Point point : syncFunction) {
            assertNotNull(point);
            count++;
        }
        assertEquals(3, count);
    }


    @Test
    void testIteratorIndependenceFromFunctionChanges() {
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(
                new double[]{1.0, 2.0},
                new double[]{10.0, 20.0}
        );
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        // Получаем итератор
        Iterator<Point> iterator = syncFunction.iterator();

        // Изменяем функцию
        syncFunction.setY(0, 15.0);
        syncFunction.setY(1, 25.0);

        // Итератор должен работать со старыми значениями (копией)
        Point point1 = iterator.next();
        assertEquals(1.0, point1.getX(), 1e-10);
        assertEquals(10.0, point1.getY(), 1e-10); // Старое значение

        Point point2 = iterator.next();
        assertEquals(2.0, point2.getX(), 1e-10);
        assertEquals(20.0, point2.getY(), 1e-10); // Старое значение
    }


    @Test
    void testIteratorNoSuchElementException() {
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(
                new double[]{1.0, 2.0},
                new double[]{10.0, 20.0}
        );
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        Iterator<Point> iterator = syncFunction.iterator();


        assertTrue(iterator.hasNext());
        iterator.next();

        assertTrue(iterator.hasNext());
        iterator.next();


        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }



}