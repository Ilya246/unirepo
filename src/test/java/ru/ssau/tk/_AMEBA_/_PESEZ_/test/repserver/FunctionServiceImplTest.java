package ru.ssau.tk._AMEBA_._PESEZ_.test.repserver;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionOwnershipEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionOwnershipId;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.MathFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionOwnershipRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.service.FunctionServiceImpl;
import ru.ssau.tk._AMEBA_._PESEZ_.utility.TestHibernateSessionFactoryUtil;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class FunctionServiceImplTest extends BaseRepositoryTest {

    private SessionFactory sessionFactory;
    private FunctionServiceImpl functionService;
    private FunctionRepository functionRepository;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        sessionFactory = TestHibernateSessionFactoryUtil.getSessionFactory();
        functionService = new FunctionServiceImpl(sessionFactory);
        functionRepository = new FunctionRepository(sessionFactory);
        userRepository = new UserRepository(sessionFactory);
        clearDatabase();
    }

    private void clearDatabase() {
        try (var session = sessionFactory.openSession()) {
            var transaction = session.beginTransaction();
            session.createNativeQuery("DELETE FROM composite_function").executeUpdate();
            session.createNativeQuery("DELETE FROM points").executeUpdate();
            session.createNativeQuery("DELETE FROM function_ownership").executeUpdate();
            session.createNativeQuery("DELETE FROM function").executeUpdate();
            session.createNativeQuery("DELETE FROM users").executeUpdate();
            transaction.commit();
        }
    }

    @Test
    void testGetFunctionOwner() {
        // Создаем пользователя и функцию
        UserEntity user = new UserEntity(1, "TestOwner", "password", new Date());
        userRepository.save(user);

        FunctionEntity function = new FunctionEntity(1, "x^2");
        functionRepository.save(function);

        // Создаем связь владения
        FunctionOwnershipRepository ownershipRepository = new FunctionOwnershipRepository(sessionFactory);

        FunctionOwnershipEntity ownership = new FunctionOwnershipEntity();
        ownership.setId(new FunctionOwnershipId(user.getUserId(), function.getFuncId())); // Используем конструктор
        ownership.setUser(user);
        ownership.setFunction(function);
        ownership.setCreatedDate(new Date());
        ownership.setFuncName("My Function");
        ownershipRepository.save(ownership);

        // Тестируем получение владельца
        Optional<UserEntity> owner = functionService.getFunctionOwner(function.getFuncId());
        assertTrue(owner.isPresent(), "Владелец должен быть найден");
        assertEquals("TestOwner", owner.get().getUserName());
    }




    @Test
    void testGetFunctionById() {
        FunctionEntity function = new FunctionEntity(1, "sin(x)");
        functionService.saveFunc(function);

        FunctionEntity found = functionService.getFunctionById(function.getFuncId());
        assertNotNull(found, "Функция должна быть найдена");
        assertEquals("sin(x)", found.getExpression());
        assertEquals(found.getTypeId(), found.getTypeId());
    }


    @Test
    void testGetAllFunctions() {
        FunctionEntity f1 = new FunctionEntity(1, "f1(x)");
        FunctionEntity f2 = new FunctionEntity(2, "f2(x)");
        FunctionEntity f3 = new FunctionEntity(1, "f3(x)");

        functionService.saveFunc(f1);
        functionService.saveFunc(f2);
        functionService.saveFunc(f3);

        List<FunctionEntity> functions = functionService.getAllFunctions();
        assertEquals(3, functions.size(), "Должно быть 3 функции");
        assertTrue(functions.stream().anyMatch(f -> f.getExpression().equals("f1(x)")));
        assertTrue(functions.stream().anyMatch(f -> f.getExpression().equals("f2(x)")));
        assertTrue(functions.stream().anyMatch(f -> f.getExpression().equals("f3(x)")));
    }

    @Test
    void testGetFunctionsByType() {
        FunctionEntity f1 = new FunctionEntity(1, "linear");
        FunctionEntity f2 = new FunctionEntity(1, "quadratic");
        FunctionEntity f3 = new FunctionEntity(2, "trigonometric");

        functionService.saveFunc(f1);
        functionService.saveFunc(f2);
        functionService.saveFunc(f3);

        List<FunctionEntity> type1Functions = functionService.getFunctionsByType(1);
        assertEquals(2, type1Functions.size(), "Должно быть 2 функции типа 1");
        assertTrue(type1Functions.stream().anyMatch(f -> f.getExpression().equals("linear")));
        assertTrue(type1Functions.stream().anyMatch(f -> f.getExpression().equals("quadratic")));

        List<FunctionEntity> type2Functions = functionService.getFunctionsByType(2);
        assertEquals(1, type2Functions.size(), "Должна быть 1 функция типа 2");
        assertEquals("trigonometric", type2Functions.get(0).getExpression());
    }

    @Test
    void testSaveFunc() {
        FunctionEntity function = new FunctionEntity(1, "test function");
        functionService.saveFunc(function);

        FunctionEntity found = functionService.getFunctionById(function.getFuncId());
        assertNotNull(found, "Функция должна быть сохранена");
        assertEquals("test function", found.getExpression());
    }

    @Test
    void testGetUserFunctionsSortedByDate() {
        // Создаем пользователя и функции
        UserEntity user = new UserEntity(1, "TestUser", "pass", new Date());
        userRepository.save(user);

        FunctionEntity f1 = new FunctionEntity(1, "func1");
        FunctionEntity f2 = new FunctionEntity(1, "func2");
        FunctionEntity f3 = new FunctionEntity(1, "func3");
        functionRepository.save(f1);
        functionRepository.save(f2);
        functionRepository.save(f3);

        // Создаем связи владения с разными датами через репозиторий
        FunctionOwnershipRepository ownershipRepository = new FunctionOwnershipRepository(sessionFactory);

        FunctionOwnershipEntity o1 = new FunctionOwnershipEntity();
        o1.setId(new FunctionOwnershipId(user.getUserId(), f1.getFuncId())); // Устанавливаем составной ключ
        o1.setUser(user);
        o1.setFunction(f1);
        o1.setCreatedDate(new Date(System.currentTimeMillis() - 30000)); // самая старая
        o1.setFuncName("Oldest");
        ownershipRepository.save(o1);

        FunctionOwnershipEntity o2 = new FunctionOwnershipEntity();
        o2.setId(new FunctionOwnershipId(user.getUserId(), f2.getFuncId())); // Устанавливаем составной ключ
        o2.setUser(user);
        o2.setFunction(f2);
        o2.setCreatedDate(new Date(System.currentTimeMillis() - 20000)); // средняя
        o2.setFuncName("Middle");
        ownershipRepository.save(o2);

        FunctionOwnershipEntity o3 = new FunctionOwnershipEntity();
        o3.setId(new FunctionOwnershipId(user.getUserId(), f3.getFuncId())); // Устанавливаем составной ключ
        o3.setUser(user);
        o3.setFunction(f3);
        o3.setCreatedDate(new Date(System.currentTimeMillis() - 10000)); // самая новая
        o3.setFuncName("Newest");
        ownershipRepository.save(o3);

        // Тестируем сортировку по возрастанию (старые сначала)
        List<FunctionEntity> ascending = functionService.getUserFunctionsSortedByDate(user.getUserId(), false);
        assertEquals(3, ascending.size());
        assertEquals("func1", ascending.get(0).getExpression()); // самая старая

        // Тестируем сортировку по убыванию (новые сначала)
        List<FunctionEntity> descending = functionService.getUserFunctionsSortedByDate(user.getUserId(), true);
        assertEquals(3, descending.size());
        assertEquals("func3", descending.get(0).getExpression()); // самая новая
    }

    @Test
    void testCreateMathFunction() throws ExecutionException, InterruptedException {
        CompletableFuture<Long> future = functionService.createMathFunction("2*x + 3");
        Long funcId = future.get();

        FunctionEntity function = functionService.getFunctionById(funcId);
        assertNotNull(function, "Математическая функция должна быть создана");
        assertEquals(1, function.getTypeId());
        assertEquals("2*x + 3", function.getExpression());
    }

    @Test
    void testCreateTabulated() throws ExecutionException, InterruptedException {
        CompletableFuture<Long> future = functionService.createTabulated("x^2", 0, 2, 3);
        Long funcId = future.get();

        FunctionEntity function = functionService.getFunctionById(funcId);
        assertNotNull(function, "Табулированная функция должна быть создана");
        assertEquals(2, function.getTypeId());
        assertEquals("x^2", function.getExpression());

        // Проверяем, что функцию можно получить и вычислить
        MathFunction mathFunc = functionService.getFunction(funcId).get();
        double result = mathFunc.apply(1.5);
        assertTrue(result > 0, "Функция должна возвращать значения");
    }

    @Test
    void testCreateComposite() throws ExecutionException, InterruptedException {
        // Создаем базовые функции
        FunctionEntity inner = new FunctionEntity(1, "x + 1");
        FunctionEntity outer = new FunctionEntity( 1, "x^2");
        functionService.saveFunc(inner);
        functionService.saveFunc(outer);

        CompletableFuture<Long> future = functionService.createComposite(inner.getFuncId(), outer.getFuncId());
        Long compositeId = Long.valueOf(future.get());

        FunctionEntity composite = functionService.getFunctionById(compositeId);
        assertNotNull(composite, "Композитная функция должна быть создана");
        assertEquals(3, composite.getTypeId());
        assertEquals("(x + 1)^2", composite.getExpression());
    }

    @Test
    void testGetFunction() throws ExecutionException, InterruptedException {
        FunctionEntity function = new FunctionEntity(1, "3*x - 2");
        functionService.saveFunc(function);

        CompletableFuture<MathFunction> future = functionService.getFunction(function.getFuncId());
        MathFunction mathFunc = future.get();

        assertNotNull(mathFunc, "Функция должна быть получена");
        assertEquals(4.0, mathFunc.apply(2), 0.0001); // 3*2 - 2 = 4
    }

    @Test
    void testUpdatePoint() throws ExecutionException, InterruptedException {
        // Создаем чисто табулированную функцию
        double[] xValues = {0, 1, 2};
        double[] yValues = {0, 1, 4};
        CompletableFuture<Long> createFuture = functionService.createTabulated("x^2", 0, 2, 3);
        Long funcId = createFuture.get();

        // Обновляем точку
        CompletableFuture<Void> updateFuture = functionService.updatePoint(funcId, 1.0, 10.0);
        updateFuture.get();

        // Проверяем, что значение обновилось
        MathFunction func = functionService.getFunction(funcId).get();
        double result = func.apply(1.0);
        assertEquals(10.0, result, 0.0001, "Значение в точке должно быть обновлено");
    }

    @Test
    void testDeletePoint() throws ExecutionException, InterruptedException {
        // Создаем чисто табулированную функцию
        double[] xValues = {0, 1, 2};
        double[] yValues = {0, 1, 4};
        CompletableFuture<Long> createFuture = functionService.createTabulated("x^2", 0, 2, 3);
        Long funcId = createFuture.get();

        // Удаляем точку
        CompletableFuture<Void> deleteFuture = functionService.deletePoint(funcId, 1.0);
        deleteFuture.get();

        // Функция должна продолжать работать для других точек
        MathFunction func = functionService.getFunction(funcId).get();
        double result = func.apply(2.0);
        assertEquals(4.0, result, 0.0001, "Функция должна работать для оставшихся точек");
    }


    @Test
    void testSaveAllFunctions() {
        List<FunctionEntity> functions = List.of(
                new FunctionEntity(1, "func1"),
                new FunctionEntity(1, "func2"),
                new FunctionEntity(2, "func3"),
                new FunctionEntity(2, "func4"),
                new FunctionEntity(3, "func5")
        );

        functionService.saveAllFunctions(functions);

        List<FunctionEntity> allFunctions = functionService.getAllFunctions();
        assertEquals(5, allFunctions.size(), "Все 5 функций должны быть сохранены");
    }

    @Test
    void testUpdateComposite() throws ExecutionException, InterruptedException {
        // Создаем исходные функции
        FunctionEntity inner1 = new FunctionEntity( 1, "x + 1");
        FunctionEntity outer1 = new FunctionEntity(1, "x^2");
        FunctionEntity inner2 = new FunctionEntity(1, "x - 1");
        FunctionEntity outer2 = new FunctionEntity(1, "x^3");

        functionService.saveFunc(inner1);
        functionService.saveFunc(outer1);
        functionService.saveFunc(inner2);
        functionService.saveFunc(outer2);

        // Создаем композитную функцию
        CompletableFuture<Long> createFuture = functionService.createComposite(inner1.getFuncId(), outer1.getFuncId());
        Long compositeId = Long.valueOf(createFuture.get());

        // Обновляем композитную функцию
        CompletableFuture<Void> updateFuture = functionService.updateComposite(compositeId, inner2.getFuncId(), outer2.getFuncId());
        updateFuture.get();

        // Проверяем, что функция все еще существует
        FunctionEntity updated = functionService.getFunctionById(compositeId);
        assertNotNull(updated, "Композитная функция должна существовать после обновления");
    }


    @Test
    void testGetFunctionsByTypeNonExistent() {
        List<FunctionEntity> functions = functionService.getFunctionsByType(999);
        assertTrue(functions.isEmpty(), "Для несуществующего типа должен вернуться пустой список");
    }


    @Test
    void testAsyncMethodErrorHandling() {
        // Тестируем обработку ошибок в асинхронных методах
        CompletableFuture<Long> invalidFuture = functionService.createMathFunction("invalid expression x^");

        assertThrows(ExecutionException.class, invalidFuture::get,
                "Должно быть выброшено исключение для невалидного выражения");
    }
}