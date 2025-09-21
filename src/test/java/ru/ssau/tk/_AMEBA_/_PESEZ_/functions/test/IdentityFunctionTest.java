package ru.ssau.tk._AMEBA_._PESEZ_.functions.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.main.IdentityFunction;

import static org.junit.jupiter.api.Assertions.*;

class IdentityFunctionTest {

    @AfterEach
    void tearDown() {
    }

    @Test
    void apply() {
        IdentityFunction id = new IdentityFunction();
        double x=3.5;
        double result=id.apply(x);
        assertEquals(x,result);
    }
}