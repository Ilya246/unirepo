package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.Test;

import ru.ssau.tk._AMEBA_._PESEZ_.exceptions.*;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;
import ru.ssau.tk._AMEBA_._PESEZ_.operations.*;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListTabulatedFunctionTest {



    private static class LinearFunction implements MathFunction {
        @Override
        public double apply(double x) {
            return 2 * x + 1;
        }
    }

    private double[] xValues = {0, 1, 2};
    private double[] yValues = {1, 3, 5};

    @Test
    void testConstructorWithMismatchedArrays() {
        // Несовпадающие размеры массивов
        assertThrows(DifferentLengthOfArraysException.class, () ->
                new LinkedListTabulatedFunction(new double[]{1, 2}, new double[]{1}));
    }

    @Test
    void testConstructorWithTooSmallArrays() {
        // Меньше 2 точек
        assertThrows(IllegalArgumentException.class, () ->
                new LinkedListTabulatedFunction(new double[]{1}, new double[]{1}));
    }

    @Test
    void testConstructorWithSmallCount() {
        // Меньше 2 точек
        assertThrows(IllegalArgumentException.class, () ->
                new LinkedListTabulatedFunction(new UnitFunction(), 1, 10, 1));
    }

    @Test
    void testConstructorWithUnsortedX() {
        // Неупорядоченные xValues
        assertThrows(ArrayIsNotSortedException.class, () ->
                new LinkedListTabulatedFunction(new double[]{2, 1}, new double[]{1, 2}));
    }

    @Test
    void testConstructorWithDuplicateX() {
        // Дублированные xValues
        assertThrows(ArrayIsNotSortedException.class, () ->
                new LinkedListTabulatedFunction(new double[]{2, 2}, new double[]{1, 2}));
    }

    @Test
    public void testArrays(){
        LinkedListTabulatedFunction func=new LinkedListTabulatedFunction(xValues,yValues);
        assertEquals(3, func.getCount());
        assertEquals(0, func.getX(0));
        assertEquals(1, func.getX(1));
        assertEquals(2, func.getX(2));

        assertEquals(1, func.getY(0));
        assertEquals(3, func.getY(1));
        assertEquals(5, func.getY(2));
    }

    @Test
    public void testFunction() {
        LinearFunction f = new LinearFunction();
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(f, 0, 2, 3);

        assertEquals(3, func.getCount());
        assertEquals(0, func.getX(0));
        assertEquals(1, func.getX(1));
        assertEquals(2, func.getX(2));

        assertEquals(1, func.getY(0));
        assertEquals(3, func.getY(1));
        assertEquals(5, func.getY(2));

        func = new LinkedListTabulatedFunction(f, 2, 0, 3);

        assertEquals(3, func.getCount());
        assertEquals(0, func.getX(0));
        assertEquals(1, func.getX(1));
        assertEquals(2, func.getX(2));

        assertEquals(1, func.getY(0));
        assertEquals(3, func.getY(1));
        assertEquals(5, func.getY(2));
    }

    @Test
    void floorIndexOfX() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xValues, yValues);
        assertEquals(0, func.floorIndexOfX(0)); // точное совпадение
        assertEquals(0, func.floorIndexOfX(0.5)); // между 0 и 1
        assertEquals(1, func.floorIndexOfX(1.5)); // между 1 и 2
        assertEquals(3, func.floorIndexOfX(3)); // больше правой границы
    }

    @Test
    void floorIndexOfXWithException() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xValues, yValues);
        assertThrows(IllegalArgumentException.class, () -> func.floorIndexOfX(-1));
    }

    @Test
    void extrapolateLeft() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xValues, yValues);
        assertEquals(-1, func.extrapolateLeft(-1)); // по формуле линейного продолжения
        assertEquals(1, func.extrapolateLeft(0));   // совпадение с первой точкой
    }

    @Test
    void extrapolateRight() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xValues, yValues);
        assertEquals(7, func.extrapolateRight(3));  // линейное продолжение вправо
        assertEquals(5, func.extrapolateRight(2));  // совпадение с последней точкой
    }

    @Test
    void interpolate() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xValues, yValues);
        assertEquals(2, func.interpolate(0.5, 0)); // между 0 и 1
        assertEquals(4, func.interpolate(1.5, 1)); // между 1 и 2
        assertEquals(1, func.interpolate(0, 0));   // точное совпадение с узлом
        assertEquals(5, func.interpolate(2, 1));   // точное совпадение с последним узлом
    }

    @Test
    void getXWithInvalidIndex() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xValues, yValues);
        assertThrows(IllegalArgumentException.class, () -> func.getX(-1));
        assertThrows(IllegalArgumentException.class, () -> func.getX(3));
    }

    @Test
    void getYWithInvalidIndex() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xValues, yValues);
        assertThrows(IllegalArgumentException.class, () -> func.getY(-1));
        assertThrows(IllegalArgumentException.class, () -> func.getY(3));
    }

    @Test
    void setYWithInvalidIndex() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xValues, yValues);
        assertThrows(IllegalArgumentException.class, () -> func.setY(-1, 10));
        assertThrows(IllegalArgumentException.class, () -> func.setY(3, 10));
    }

    @Test
    void getCount() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xValues, yValues);
        assertEquals(3, func.getCount());
    }

    @Test
    void getX() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xValues, yValues);
        assertEquals(0, func.getX(0));
        assertEquals(1, func.getX(1));
        assertEquals(2, func.getX(2));
    }

    @Test
    void getY() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xValues, yValues);
        assertEquals(1, func.getY(0));
        assertEquals(3, func.getY(1));
        assertEquals(5, func.getY(2));
    }

    @Test
    void setY() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xValues, yValues);
        func.setY(1, 10);
        assertEquals(10, func.getY(1));
    }

    @Test
    void indexOfX() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xValues, yValues);
        assertEquals(0, func.indexOfX(0));
        assertEquals(1, func.indexOfX(1));
        assertEquals(2, func.indexOfX(2));
        assertEquals(-1, func.indexOfX(5));
    }

    @Test
    void indexOfY() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xValues, yValues);
        assertEquals(0, func.indexOfY(1));
        assertEquals(1, func.indexOfY(3));
        assertEquals(2, func.indexOfY(5));
        assertEquals(-1, func.indexOfY(10));
    }

    @Test
    void leftBound() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xValues, yValues);
        assertEquals(0, func.leftBound());
    }

    @Test
    void rightBound() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xValues, yValues);
        assertEquals(2, func.rightBound());
    }

    @Test
    void testApply() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xValues, yValues);

        // Экстраполяция слева
        assertEquals(-1, func.apply(-1));

        // Точное совпадение с узлом
        assertEquals(1, func.apply(0));
        assertEquals(3, func.apply(1));
        assertEquals(5, func.apply(2));

        // Интерполяция
        assertEquals(2, func.apply(0.5));
        assertEquals(4, func.apply(1.5));

        // Экстраполяцию справа
        assertEquals(7, func.apply(3));
    }

    @Test
    void testApplyWithException() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xValues, yValues);
        assertDoesNotThrow(() -> func.apply(-10));
        assertDoesNotThrow(() -> func.apply(10));
    }

    @Test
    void testApplyOnEmpty() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(new UnitFunction(), 0, 1, 2);
        func.remove(0);
        func.remove(0);
        assertEquals(0, func.getCount());
        // head должна быть null
        assertThrows(NullPointerException.class, () -> func.apply(0));
    }

    @Test
    void testInsertOnEmpty() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(new UnitFunction(), 0, 1, 2);
        func.remove(0);
        func.remove(0);
        assertEquals(0, func.getCount());
        func.insert(10, 10);
        func.insert(11, 20);
        assertEquals(2, func.getCount());
        assertEquals(15, func.apply(10.5));
    }

    @Test
    void testInsertReplaceExistingX() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(
                new double[]{0, 1, 2}, new double[]{0, 10, 20});

        func.insert(1, 100);

        assertEquals(3, func.getCount());
        assertEquals(100, func.getY(1));
    }

    @Test
    void testInsertNewInside() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(
                new double[]{0, 2, 4}, new double[]{0, 20, 40});

        func.insert(3, 30);

        assertEquals(4, func.getCount());
        assertEquals(2, func.indexOfX(3));
        assertEquals(30, func.getY(2));
        assertEquals(40, func.getY(3));
    }

    @Test
    void testInsertNewAtStart() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(
                new double[]{1, 2, 3}, new double[]{10, 20, 30});

        func.insert(0, 0); // вставка в начало

        assertEquals(4, func.getCount());
        assertEquals(0, func.getX(0));
        assertEquals(0, func.getY(0));
        assertEquals(10, func.getY(1));
    }

    @Test
    void testInsertNewAtEnd() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(
                new double[]{1, 2, 3}, new double[]{10, 20, 30});

        func.insert(4, 40);

        assertEquals(4, func.getCount());
        assertEquals(4, func.getX(3));
        assertEquals(40, func.getY(3));
    }



    @Test
    void removeHead() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(
                new double[]{1.0, 2.0, 3.0}, new double[]{10.0, 20.0, 30.0}
        );
        func.remove(0); // удаляем голову
        assertEquals(2, func.getCount());
        assertEquals(2.0, func.getX(0));
        assertEquals(20.0, func.getY(0));
    }

    @Test
    void removeMiddle() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(
                new double[]{1.0, 2.0, 3.0}, new double[]{10.0, 20.0, 30.0}
        );
        func.remove(1); // удаляем середину
        assertEquals(2, func.getCount());
        assertEquals(1.0, func.getX(0));
        assertEquals(10.0, func.getY(0));
        assertEquals(3.0, func.getX(1));
        assertEquals(30.0, func.getY(1));
    }

    @Test
    void removeTail() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(
                new double[]{1.0, 2.0, 3.0}, new double[]{10.0, 20.0, 30.0}
        );
        func.remove(2);
        assertEquals(2, func.getCount());
        assertEquals(1.0, func.getX(0));
        assertEquals(10.0, func.getY(0));
        assertEquals(2.0, func.getX(1));
        assertEquals(20.0, func.getY(1));
    }

    @Test
    void removeInvalidIndex() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(
                new double[]{1.0, 2.0}, new double[]{10.0, 20.0}
        );
        assertThrows(IllegalArgumentException.class, () -> func.remove(-1));
        assertThrows(IllegalArgumentException.class, () -> func.remove(2));
    }

    @Test
    void testInterpolationException() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(
                new double[]{1, 2, 3}, new double[]{10, 20, 30});

        assertThrows(InterpolationException.class, () -> func.interpolate(2.5, 0));
        assertThrows(InterpolationException.class, () -> func.interpolate(0.5, 0));
    }
    @Test
    void testIterator() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(
                new double[]{1, 2, 3}, new double[]{10, 20, 30});

        var it = func.iterator();
        double val = 1;
        while (it.hasNext()) {
            Point point = it.next();
            assertEquals(point.getX(), val);
            assertEquals(point.getY(), val * 10);
            val += 1;
        }
        assertThrows(NoSuchElementException.class, () -> it.next());

        val = 1;
        for (Point point : func) {
            assertEquals(point.getX(), val);
            assertEquals(point.getY(), val * 10);
            val += 1;
        }

    }

    @Test
    void testAsPoints() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(
                new double[]{1, 2, 3}, new double[]{10, 20, 30});

        Point[] pts = TabulatedFunctionOperationService.asPoints(func);
        assertEquals(3, pts.length);
        assertEquals(1, pts[0].getX());
        assertEquals(2, pts[1].getX());
        assertEquals(3, pts[2].getX());
        assertEquals(10, pts[0].getY());
        assertEquals(20, pts[1].getY());
        assertEquals(30, pts[2].getY());
    }
}