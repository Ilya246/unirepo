package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.concurrent.MultiplyingTask;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;
import ru.ssau.tk._AMEBA_._PESEZ_.concurrent.SynchronizedTabulatedFunction;

import java.util.*;

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

    @Test
    void testDoSynchronously() {
        ArrayTabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues.clone(), yValues.clone());
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(arrayFunc);

        syncFunc.doSynchronously(func -> {
            realAssert(func);
            return null;
        });

        double sum = syncFunc.doSynchronously(func -> {
            double s = 0;
            for (Point p : func) {
                s += p.getY();
            }
            return s;
        });
        assertEquals(10 + 20 + 30, sum);
    }

    @Test
    void testDoSynchronouslyMultithreaded() throws InterruptedException {
        int count = 10000;
        ArrayTabulatedFunction arrayFunc = new ArrayTabulatedFunction(new UnitFunction(), 1, count, count);
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(arrayFunc);

        List<Thread> threads = new ArrayList<>();

        int power = 10;
        for (int i = 0; i < power; i++) {
            var thread = new Thread(() -> {
                syncFunc.doSynchronously(func -> {
                    for (int j = 0; j < count; j++) {
                        func.setY(j, func.getY(j) * 2);
                    }
                    return null;
                });
            });
            threads.add(thread);
        }
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }

        double expected = 1 << power;
        for (Point p : syncFunc) {
            assertEquals(expected, p.getY(), 0.1);
        }
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
}