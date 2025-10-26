package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.MathFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.service.FunctionService;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class FunctionServiceTest {

    @Test
    void testMathFunction() throws SQLException {
        String expr = "2x+3";
        // Пишем в базу данных
        int id = FunctionService.createMathFunction(expr);

        MathFunction function = FunctionService.getFunction(id);
        assertEquals(5, function.apply(1));
        assertEquals(1, function.apply(-1));

        FunctionService.deleteFunction(id);
    }
}