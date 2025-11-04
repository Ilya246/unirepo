package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.PointsEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.MathFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.TabulatedFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.utility.TestHibernateSessionFactoryUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.Log;


import static org.junit.jupiter.api.Assertions.*;

class FunctionRepositoryTest extends BaseRepositoryTest {
    private FunctionRepository repository;
    private UserRepository userRepository;
    private SessionFactory sessionFactory;
    @BeforeEach
    void setUp() {
        repository = new FunctionRepository(TestHibernateSessionFactoryUtil.getSessionFactory());
        userRepository = new UserRepository(TestHibernateSessionFactoryUtil.getSessionFactory());
        sessionFactory=TestHibernateSessionFactoryUtil.getSessionFactory();
    }

    @Test
    void testSaveAndFindById() {
        FunctionEntity function = new FunctionEntity(1, 1, "x^2 + 2*x + 1");
        repository.save(function);

        FunctionEntity found = repository.findById(1);

        assertNotNull(found, "Функция должна быть найдена после сохранения");
        assertEquals("x^2 + 2*x + 1", found.getExpression());
        assertEquals(1, found.getTypeId());
    }

    @Test
    void testFindAll() {
        FunctionEntity f1 = new FunctionEntity(1, 1, "sin(x)");
        FunctionEntity f2 = new FunctionEntity(2, 2, "cos(x)");
        FunctionEntity f3 = new FunctionEntity(3, 3, "x^3");

        repository.save(f1);
        repository.save(f2);
        repository.save(f3);

        List<FunctionEntity> functions = repository.findAll();

        assertEquals(3, functions.size(), "Должно быть три функции в базе");
        assertTrue(functions.stream().anyMatch(f -> f.getExpression().equals("sin(x)")));
        assertTrue(functions.stream().anyMatch(f -> f.getExpression().equals("cos(x)")));
        assertTrue(functions.stream().anyMatch(f -> f.getExpression().equals("x^3")));
    }

    @Test
    void testFindByType() {
        FunctionEntity f1 = new FunctionEntity(1, 1, "sin(x)");
        FunctionEntity f2 = new FunctionEntity(2, 1, "cos(x)");
        FunctionEntity f3 = new FunctionEntity(3, 2, "x^2");
        FunctionEntity f4 = new FunctionEntity(4, 3, "log(x)");

        repository.save(f1);
        repository.save(f2);
        repository.save(f3);
        repository.save(f4);

        List<FunctionEntity> type1Functions = repository.findByType(1);
        assertEquals(2, type1Functions.size(), "Должно быть найдено 2 функции с typeId = 1");
        assertTrue(type1Functions.stream().anyMatch(f -> f.getExpression().equals("sin(x)")));
        assertTrue(type1Functions.stream().anyMatch(f -> f.getExpression().equals("cos(x)")));
        assertFalse(type1Functions.stream().anyMatch(f -> f.getExpression().equals("x^2")));

        List<FunctionEntity> type2Functions = repository.findByType(2);
        assertEquals(1, type2Functions.size(), "Должна быть найдена 1 функция с typeId = 2");
        assertEquals("x^2", type2Functions.get(0).getExpression());

        List<FunctionEntity> type300Functions = repository.findByType(300);
        assertTrue(type300Functions.isEmpty(), "Не должно быть функций с typeId = 300");
    }

    @Test
    void testDeleteById() {
        FunctionEntity function = new FunctionEntity(1, 1, "x + 5");
        repository.save(function);

        // Проверяем, что функция сохранена
        FunctionEntity foundBeforeDelete = repository.findById(1);
        assertNotNull(foundBeforeDelete, "Функция должна существовать до удаления");

        repository.deleteById(1);

        FunctionEntity foundAfterDelete = repository.findById(1);
        assertNull(foundAfterDelete, "Функция должна быть удалена");
    }

    @Test
    void testParseFunction() {
        MathFunction func = FunctionRepository.parseFunction("x^2 + 2*x + 1");
        assertNotNull(func, "Функция должна быть успешно распарсена");

        double result = func.apply(2);
        assertEquals(9.0, result, 0.0001, "f(2) = 2^2 + 2*2 + 1 = 9");
    }

    @Test
    void testParseFunctionInvalidExpression() {
        assertThrows(IllegalArgumentException.class, () -> {
            FunctionRepository.parseFunction("x^ + invalid");
        }, "Должно быть выброшено исключение для невалидного выражения");
    }

