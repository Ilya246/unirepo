package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.*;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;
import ru.ssau.tk._AMEBA_._PESEZ_.service.*;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class FunctionServiceTest {
    static DatabaseConnection database = new DatabaseConnection("jdbc:postgresql://localhost:5432/function_db_test");

    @BeforeAll
    static void ensureTables() {
        FunctionService.ensureTables(database);
    }

    // Финальная очистка на случай провала тестов
    @AfterAll
    static void cleanup() throws SQLException {
        database.executeUpdate("DELETE FROM points");
        database.executeUpdate("DELETE FROM composite_function");
        database.executeUpdate("DELETE FROM function");
    }

    @Test
    void testMathFunction() throws SQLException {
        String expr = "2x+3";
        // Пишем в базу данных
        int id = FunctionService.createMathFunction(expr, database);

        MathFunction function = FunctionService.getFunction(id, database);
        assertEquals(5, function.apply(1));
        assertEquals(1, function.apply(-1));

        FunctionService.deleteFunction(id, database);
    }

    @Test
    void testTabulatedFunction() throws SQLException {
        String expr = "2x*sin(x)";
        // Пишем в базу данных
        int id = FunctionService.createTabulated(expr, -10, 10, 100, database);

        MathFunction function = FunctionService.getFunction(id, database);
        assertInstanceOf(TabulatedFunction.class, function);
        assertEquals(0, function.apply(0), 0.05);
        assertEquals(3.38, function.apply(1.71), 0.05);
        assertEquals(3.38, function.apply(-1.71), 0.05);
    }

    @Test
    void testCompositeFunction() throws SQLException {
        String exprInner = "sin(x)";
        String exprOuter = "2*asin(x)";
        // Пишем в базу данных
        int idInner = FunctionService.createMathFunction(exprInner, database);
        int idOuter = FunctionService.createMathFunction(exprOuter, database);
        int idComposite = FunctionService.createComposite(idInner, idOuter, database);

        MathFunction function = FunctionService.getFunction(idComposite, database);
        assertInstanceOf(CompositeFunction.class, function);
        assertEquals(0, function.apply(0), 0.05);
        assertEquals(0.2, function.apply(0.1), 0.05);
        assertEquals(2.4, function.apply(1.2), 0.05);
        assertEquals(-2.6, function.apply(-1.3), 0.05);
        assertEquals(3, function.apply(1.5), 0.05);
    }
}