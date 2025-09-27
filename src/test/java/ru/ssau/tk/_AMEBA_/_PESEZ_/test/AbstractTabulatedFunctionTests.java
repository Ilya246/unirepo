package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.Test;

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
}