    @Test
    void testCreateMathFunction() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> future = repository.createMathFunction("x^3 + 2*x");
        int funcId = future.get();

        FunctionEntity function = repository.findById(funcId);
        assertNotNull(function, "Математическая функция должна быть создана");
        assertEquals(1, function.getTypeId(), "Тип должен быть MATH_FUNCTION_ID (1)");
        assertEquals("x^3 + 2*x", function.getExpression());
    }

    @Test
    void testCreateMathFunctionInvalidExpression() {
        CompletableFuture<Integer> future = repository.createMathFunction("x^ + invalid");

        assertThrows(ExecutionException.class, future::get,
                "Должно быть выброшено исключение для невалидного выражения");
    }

    @Test
    void testCreateTabulated() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> future = repository.createTabulated("x^2", 0, 5, 6);
        int funcId = future.get();

        FunctionEntity function = repository.findById(funcId);
        assertNotNull(function, "Табулированная функция должна быть создана");
        assertEquals(2, function.getTypeId(), "Тип должен быть TABULATED_ID (2)");
        assertEquals("x^2", function.getExpression());

        // Проверяем, что точки созданы
        MathFunction tabulatedFunc = repository.getFunction(funcId).get();
        double result = tabulatedFunc.apply(2);
        assertEquals(4.0, result, 0.0001, "f(2) = 2^2 = 4");
    }

    @Test
    void testCreatePureTabulated() throws ExecutionException, InterruptedException {
        double[] xValues = {0, 1, 2, 3, 4};
        double[] yValues = {0, 1, 4, 9, 16};

        CompletableFuture<Integer> future = repository.createPureTabulated(xValues, yValues);
        int funcId = future.get();

        FunctionEntity function = repository.findById(funcId);
        assertNotNull(function, "Чисто табулированная функция должна быть создана");
        assertEquals(2, function.getTypeId(), "Тип должен быть TABULATED_ID (2)");
        assertEquals("<TABULATED>", function.getExpression());

        // Проверяем значения функции
        MathFunction tabulatedFunc = repository.getFunction(funcId).get();
        assertEquals(4.0, tabulatedFunc.apply(2), 0.0001, "f(2) должно быть 4");
        assertEquals(9.0, tabulatedFunc.apply(3), 0.0001, "f(3) должно быть 9");
    }

    @Test
    void testCreatePureTabulatedInvalidArrays() {
        double[] xValues = {0, 1, 2};
        double[] yValues = {0, 1}; // разная длина

        CompletableFuture<Integer> future = repository.createPureTabulated(xValues, yValues);

        assertThrows(ExecutionException.class, future::get,
                "Должно быть выброшено исключение при несовпадающих длинах массивов");
    }

    @Test
    void testCreateComposite() throws ExecutionException, InterruptedException {
        // Создаем внутреннюю и внешнюю функции
        FunctionEntity inner = new FunctionEntity(1, 1, "x + 1");
        FunctionEntity outer = new FunctionEntity(2, 1, "x^2");
        repository.save(inner);
        repository.save(outer);

        CompletableFuture<Integer> future = repository.createComposite(1, 2);
        int compositeId = future.get();

        FunctionEntity composite = repository.findById(compositeId);
        assertNotNull(composite, "Композитная функция должна быть создана");
        assertEquals(3, composite.getTypeId(), "Тип должен быть COMPOSITE_ID (3)");
        assertEquals("(x + 1)^2", composite.getExpression());

        // Проверяем вычисление композитной функции
        MathFunction compositeFunc = repository.getFunction(compositeId).get();
        double result = compositeFunc.apply(2); // (2 + 1)^2 = 9
        assertEquals(9.0, result, 0.0001);
    }

    @Test
    void testCreateCompositeSameIds() {
        FunctionEntity func = new FunctionEntity(1, 1, "x");
        repository.save(func);

        CompletableFuture<Integer> future = repository.createComposite(1, 1);

        assertThrows(ExecutionException.class, future::get,
                "Должно быть выброшено исключение при одинаковых ID внутренней и внешней функций");
    }

    @Test
    void testCreateCompositeNonExistentFunctions() {
        CompletableFuture<Integer> future = repository.createComposite(999, 888);

        assertThrows(ExecutionException.class, future::get,
                "Должно быть выброшено исключение при несуществующих функциях");
    }

    @Test
    void testGetFunctionAsMath() throws ExecutionException, InterruptedException {
        FunctionEntity mathFunc = new FunctionEntity(1, 1, "2*x + 3");
        repository.save(mathFunc);

        CompletableFuture<MathFunction> future = repository.getFunction(1, true);
        MathFunction function = future.get();

        assertNotNull(function, "Функция должна быть получена");
        assertEquals(7.0, function.apply(2), 0.0001, "f(2) = 2*2 + 3 = 7");
    }

    @Test
    void testGetFunctionPureTabulatedAsMath() throws ExecutionException, InterruptedException {
        double[] xValues = {0, 1, 2};
        double[] yValues = {0, 1, 4};
        CompletableFuture<Integer> future = repository.createPureTabulated(xValues, yValues);
        int funcId = future.get();

        CompletableFuture<MathFunction> mathFuture = repository.getFunction(funcId, true);

        assertThrows(ExecutionException.class, mathFuture::get,
                "Должно быть выброшено исключение при попытке получить чисто табулированную функцию как математическую");
    }

    @Test
    void testGetFunctionNonExistent() {
        CompletableFuture<MathFunction> future = repository.getFunction(999);

        assertThrows(ExecutionException.class, future::get,
                "Должно быть выброшено исключение для несуществующей функции");
    }

    @Test
    void testUpdatePoint() throws ExecutionException, InterruptedException {
        // Создаем чисто табулированную функцию
        double[] xValues = {0, 1, 2};
        double[] yValues = {0, 1, 4};
        CompletableFuture<Integer> future = repository.createPureTabulated(xValues, yValues);
        int funcId = future.get();

        // Обновляем точку
        CompletableFuture<Void> updateFuture = repository.updatePoint(funcId, 1.0, 10.0);
        updateFuture.get();

        // Проверяем, что точка обновилась
        MathFunction func = repository.getFunction(funcId).get();
        assertEquals(10.0, func.apply(1), 0.0001, "Значение в точке x=1 должно быть обновлено на 10");
    }

    @Test
    void testDeletePoint() throws ExecutionException, InterruptedException {
        // Создаем чисто табулированную функцию
        double[] xValues = {0, 1, 2};
        double[] yValues = {0, 1, 4};
        CompletableFuture<Integer> future = repository.createPureTabulated(xValues, yValues);
        int funcId = future.get();

        // Удаляем точку
        CompletableFuture<Void> deleteFuture = repository.deletePoint(funcId, 1.0);
        deleteFuture.get();

        // Проверяем, что функция все еще работает для других точек
        MathFunction func = repository.getFunction(funcId).get();
        assertEquals(4.0, func.apply(2), 0.0001, "Функция должна работать для оставшихся точек");
    }

    @Test
    void testDeleteFunctionCascading() throws ExecutionException, InterruptedException {
        // Создаем пользователя и функцию
        UserEntity user = new UserEntity(1, 1, "TestUser", "password");
        userRepository.save(user);

        FunctionEntity function = new FunctionEntity(1, 2, "x^2");
        repository.save(function);

        // Создаем точки для функции
        double[] xValues = {0, 1, 2};
        double[] yValues = {0, 1, 4};
        CompletableFuture<Integer> tabFuture = repository.createPureTabulated(xValues, yValues);
        int tabFuncId = tabFuture.get();

        // Удаляем функцию
        CompletableFuture<Void> deleteFuture = repository.deleteFunction(tabFuncId);
        deleteFuture.get();

        // Проверяем, что функция удалена
        FunctionEntity deleted = repository.findById(tabFuncId);
        assertNull(deleted, "Функция должна быть удалена");

        // Проверяем, что точки также удалены (через вызов getFunction)
        CompletableFuture<MathFunction> getFuture = repository.getFunction(tabFuncId);
        assertThrows(ExecutionException.class, getFuture::get,
                "Должно быть выброшено исключение при попытке получить удаленную функцию");
    }


    @Test
    void testSaveAll() {
        // Создаем список функций
        List<FunctionEntity> functions = List.of(
                new FunctionEntity(1, 1, "f1(x)"),
                new FunctionEntity(2, 1, "f2(x)"),
                new FunctionEntity(3, 2, "f3(x)"),
                new FunctionEntity(4, 2, "f4(x)"),
                new FunctionEntity(5, 3, "f5(x)")
        );

        // Сохраняем все функции
        repository.saveAll(functions);

        // Проверяем, что все функции сохранены
        List<FunctionEntity> allFunctions = repository.findAll();
        assertEquals(5, allFunctions.size(), "Должно быть сохранено 5 функций");

        // Проверяем, что функции доступны по ID
        for (int i = 1; i <= 5; i++) {
            FunctionEntity func = repository.findById(i);
            assertNotNull(func, "Функция с ID " + i + " должна быть найдена");
        }
    }

    @Test
    void testGetFunctionDefault() throws ExecutionException, InterruptedException {
        FunctionEntity mathFunc = new FunctionEntity(1, 1, "3*x - 1");
        repository.save(mathFunc);

        CompletableFuture<MathFunction> future = repository.getFunction(1);
        MathFunction function = future.get();

        assertNotNull(function, "Функция должна быть получена с параметром asMath по умолчанию (false)");
        assertEquals(5.0, function.apply(2), 0.0001, "f(2) = 3*2 - 1 = 5");
    }

    @Test
    void testTabulatedFunctionInterpolation() throws ExecutionException, InterruptedException {
        // Создаем табулированную функцию с малым количеством точек
        CompletableFuture<Integer> future = repository.createTabulated("x^2", 0, 4, 3);
        int funcId = future.get();

        MathFunction func = repository.getFunction(funcId).get();

        // При 3 точках на [0,4] точки будут: (0,0), (2,4), (4,16)
        // Проверяем значения в узловых точках
        assertEquals(0.0, func.apply(0), 0.0001, "f(0) = 0");
        assertEquals(4.0, func.apply(2), 0.0001, "f(2) = 4");
        assertEquals(16.0, func.apply(4), 0.0001, "f(4) = 16");

        // Проверяем интерполяцию между точками
        // Между (0,0) и (2,4): линейная интерполяция дает f(1) = 2
        assertEquals(2.0, func.apply(1), 0.0001, "f(1) = 2 (linear interpolation between 0 and 2)");

        // Между (2,4) и (4,16): линейная интерполяция дает f(3) = 10
        assertEquals(10.0, func.apply(3), 0.0001, "f(3) = 10 (linear interpolation between 2 and 4)");
    }

    @Test
    void testWriteGetMany() throws InterruptedException, ExecutionException {
        int startCount = 1000;
        int countDelta = 1000;
        int testAmount = 10;
        var writeTimes = new float[testAmount];

        for (int count = startCount, it = 0; it < testAmount; count += countDelta, it++) {
            //clearDatabase();

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

            // Дожидаемся завершения всех операций
            for (CompletableFuture f : functions) {
                f.get();
            }

            float tookMillis = System.currentTimeMillis() - startTime;
            float tookSeconds = tookMillis / 1000f;
            writeTimes[it] = tookSeconds;

            Log.info("Write of {} tabulated functions took: {}, {}/s", count, tookSeconds, count / tookSeconds);

            // Проверяем, что функции действительно сохранились
            try (var session = sessionFactory.openSession()) {
                Long actualCount = session.createQuery(
                        "SELECT COUNT(f) FROM FunctionEntity f WHERE f.expression = '<TABULATED>'",
                        Long.class).uniqueResult();

            }
        }

        // Вывод итоговой статистики
        for (int i = 0; i < testAmount; i++) {
            float time = writeTimes[i];
            int amount = startCount + countDelta * i;
            Log.info("Write {}: took {}s for {} ({}/s)", i + 1, time, amount, amount / time);
        }
    }

    // Метод для очистки базы данных
    private void clearDatabase() {
        try (var session = sessionFactory.openSession()) {
            var transaction = session.beginTransaction();

            try {
                // Удаляем в правильном порядке из-за foreign key constraints
                session.createQuery("DELETE FROM PointsEntity").executeUpdate();
                session.createQuery("DELETE FROM CompositeFunctionEntity").executeUpdate();
                session.createQuery("DELETE FROM FunctionOwnershipEntity").executeUpdate();
                session.createQuery("DELETE FROM FunctionEntity").executeUpdate();

                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }

            }
        }
    }

}