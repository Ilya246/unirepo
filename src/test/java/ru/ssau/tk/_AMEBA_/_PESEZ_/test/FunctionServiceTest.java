package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.*;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;
import ru.ssau.tk._AMEBA_._PESEZ_.service.*;

import java.sql.SQLException;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class FunctionServiceTest {
    static ThreadLocal<DatabaseConnection> database;

    @BeforeAll
    static void setup() {
        database = ThreadLocal.withInitial(() -> new DatabaseConnection("jdbc:postgresql://localhost:5432/function_db_test"));
        FunctionService.ensureTables(database);
    }

    // Финальная очистка
    @AfterAll
    static void cleanup() throws SQLException {
        database.get().executeUpdate("DELETE FROM points");
        database.get().executeUpdate("DELETE FROM composite_function");
        database.get().executeUpdate("DELETE FROM function");
    }

    @Test
    void testMathFunction() throws InterruptedException, ExecutionException {
        String expr = "2x+3";
        // Пишем в базу данных
        int id = FunctionService.createMathFunction(expr, database).get();

        MathFunction function = FunctionService.getFunction(id, database).get();
        assertEquals(5, function.apply(1));
        assertEquals(1, function.apply(-1));

        FunctionService.deleteFunction(id, database);
    }

    @Test
    void testTabulatedFunction() throws InterruptedException, ExecutionException {
        String expr = "2x*sin(x)";
        // Пишем в базу данных
        int id = FunctionService.createTabulated(expr, -10, 10, 100, database).get();

        MathFunction function = FunctionService.getFunction(id, database).get();
        assertInstanceOf(TabulatedFunction.class, function);
        assertEquals(0, function.apply(0), 0.05);
        assertEquals(3.38, function.apply(1.71), 0.05);
        assertEquals(3.38, function.apply(-1.71), 0.05);
    }

    @Test
    void testCompositeFunction() throws InterruptedException, ExecutionException {
        String exprInner = "sin(x)";
        String exprOuter = "2*asin(x)";
        // Пишем в базу данных
        CompletableFuture<Integer> idInner = FunctionService.createMathFunction(exprInner, database);
        CompletableFuture<Integer> idOuter = FunctionService.createMathFunction(exprOuter, database);
        CompletableFuture<Integer> idComposite = FunctionService.createComposite(idInner.get(), idOuter.get(), database);

        MathFunction function = FunctionService.getFunction(idComposite.get(), database).get();
        assertInstanceOf(CompositeFunction.class, function);
        assertEquals(0, function.apply(0), 0.05);
        assertEquals(0.2, function.apply(0.1), 0.05);
        assertEquals(2.4, function.apply(1.2), 0.05);
        assertEquals(-2.6, function.apply(-1.3), 0.05);
        assertEquals(3, function.apply(1.5), 0.05);
    }

    @Test
    void testMathTabulatedCompositeFunction() throws InterruptedException, ExecutionException {
        String exprInner = "sin(x)";
        String exprOuter = "x^2";
        // Пишем в базу данных
        CompletableFuture<Integer> idInner = FunctionService.createMathFunction(exprInner, database);
        CompletableFuture<Integer> idOuter = FunctionService.createTabulated(exprOuter, -1, 1, 100, database);
        CompletableFuture<Integer> idComposite = FunctionService.createComposite(idInner.get(), idOuter.get(), database);

        MathFunction function = FunctionService.getFunction(idComposite.get(), database).get();
        assertInstanceOf(CompositeFunction.class, function);
        assertEquals(0, function.apply(0), 0.05);
        assertEquals(0.25, function.apply(Math.PI / 6), 0.05);
        assertEquals(0.5, function.apply(Math.PI / 4), 0.05);
        assertEquals(0.5, function.apply(-Math.PI / 4), 0.05);
        assertEquals(1, function.apply(Math.PI / 2), 0.05);
        assertEquals(1, function.apply(-Math.PI / 2), 0.05);
    }

    @Test
    void testUpdateComposite() throws InterruptedException, ExecutionException {
        String exprInner = "2x+7";
        String exprOuter = "0.5x-0.5";
        String exprOuterNew = "0.5x-2.5";
        // Пишем в базу данных
        CompletableFuture<Integer> idInner = FunctionService.createMathFunction(exprInner, database);
        CompletableFuture<Integer> idOuter = FunctionService.createMathFunction(exprOuter, database);
        CompletableFuture<Integer> idOuterNew = FunctionService.createMathFunction(exprOuterNew, database);
        CompletableFuture<Integer> idComposite = FunctionService.createComposite(idInner.get(), idOuter.get(), database);

        MathFunction function = FunctionService.getFunction(idComposite.get(), database).get();
        assertEquals(3, function.apply(0), 0.05);
        assertEquals(5, function.apply(2), 0.05);
        assertEquals(17.2, function.apply(14.2), 0.05);
        assertEquals(-2.2, function.apply(-5.2), 0.05);
        assertEquals(-7, function.apply(-10), 0.05);

        FunctionService.updateComposite(idComposite.get(), null, idOuterNew.get(), database);
        function = FunctionService.getFunction(idComposite.get(), database).get();
        assertEquals(1, function.apply(0), 0.05);
        assertEquals(3, function.apply(2), 0.05);
        assertEquals(15.2, function.apply(14.2), 0.05);
        assertEquals(-4.2, function.apply(-5.2), 0.05);
        assertEquals(-9, function.apply(-10), 0.05);

    }

    @Test
    void testUpdatePoint() throws InterruptedException, ExecutionException {
        String expr = "2x*sin(x)";
        // Пишем в базу данных
        int id = FunctionService.createTabulated(expr, -10, 10, 100, database).get();

        MathFunction function = FunctionService.getFunction(id, database).get();
        var tabulated = (TabulatedFunction)function;
        double ptX = tabulated.getX(50);
        double ptY = tabulated.apply(ptX);

        FunctionService.updatePoint(id, ptX, 10, database);
        function = FunctionService.getFunction(id, database).get();
        assertEquals(10, function.apply(ptX));
        assertNotEquals(ptY, function.apply(ptX));
    }

    @Test
    void testDeletePoint() throws InterruptedException, ExecutionException {
        String expr = "2x*sin(x)";
        // Пишем в базу данных
        int id = FunctionService.createTabulated(expr, -10, 10, 100, database).get();

        MathFunction function = FunctionService.getFunction(id, database).get();
        var tabulated = (TabulatedFunction)function;
        assertEquals(100, tabulated.getCount());
        double ptX = tabulated.getX(50);

        FunctionService.deletePoint(id, ptX, database);
        function = FunctionService.getFunction(id, database).get();
        tabulated = (TabulatedFunction)function;
        assertEquals(99, tabulated.getCount());
    }

    @Test
    void testWriteGetMany() throws InterruptedException, ExecutionException {
        int count = 10000;
        int pointCount = 5;
        var functions = new CompletableFuture[count];
        for (int i = 0; i < count; i++) {
            // генерируем случайные табулированные функции
            double curX = -Math.random() * pointCount / 4;
            var xValues = new double[pointCount];
            var yValues = new double[pointCount];
            for (int j = 0; j < pointCount; j++) {
                curX += Math.max(Math.random(), 0.001);
                xValues[j] = curX;
                yValues[j] = Math.random() * 10 - 5;
            }
            functions[i] = FunctionService.createPureTabulated(xValues, yValues, database);
        }
        //noinspection rawtypes
        for (CompletableFuture f : functions) {
            f.get();
        }
    }
}