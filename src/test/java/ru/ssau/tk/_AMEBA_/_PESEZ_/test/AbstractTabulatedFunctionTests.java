package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;

import static org.junit.jupiter.api.Assertions.*;

class AbstractTabulatedFunctionTests {
    @Test
    void interpolate() {
        MockTabulatedFunction func = new MockTabulatedFunction(0, 0, 0, 0);

        assertEquals(5, func.interpolate(-5, -5, -15, 5, 15));
        assertEquals(15, func.interpolate(-15, -5, -15, 5, 15));
        assertEquals(10, func.interpolate(-10, -5, -15, 5, 15));
        assertEquals(0, func.interpolate(0, -5, -15, 5, 15));
        assertEquals(20, func.interpolate(-20, -5, -15, 5, 15));
    }

    @Test
    void apply() {
        MockTabulatedFunction func = new MockTabulatedFunction(0, 0, 3, 5);

        assertEquals(0, func.apply(0));
        assertEquals(5, func.apply(3));
        assertEquals(2.5, func.apply(1.5));
        assertEquals(-5, func.apply(-3));
        assertEquals(10, func.apply(6));

        func = new MockTabulatedFunction(-2, -32, -17, -2);

        assertEquals(-32, func.apply(-2));
        assertEquals(-2, func.apply(-17));
        assertEquals(-17, func.apply(-9.5));
        assertEquals(28, func.apply(-32));
        assertEquals(-62, func.apply(13));
    }
    @Test
    void testToString() {
        var function = new ArrayTabulatedFunction(new double[]{1.0, 2.0, 3.0}, new double[]{10.0, 20.0, 30.0});
        var result = function.toString();

        assertEquals("ArrayTabulatedFunction size = 3\n[1.0; 10.0]\n[2.0; 20.0]\n[3.0; 30.0]", result);
    }

    @Test
    void testEquals() {
        var function = new ArrayTabulatedFunction(new double[]{1.0, 2.0, 3.0}, new double[]{10.0, 20.0, 30.0});

        assertNotEquals(function, 0);
        assertEquals(function, function);

        // одна функция, разные типы (равны)
        var function2 = new LinkedListTabulatedFunction(new double[]{1.0, 2.0, 3.0}, new double[]{10.0, 20.0, 30.0});
        assertEquals(function, function2);

        // разное Y
        var function3 = new LinkedListTabulatedFunction(new double[]{1.0, 2.0, 3.0}, new double[]{11.0, 20.0, 30.0});
        assertNotEquals(function, function3);

        // разное X
        var function4 = new LinkedListTabulatedFunction(new double[]{1.0, 2.0, 3.0}, new double[]{11.0, 20.0, 30.0});
        assertNotEquals(function, function4);

        // лишняя точка
        var function5 = new LinkedListTabulatedFunction(new double[]{1.0, 2.0, 3.0, 4.0}, new double[]{11.0, 20.0, 30.0, 40.0});
        assertNotEquals(function, function5);
    }

    @Test
    void testIterator() {
        MockTabulatedFunction func = new MockTabulatedFunction(0, 0, 3, 5);
        assertThrows(UnsupportedOperationException.class, func::iterator);
    }
}