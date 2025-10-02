package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.exceptions.*;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ArrayTabulatedFunctionTest {

    // Тесты конструкторов
    @Test
    void testConstructorWithSinglePoint() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            double[] x = {1.0};
            double[] y = {2.0};
            new ArrayTabulatedFunction(x, y);
        });
        assertTrue(exception.getMessage().contains("Length of tabulated function must be at least 2"));
    }

    @Test
    void testConstructorWithDifferentLengths() {
        DifferentLengthOfArraysException exception = assertThrows(DifferentLengthOfArraysException.class, () -> {
            double[] x = {1.0, 2.0};
            double[] y = {1.0};
            new ArrayTabulatedFunction(x, y);
        });
    }

    @Test
    void testConstructorWithUnsortedX() {
        ArrayIsNotSortedException exception = assertThrows(ArrayIsNotSortedException.class, () -> {
            double[] x = {2.0, 1.0, 3.0};
            double[] y = {2.0, 1.0, 3.0};
            new ArrayTabulatedFunction(x, y);
        });
    }

    @Test
    void testConstructorWithDuplicatedX() {
        ArrayIsNotSortedException exception = assertThrows(ArrayIsNotSortedException.class, () -> {
            double[] x = {1.0, 2.0, 2.0, 3.0};
            double[] y = {1.0, 4.0, 4.0, 9.0};
            new ArrayTabulatedFunction(x, y);
        });
    }

    @Test
    void testConstructorWithMathFunctionInvalidCount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            MathFunction source = x -> x * x;
            new ArrayTabulatedFunction(source, 0, 10, 1);
        });
        assertTrue(exception.getMessage().contains("Length of tabulated function must be at least 2"));
    }

    // Тесты методов доступа с некорректными индексами
    @Test
    void testGetXWithNegativeIndex() {
        double[] x = {1.0, 2.0};
        double[] y = {1.0, 4.0};
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(x, y);

        assertThrows(IllegalArgumentException.class, () -> func.getX(-1));
    }

    @Test
    void testGetXWithIndexEqualToCount() {
        double[] x = {1.0, 2.0};
        double[] y = {1.0, 4.0};
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(x, y);

        assertThrows(IllegalArgumentException.class, () -> func.getX(2));
    }

    @Test
    void testGetYWithNegativeIndex() {
        double[] x = {1.0, 2.0};
        double[] y = {1.0, 4.0};
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(x, y);

        assertThrows(IllegalArgumentException.class, () -> func.getY(-1));
    }

    @Test
    void testSetYWithInvalidIndex() {
        double[] x = {1.0, 2.0, 3.0};
        double[] y = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(x, y);

        assertThrows(IllegalArgumentException.class, () -> func.setY(5, 10.0));
    }

    @Test
    void testRemoveWithInvalidIndex() {
        double[] x = {1.0, 2.0};
        double[] y = {1.0, 4.0};
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(x, y);

        assertThrows(IllegalArgumentException.class, () -> func.remove(-1));
    }

    // Тесты корректной работы
    @Test
    void testValidConstructorAndBasicOperations() {
        double[] x = {1.0, 2.0, 3.0, 4.0};
        double[] y = {1.0, 4.0, 9.0, 16.0};
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(x, y);

        assertEquals(4, func.getCount());
        assertEquals(2.0, func.getX(1), 0.0001);
        assertEquals(4.0, func.getY(1), 0.0001);
        assertEquals(1.0, func.leftBound(), 0.0001);
        assertEquals(4.0, func.rightBound(), 0.0001);
    }

    @Test
    void testSetYValid() {
        double[] x = {1.0, 2.0, 3.0};
        double[] y = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(x, y);

        func.setY(1, 5.0);
        assertEquals(5.0, func.getY(1), 0.0001);
    }

    @Test
    void testIndexOfX() {
        double[] x = {1.0, 2.0, 3.0, 4.0};
        double[] y = {1.0, 4.0, 9.0, 16.0};
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(x, y);

        assertEquals(1, func.indexOfX(2.0));
        assertEquals(-1, func.indexOfX(2.5));
        assertEquals(0, func.indexOfX(1.0));
        assertEquals(3, func.indexOfX(4.0));
    }

    @Test
    void testIndexOfY() {
        double[] x = {1.0, 2.0, 3.0, 4.0};
        double[] y = {1.0, 4.0, 9.0, 16.0};
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(x, y);

        assertEquals(1, func.indexOfY(4.0));
        assertEquals(-1, func.indexOfY(5.0));
        assertEquals(0, func.indexOfY(1.0));
    }

    @Test
    void testInsert() {
        double[] x = {1.0, 3.0, 4.0};
        double[] y = {1.0, 9.0, 16.0};
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(x, y);

        // Вставка в начало
        func.insert(0.5, 0.25);
        assertEquals(4, func.getCount());
        assertEquals(0.5, func.getX(0), 0.0001);
        assertEquals(0.25, func.getY(0), 0.0001);

        // Вставка в конец
        func.insert(5.0, 25.0);
        assertEquals(5, func.getCount());
        assertEquals(5.0, func.getX(4), 0.0001);
        assertEquals(25.0, func.getY(4), 0.0001);

        // Вставка в середину
        func.insert(2.0, 4.0);
        assertEquals(6, func.getCount());
        assertEquals(2.0, func.getX(2), 0.0001);
        assertEquals(4.0, func.getY(2), 0.0001);

        // Обновление существующей точки
        func.insert(2.0, 5.0);
        assertEquals(6, func.getCount()); // Количество не должно измениться
        assertEquals(5.0, func.getY(2), 0.0001); // Значение должно обновиться
    }

    @Test
    void testRemove() {
        double[] x = {1.0, 2.0, 3.0, 4.0};
        double[] y = {1.0, 4.0, 9.0, 16.0};
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(x, y);

        func.remove(1); // Удаляем второй элемент (x=2.0, y=4.0)

        assertEquals(3, func.getCount());
        assertEquals(1.0, func.getX(0), 0.0001);
        assertEquals(3.0, func.getX(1), 0.0001);
        assertEquals(4.0, func.getX(2), 0.0001);
        assertEquals(9.0, func.getY(1), 0.0001);
    }


    // Тесты интерполяции и экстраполяции
    @Test
    void testInterpolation() {
        double[] x = {1.0, 2.0, 3.0};
        double[] y = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(x, y);

        // Интерполяция между точками
        double result = func.apply(1.5);
        assertEquals(2.5, result, 0.0001); // Линейная интерполяция между 1 и 4

        // Экстраполяция слева
        double leftExtrapolation = func.apply(0.5);
        assertTrue(leftExtrapolation < 1.0);

        // Экстраполяция справа
        double rightExtrapolation = func.apply(3.5);
        assertTrue(rightExtrapolation > 9.0);
    }

    // Тест на floorIndexOfX через косвенные методы
    @Test
    void testFloorIndexOfXDirectly() {
        double[] x = {1.0, 2.0, 3.0, 4.0};
        double[] y = {1.0, 4.0, 9.0, 16.0};

        // Создаем тестовый класс для доступа к protected методу
        class TestArrayTabulatedFunction extends ArrayTabulatedFunction {
            TestArrayTabulatedFunction(double[] xValues, double[] yValues) {
                super(xValues, yValues);
            }

            public int testFloorIndexOfX(double x) {
                return super.floorIndexOfX(x);
            }
        }

        TestArrayTabulatedFunction func = new TestArrayTabulatedFunction(x, y);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            func.testFloorIndexOfX(0.5);
        });
        assertTrue(exception.getMessage().contains("x is less than left bound"));
    }


    @Test
    void testBasicFunctionality() {
        // Тестирование базового функционала: создание функции, границы, доступ к значениям
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(new double[] {0, 1, 2, 5}, new double[] {0, -1, 2, -4});

        assertEquals(4, func.getCount());
        assertEquals(0, func.leftBound());
        assertEquals(5, func.rightBound());
        assertEquals(0, func.getX(0));
        assertEquals(0, func.getY(0));
        assertEquals(2, func.getX(2));
        assertEquals(2, func.getY(2));

        // Тестирование изменения значения Y
        func.setY(2, 4);
        assertEquals(4, func.getY(2));
        func.setY(2, 2);
        assertEquals(2, func.getY(2));

        // Тестирование поиска индексов
        assertEquals(0, func.indexOfX(0));
        assertEquals(2, func.indexOfX(2));
        assertEquals(3, func.indexOfX(5));
        assertEquals(0, func.indexOfY(0));
        assertEquals(2, func.indexOfY(2));
        assertEquals(3, func.indexOfY(-4));

        // Тестирование интерполяции между точками
        assertEquals(0, func.apply(0));
        assertEquals(-0.5, func.apply(0.5));
        assertEquals(0.5, func.apply(1.5));
        assertEquals(2, func.apply(2));
        assertEquals(0, func.apply(3));
        assertEquals(-2, func.apply(4));
        assertEquals(-4, func.apply(5));
    }

    @Test
    void testConstructorWithMathFunction() {
        // Тестирование конструктора с математической функцией
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(new IdentityFunction(), 1, 5, 4);

        assertEquals(4, func.getCount());
        assertEquals(1, func.leftBound());
        assertEquals(5, func.rightBound());
        assertEquals(1, func.getX(0));
        assertEquals(1, func.getY(0));

        // Тестирование экстраполяции слева и справа
        assertEquals(0, func.apply(0));
        assertEquals(0.5, func.apply(0.5));
        assertEquals(0.25, func.apply(0.25));
        assertEquals(2, func.apply(2));
        assertEquals(2.5, func.apply(2.5));
        assertEquals(512, func.apply(512));
        assertEquals(-512, func.apply(-512));
    }

    @Test
    void testInsertReplaceExistingX() {
        // Тестирование вставки с заменой существующего значения X
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(
                new double[]{0, 1, 2}, new double[]{0, 10, 20});

        func.insert(1, 100); // Должно заменить существующее значение Y для X=1

        assertEquals(3, func.getCount()); // Количество точек не должно измениться
        assertEquals(100, func.getY(1)); // Значение должно обновиться
    }

    @Test
    void testInsertNewInside() {
        // Тестирование вставки новой точки в середину массива
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(
                new double[]{0, 2, 4}, new double[]{0, 20, 40});

        func.insert(3, 30); // Вставка между 2 и 4

        assertEquals(4, func.getCount()); // Количество точек должно увеличиться
        assertEquals(2, func.indexOfX(3)); // Новая точка должна быть на позиции 2
        assertEquals(30, func.getY(2)); // Проверка значения новой точки
        assertEquals(40, func.getY(3)); // Проверка, что старая точка сдвинулась
    }

    @Test
    void testInsertNewAtStart() {
        // Тестирование вставки новой точки в начало массива
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(
                new double[]{1, 2, 3}, new double[]{10, 20, 30});

        func.insert(0, 0); // вставка в начало

        assertEquals(4, func.getCount());
        assertEquals(0, func.getX(0)); // Новая точка должна быть первой
        assertEquals(0, func.getY(0));
        assertEquals(10, func.getY(1)); // Старая первая точка должна сдвинуться
    }

    @Test
    void testInsertNewAtEnd() {
        // Тестирование вставки новой точки в конец массива
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(
                new double[]{1, 2, 3}, new double[]{10, 20, 30});

        func.insert(4, 40); // вставка в конец

        assertEquals(4, func.getCount());
        assertEquals(4, func.getX(3)); // Новая точка должна быть последней
        assertEquals(40, func.getY(3));
    }

    @Test
    void testDeleteAtBeginning() {
        // Тестирование удаления точек с начала массива
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(
                new double[]{1, 2, 3}, new double[]{10, 20, 30});

        // Проверка начального состояния
        assertEquals(1, func.getX(0));
        assertEquals(10, func.getY(0));
        assertEquals(2, func.getX(1));
        assertEquals(20, func.getY(1));
        assertEquals(3, func.getX(2));
        assertEquals(30, func.getY(2));

        // Удаление первой точки
        func.remove(0);
        assertEquals(2, func.getCount());
        assertEquals(2, func.getX(0)); // Теперь первая точка должна быть 2
        assertEquals(20, func.getY(0));
        assertEquals(3, func.getX(1));
        assertEquals(30, func.getY(1));

        // Удаление следующей точки
        func.remove(0);
        assertEquals(1, func.getCount());
        assertEquals(3, func.getX(0)); // Осталась только последняя точка
        assertEquals(30, func.getY(0));
    }

    @Test
    void testDeleteInMiddleAndEnd() {
        // Тестирование удаления точек из середины и конца массива
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(
                new double[]{1, 2, 3}, new double[]{10, 20, 30});

        // Проверка начального состояния
        assertEquals(1, func.getX(0));
        assertEquals(10, func.getY(0));
        assertEquals(2, func.getX(1));
        assertEquals(20, func.getY(1));
        assertEquals(3, func.getX(2));
        assertEquals(30, func.getY(2));

        // Удаление из середины
        func.remove(1);
        assertEquals(2, func.getCount());
        assertEquals(1, func.getX(0)); // Первая точка остается
        assertEquals(10, func.getY(0));
        assertEquals(3, func.getX(1)); // Третья точка сдвигается на место второй
        assertEquals(30, func.getY(1));

        // Удаление с конца
        func.remove(1);
        assertEquals(1, func.getCount());
        assertEquals(1, func.getX(0)); // Осталась только первая точка
        assertEquals(10, func.getY(0));
    }

    @Test
    void testInterpolationException() {
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(
                new double[]{1, 2, 3}, new double[]{10, 20, 30});

        assertThrows(InterpolationException.class, () -> func.interpolate(2.5, 0));
        assertThrows(InterpolationException.class, () -> func.interpolate(0.5, 0));
    }

    @Test
    void testIterator() {
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(
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

}
