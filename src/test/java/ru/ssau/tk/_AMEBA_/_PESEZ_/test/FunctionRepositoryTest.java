package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.CompositeFunctionDTO;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.FunctionDTO;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.PointsDTO;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.*;

import static ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository.*;
import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.*;

import java.sql.SQLException;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class FunctionRepositoryTest {
    static String databaseUrl = "jdbc:postgresql://localhost:5432/function_db_test";
    static FunctionRepository repository;

    @BeforeAll
    static void setup() {
        repository = new FunctionRepository(databaseUrl);
        repository.ensureTables();
    }

    // Финальная очистка
    @AfterAll
    static void cleanup() throws SQLException {
        var database = new DatabaseConnection(databaseUrl);
        database.executeUpdate("DROP TABLE points");
        database.executeUpdate("DROP TABLE composite_function");
        database.executeUpdate("DROP TABLE function");
    }

    @Test
    void testMathFunction() {
        String expr = "2x+3";
        // Пишем в базу данных
        int id = repository.createMathFunction(expr).join();

        MathFunction function = repository.getFunction(id).join();
        assertEquals(5, function.apply(1));
        assertEquals(1, function.apply(-1));

        repository.deleteFunction(id).join();
    }

    @Test
    void testTabulatedFunction() {
        String expr = "2x*sin(x)";
        // Пишем в базу данных
        int id = repository.createTabulated(expr, -10, 10, 100).join();

        MathFunction function = repository.getFunction(id).join();
        assertInstanceOf(TabulatedFunction.class, function);
        assertEquals(0, function.apply(0), 0.05);
        assertEquals(3.38, function.apply(1.71), 0.05);
        assertEquals(3.38, function.apply(-1.71), 0.05);
    }

    @Test
    void testCompositeFunction() {
        String exprInner = "sin(x)";
        String exprOuter = "2*asin(x)";
        // Пишем в базу данных
        CompletableFuture<Integer> idInner = repository.createMathFunction(exprInner);
        CompletableFuture<Integer> idOuter = repository.createMathFunction(exprOuter);
        CompletableFuture<Integer> idComposite = repository.createComposite(idInner.join(), idOuter.join());

        MathFunction function = repository.getFunction(idComposite.join()).join();
        assertInstanceOf(CompositeFunction.class, function);
        assertEquals(0, function.apply(0), 0.05);
        assertEquals(0.2, function.apply(0.1), 0.05);
        assertEquals(2.4, function.apply(1.2), 0.05);
        assertEquals(-2.6, function.apply(-1.3), 0.05);
        assertEquals(3, function.apply(1.5), 0.05);
    }

    @Test
    void testMathTabulatedCompositeFunction() {
        String exprInner = "sin(x)";
        String exprOuter = "x^2";
        // Пишем в базу данных
        CompletableFuture<Integer> idInner = repository.createMathFunction(exprInner);
        CompletableFuture<Integer> idOuter = repository.createTabulated(exprOuter, -1, 1, 100);
        CompletableFuture<Integer> idComposite = repository.createComposite(idInner.join(), idOuter.join());

        MathFunction function = repository.getFunction(idComposite.join()).join();
        assertInstanceOf(CompositeFunction.class, function);
        assertEquals(0, function.apply(0), 0.05);
        assertEquals(0.25, function.apply(Math.PI / 6), 0.05);
        assertEquals(0.5, function.apply(Math.PI / 4), 0.05);
        assertEquals(0.5, function.apply(-Math.PI / 4), 0.05);
        assertEquals(1, function.apply(Math.PI / 2), 0.05);
        assertEquals(1, function.apply(-Math.PI / 2), 0.05);
    }

    @Test
    void testUpdateComposite() {
        String exprInner = "2x+7";
        String exprOuter = "0.5x-0.5";
        String exprOuterNew = "0.5x-2.5";
        // Пишем в базу данных
        CompletableFuture<Integer> idInner = repository.createMathFunction(exprInner);
        CompletableFuture<Integer> idOuter = repository.createMathFunction(exprOuter);
        CompletableFuture<Integer> idOuterNew = repository.createMathFunction(exprOuterNew);
        CompletableFuture<Integer> idComposite = repository.createComposite(idInner.join(), idOuter.join());

        MathFunction function = repository.getFunction(idComposite.join()).join();
        assertEquals(3, function.apply(0), 0.05);
        assertEquals(5, function.apply(2), 0.05);
        assertEquals(17.2, function.apply(14.2), 0.05);
        assertEquals(-2.2, function.apply(-5.2), 0.05);
        assertEquals(-7, function.apply(-10), 0.05);

        repository.updateComposite(idComposite.join(), null, idOuterNew.join()).join();
        function = repository.getFunction(idComposite.join()).join();
        assertEquals(1, function.apply(0), 0.05);
        assertEquals(3, function.apply(2), 0.05);
        assertEquals(15.2, function.apply(14.2), 0.05);
        assertEquals(-4.2, function.apply(-5.2), 0.05);
        assertEquals(-9, function.apply(-10), 0.05);

    }

    @Test
    void testUpdatePoint() {
        String expr = "2x*sin(x)";
        // Пишем в базу данных
        int id = repository.createTabulated(expr, -10, 10, 100).join();

        MathFunction function = repository.getFunction(id).join();
        var tabulated = (TabulatedFunction)function;
        double ptX = tabulated.getX(50);
        double ptY = tabulated.apply(ptX);

        repository.updatePoint(id, ptX, 10).join();
        function = repository.getFunction(id).join();
        assertEquals(10, function.apply(ptX));
        assertNotEquals(ptY, function.apply(ptX));
    }

    @Test
    void testNewPoint() {
        String expr = "2x*sin(x)";
        // Пишем в базу данных
        int id = repository.createTabulated(expr, -10, 10, 100).join();

        repository.createPoint(id, 15, 137).join();
        MathFunction function = repository.getFunction(id).join();
        assertEquals(137, function.apply(15));
    }

    @Test
    void testDeletePoint() {
        String expr = "2x*sin(x)";
        // Пишем в базу данных
        int id = repository.createTabulated(expr, -10, 10, 100).join();

        MathFunction function = repository.getFunction(id).join();
        var tabulated = (TabulatedFunction)function;
        assertEquals(100, tabulated.getCount());
        double ptX = tabulated.getX(50);

        repository.deletePoint(id, ptX).join();
        function = repository.getFunction(id).join();
        tabulated = (TabulatedFunction)function;
        assertEquals(99, tabulated.getCount());
    }

    @Test
    void benchmarkWriteGetMany() {
        Configurator.setLevel("ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility", Level.WARN);
        int startCount = 1000;
        int countDelta = 1000;
        int testAmount = 5;
        var writeTimes = new float[testAmount];
        for (int count = startCount, it = 0; it < testAmount; count += countDelta, it++) {
            int pointCount = 50;
            var functions = new CompletableFuture[count];
            long startTime = System.currentTimeMillis();
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
                functions[i] = repository.createPureTabulated(xValues, yValues);
            }
            CompletableFuture.allOf(functions).join();
            float tookMillis = System.currentTimeMillis() - startTime;
            float tookSeconds = tookMillis / 1000f;
            writeTimes[it] = tookSeconds;
            Log.warn("Write of {} tabulated functions took {}s, {}/s", count, tookSeconds, count / tookSeconds);
        }
    }

    @Test
    void testFunctionDTO() {
        String expr = "2x+3";
        // Пишем в базу данных
        int id = repository.createMathFunction(expr).join();

        FunctionDTO function = repository.getFunctionData(id).join();
        assertEquals(id, function.funcId);
        assertEquals(expr, function.expression);
        assertEquals(MathFunctionID, function.funcType);
    }

    @Test
    void testCompositeDTO() {
        String exprInner = "sin(x)";
        String exprOuter = "2*asin(x)";
        // Пишем в базу данных
        CompletableFuture<Integer> idInner = repository.createMathFunction(exprInner);
        CompletableFuture<Integer> idOuter = repository.createMathFunction(exprOuter);
        int idComposite = repository.createComposite(idInner.join(), idOuter.join()).join();

        CompositeFunctionDTO function = repository.getCompositeData(idComposite).join();
        assertEquals(idComposite, function.funcId);
        assertEquals(idInner.join(), function.innerFuncId);
        assertEquals(idOuter.join(), function.outerFuncId);
    }

    @Test
    void testPointsDTO() {
        String expr = "2x*sin(x)";
        // Пишем в базу данных
        int id = repository.createTabulated(expr, -10, 10, 100).join();

        PointsDTO function = repository.getPointsData(id).join();
        assertEquals(100, function.xValues.length);
        assertEquals(100, function.yValues.length);
        assertEquals(-10, function.xValues[0], 0.001);
        assertEquals(10, function.xValues[99], 0.001);
    }
}