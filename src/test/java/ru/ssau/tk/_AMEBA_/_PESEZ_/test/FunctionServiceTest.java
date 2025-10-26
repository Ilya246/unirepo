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

    // Финальная очистка
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

    @Test
    void testMathTabulatedCompositeFunction() throws SQLException {
        String exprInner = "sin(x)";
        String exprOuter = "x^2";
        // Пишем в базу данных
        int idInner = FunctionService.createMathFunction(exprInner, database);
        int idOuter = FunctionService.createTabulated(exprOuter, -1, 1, 100, database);
        int idComposite = FunctionService.createComposite(idInner, idOuter, database);

        MathFunction function = FunctionService.getFunction(idComposite, database);
        assertInstanceOf(CompositeFunction.class, function);
        assertEquals(0, function.apply(0), 0.05);
        assertEquals(0.25, function.apply(Math.PI / 6), 0.05);
        assertEquals(0.5, function.apply(Math.PI / 4), 0.05);
        assertEquals(0.5, function.apply(-Math.PI / 4), 0.05);
        assertEquals(1, function.apply(Math.PI / 2), 0.05);
        assertEquals(1, function.apply(-Math.PI / 2), 0.05);
    }

    @Test
    void testUpdateComposite() throws SQLException {
        String exprInner = "2x+7";
        String exprOuter = "0.5x-0.5";
        String exprOuterNew = "0.5x-2.5";
        // Пишем в базу данных
        int idInner = FunctionService.createMathFunction(exprInner, database);
        int idOuter = FunctionService.createMathFunction(exprOuter, database);
        int idOuterNew = FunctionService.createMathFunction(exprOuterNew, database);
        int idComposite = FunctionService.createComposite(idInner, idOuter, database);

        MathFunction function = FunctionService.getFunction(idComposite, database);
        assertEquals(3, function.apply(0), 0.05);
        assertEquals(5, function.apply(2), 0.05);
        assertEquals(17.2, function.apply(14.2), 0.05);
        assertEquals(-2.2, function.apply(-5.2), 0.05);
        assertEquals(-7, function.apply(-10), 0.05);

        FunctionService.updateComposite(idComposite, null, idOuterNew, database);
        function = FunctionService.getFunction(idComposite, database);
        assertEquals(1, function.apply(0), 0.05);
        assertEquals(3, function.apply(2), 0.05);
        assertEquals(15.2, function.apply(14.2), 0.05);
        assertEquals(-4.2, function.apply(-5.2), 0.05);
        assertEquals(-9, function.apply(-10), 0.05);

    }

    @Test
    void testUpdatePoint() throws SQLException {
        String expr = "2x*sin(x)";
        // Пишем в базу данных
        int id = FunctionService.createTabulated(expr, -10, 10, 100, database);

        MathFunction function = FunctionService.getFunction(id, database);
        var tabulated = (TabulatedFunction)function;
        double ptX = tabulated.getX(50);
        double ptY = tabulated.apply(ptX);

        FunctionService.updatePoint(id, ptX, 10, database);
        function = FunctionService.getFunction(id, database);
        assertEquals(10, function.apply(ptX));
        assertNotEquals(ptY, function.apply(ptX));
    }

    @Test
    void testDeletePoint() throws SQLException {
        String expr = "2x*sin(x)";
        // Пишем в базу данных
        int id = FunctionService.createTabulated(expr, -10, 10, 100, database);

        MathFunction function = FunctionService.getFunction(id, database);
        var tabulated = (TabulatedFunction)function;
        assertEquals(100, tabulated.getCount());
        double ptX = tabulated.getX(50);

        FunctionService.deletePoint(id, ptX, database);
        function = FunctionService.getFunction(id, database);
        tabulated = (TabulatedFunction)function;
        assertEquals(99, tabulated.getCount());
    }
}