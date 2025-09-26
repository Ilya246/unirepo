package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.ConstantFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.UnitFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.ZeroFunction;

import static org.junit.jupiter.api.Assertions.*;

class ConstantFunctionsTest {

    @Test
    void test() {
        ConstantFunction f1 = new ConstantFunction(5);
        assertEquals(5, f1.getConstant());
        assertEquals(5, f1.apply(123435));

        ZeroFunction zf = new ZeroFunction();
        assertEquals(0, zf.getConstant());
        assertEquals(0, zf.apply(-15000));

        UnitFunction uf = new UnitFunction();
        assertEquals(1, uf.getConstant());
        assertEquals(1, uf.apply(1));
    }
}