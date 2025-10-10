package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;
import ru.ssau.tk._AMEBA_._PESEZ_.operations.*;

import static org.junit.jupiter.api.Assertions.*;

class SteppingDifferentialOperatorTest {
    @Test
    void testLeftStep() {
        var op = new LeftSteppingDifferentialOperator(0.005);
        testDerivatives(op);
    }

    @Test
    void testRightStep() {
        var op = new RightSteppingDifferentialOperator(0.005);
        testDerivatives(op);
    }

    @Test
    void testMiddleStep() {
        var op = new MiddleSteppingDifferentialOperator(0.005);
        testDerivatives(op);
    }

    @Test
    void testExceptions() {
        var op = new MiddleSteppingDifferentialOperator(0.005);
        assertDoesNotThrow(() -> op.setStep(0.5));
        assertThrows(IllegalArgumentException.class, () -> op.setStep(-1));
        assertThrows(IllegalArgumentException.class, () -> op.setStep(0));
        assertThrows(IllegalArgumentException.class, () -> op.setStep(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> op.setStep(Double.POSITIVE_INFINITY));
        assertThrows(IllegalArgumentException.class, () -> op.setStep(Double.NEGATIVE_INFINITY));
    }

    void testDerivatives(SteppingDifferentialOperator op) {
        testSqrDerivative(op);
        testIdentityDerivative(op);
        testTabulatedDerivative(op);
        testDoubleDerivative(op);

        testErrors(op);
    }

    void testErrors(SteppingDifferentialOperator op) {
        op.setStep(0.5);
        assertThrows(IllegalArgumentException.class, () -> op.setStep(-1));
        assertThrows(IllegalArgumentException.class, () -> op.setStep(0));
        assertThrows(IllegalArgumentException.class, () -> op.setStep(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> op.setStep(Double.POSITIVE_INFINITY));
        assertThrows(IllegalArgumentException.class, () -> op.setStep(Double.NEGATIVE_INFINITY));
        assertEquals(0.5, op.getStep());
    }

    void testSqrDerivative(SteppingDifferentialOperator op) {
        var fun = new SqrFunction();
        var derivative = op.derive(fun);
        // (x^2)' = 2x
        assertEquals(2, derivative.apply(1), 0.01);
        assertEquals(4, derivative.apply(2), 0.02);
        assertEquals(8, derivative.apply(4), 0.04);
        assertEquals(16, derivative.apply(8), 0.08);
        assertEquals(-2, derivative.apply(-1), 0.01);
    }

    void testIdentityDerivative(SteppingDifferentialOperator op) {
        var fun = new IdentityFunction();
        var derivative = op.derive(fun);
        // (x)' = 1
        assertEquals(1, derivative.apply(2), 0.01);
        assertEquals(1, derivative.apply(4), 0.01);
        assertEquals(1, derivative.apply(8), 0.01);
        assertEquals(1, derivative.apply(16), 0.01);
        assertEquals(1, derivative.apply(-2), 0.01);
        assertEquals(1, derivative.apply(3539388), 0.01);
    }

    void testTabulatedDerivative(SteppingDifferentialOperator op) {
        var fun = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{10, 15, 30});
        var derivative = op.derive(fun);
        assertEquals(5, derivative.apply(-2), 0.01);
        assertEquals(5, derivative.apply(1.5), 0.01);
        assertEquals(15, derivative.apply(2.5), 0.01);
        assertEquals(15, derivative.apply(5), 0.01);
    }

    void testDoubleDerivative(SteppingDifferentialOperator op) {
        var fun = new SqrFunction().andThen(new SqrFunction());
        var _derivative = op.derive(fun);
        var derivative = op.derive(_derivative);
        // (x^4)' = 4x^3
        // (4x^3)' = 12x^2
        assertEquals(48, derivative.apply(-2), 0.4);
        assertEquals(27, derivative.apply(1.5), 0.3);
        assertEquals(48, derivative.apply(2), 0.4);
        assertEquals(192, derivative.apply(4), 0.8);
    }
}