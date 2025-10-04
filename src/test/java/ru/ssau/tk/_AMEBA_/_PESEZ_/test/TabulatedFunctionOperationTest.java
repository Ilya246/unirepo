package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.exceptions.InconsistentFunctionsException;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.factory.*;
import ru.ssau.tk._AMEBA_._PESEZ_.operations.TabulatedFunctionOperationService;

import static org.junit.jupiter.api.Assertions.*;

class TabulatedFunctionOperationTest {

    @Test
    void testFunctionality() {
        var funcA = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{10, 20, 30});
        var funcB = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{15, 10, 5});

        var service = new TabulatedFunctionOperationService();

        TabulatedFunction sumFunc = service.add(funcA, funcB);
        assertEquals(3, sumFunc.getCount());
        assertEquals(25, sumFunc.getY(0));
        assertEquals(30, sumFunc.getY(1));
        assertEquals(35, sumFunc.getY(2));

        TabulatedFunction subFunc = service.subtract(funcA, funcB);
        assertEquals(3, subFunc.getCount());
        assertEquals(-5, subFunc.getY(0));
        assertEquals(10, subFunc.getY(1));
        assertEquals(25, subFunc.getY(2));
    }

    @Test
    void testSumLinkedListArray() {
        var funcA = new LinkedListTabulatedFunction(new double[]{1, 2, 3}, new double[]{10, 20, 30});
        var funcB = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{15, 10, 5});

        var service = new TabulatedFunctionOperationService();

        TabulatedFunction sumFunc = service.add(funcA, funcB);
        assertEquals(3, sumFunc.getCount());
        assertEquals(25, sumFunc.getY(0));
        assertEquals(30, sumFunc.getY(1));
        assertEquals(35, sumFunc.getY(2));

        TabulatedFunction subFunc = service.subtract(funcA, funcB);
        assertEquals(3, subFunc.getCount());
        assertEquals(-5, subFunc.getY(0));
        assertEquals(10, subFunc.getY(1));
        assertEquals(25, subFunc.getY(2));
    }

    @Test
    void testSumArrayLinkedList() {
        var funcA = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{10, 20, 30});
        var funcB = new LinkedListTabulatedFunction(new double[]{1, 2, 3}, new double[]{15, 10, 5});

        var service = new TabulatedFunctionOperationService();

        TabulatedFunction sumFunc = service.add(funcA, funcB);
        assertEquals(3, sumFunc.getCount());
        assertEquals(25, sumFunc.getY(0));
        assertEquals(30, sumFunc.getY(1));
        assertEquals(35, sumFunc.getY(2));

        TabulatedFunction subFunc = service.subtract(funcA, funcB);
        assertEquals(3, subFunc.getCount());
        assertEquals(-5, subFunc.getY(0));
        assertEquals(10, subFunc.getY(1));
        assertEquals(25, subFunc.getY(2));
    }

    @Test
    void testSumToLinkedList() {
        var funcA = new LinkedListTabulatedFunction(new double[]{1, 2, 3}, new double[]{10, 20, 30});
        var funcB = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{15, 10, 5});

        var service = new TabulatedFunctionOperationService(new LinkedListTabulatedFunctionFactory());

        TabulatedFunction sumFunc = service.add(funcA, funcB);
        assertInstanceOf(LinkedListTabulatedFunction.class, sumFunc);
        assertEquals(3, sumFunc.getCount());
        assertEquals(25, sumFunc.getY(0));
        assertEquals(30, sumFunc.getY(1));
        assertEquals(35, sumFunc.getY(2));

        TabulatedFunction subFunc = service.subtract(funcA, funcB);
        assertInstanceOf(LinkedListTabulatedFunction.class, subFunc);
        assertEquals(3, subFunc.getCount());
        assertEquals(-5, subFunc.getY(0));
        assertEquals(10, subFunc.getY(1));
        assertEquals(25, subFunc.getY(2));
    }

    @Test
    void testSumToArray() {
        var funcA = new LinkedListTabulatedFunction(new double[]{1, 2, 3}, new double[]{10, 20, 30});
        var funcB = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{15, 10, 5});

        var service = new TabulatedFunctionOperationService(new ArrayTabulatedFunctionFactory());

        TabulatedFunction sumFunc = service.add(funcA, funcB);
        assertInstanceOf(ArrayTabulatedFunction.class, sumFunc);
        assertEquals(3, sumFunc.getCount());
        assertEquals(25, sumFunc.getY(0));
        assertEquals(30, sumFunc.getY(1));
        assertEquals(35, sumFunc.getY(2));

        TabulatedFunction subFunc = service.subtract(funcA, funcB);
        assertInstanceOf(ArrayTabulatedFunction.class, subFunc);
        assertEquals(3, subFunc.getCount());
        assertEquals(-5, subFunc.getY(0));
        assertEquals(10, subFunc.getY(1));
        assertEquals(25, subFunc.getY(2));
    }

    @Test
    void testGetFactory() {
        var factory = new ArrayTabulatedFunctionFactory();
        var service = new TabulatedFunctionOperationService(factory);
        assertEquals(factory, service.getFactory());
    }

    @Test
    void testSetFactory() {
        var funcA = new LinkedListTabulatedFunction(new double[]{1, 2, 3}, new double[]{10, 20, 30});
        var funcB = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{15, 10, 5});

        var service = new TabulatedFunctionOperationService(new ArrayTabulatedFunctionFactory());

        TabulatedFunction sumFunc = service.add(funcA, funcB);
        assertInstanceOf(ArrayTabulatedFunction.class, sumFunc);
        assertEquals(3, sumFunc.getCount());
        assertEquals(25, sumFunc.getY(0));
        assertEquals(30, sumFunc.getY(1));
        assertEquals(35, sumFunc.getY(2));

        service.setFactory(new LinkedListTabulatedFunctionFactory());
        TabulatedFunction subFunc = service.subtract(funcA, funcB);
        assertInstanceOf(LinkedListTabulatedFunction.class, subFunc);
        assertEquals(3, subFunc.getCount());
        assertEquals(-5, subFunc.getY(0));
        assertEquals(10, subFunc.getY(1));
        assertEquals(25, subFunc.getY(2));
    }

    @Test
    void testLengthError() {
        var funcA = new LinkedListTabulatedFunction(new double[]{1, 2, 3}, new double[]{10, 20, 30});
        var funcB = new ArrayTabulatedFunction(new double[]{1, 2}, new double[]{15, 10});

        var service = new TabulatedFunctionOperationService(new ArrayTabulatedFunctionFactory());
        assertThrows(InconsistentFunctionsException.class, () -> service.add(funcA, funcB));
        assertThrows(InconsistentFunctionsException.class, () -> service.subtract(funcA, funcB));
    }

    @Test
    void testMismatchError() {
        var funcA = new LinkedListTabulatedFunction(new double[]{1, 2, 3}, new double[]{10, 20, 30});
        var funcB = new ArrayTabulatedFunction(new double[]{1, 2, 4}, new double[]{15, 10, 5});

        var service = new TabulatedFunctionOperationService(new ArrayTabulatedFunctionFactory());
        assertThrows(InconsistentFunctionsException.class, () -> service.add(funcA, funcB));
        assertThrows(InconsistentFunctionsException.class, () -> service.subtract(funcA, funcB));
    }
}