package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.LinkedListTabulatedFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.MathFunction;

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
    }

    @Test
    void floorIndexOfX() {
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xValues, yValues);
        assertEquals(0, func.floorIndexOfX(0)); // точное совпадение
        assertEquals(0, func.floorIndexOfX(0.5)); // между 0 и 1
        assertEquals(1, func.floorIndexOfX(1.5)); // между 1 и 2
        assertEquals(3, func.floorIndexOfX(3)); // больше правой границы
        assertEquals(0, func.floorIndexOfX(-1)); // меньше левой границы
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
        double[] xValues = {0, 1, 2};
        double[] yValues = {1, 3, 5};
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
}