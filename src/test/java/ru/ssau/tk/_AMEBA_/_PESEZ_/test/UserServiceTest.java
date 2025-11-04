package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.*;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.DatabaseConnection;
import ru.ssau.tk._AMEBA_._PESEZ_.service.UserService;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository.*;

import static ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository.*;

import static org.junit.jupiter.api.Assertions.*;
import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.Log;

class UserServiceTest {
    static String URL = "jdbc:postgresql://localhost:5432/function_db_test";
    static UserService service;

    @BeforeAll
    static void setup() {
        service = new UserService(URL);
    }

    @AfterAll
    static void cleanup() throws SQLException {
        var database = new DatabaseConnection(URL);
        database.executeUpdate("DROP TABLE function_ownership");
        database.executeUpdate("DROP TABLE users");
        database.executeUpdate("DROP TABLE composite_function");
        database.executeUpdate("DROP TABLE points");
        database.executeUpdate("DROP TABLE function");
    }

    @Test
    void testAddGetDeleteUser() throws InterruptedException, ExecutionException {
        int userId = service.createUser(NormalUserID, "MyUser", "MyPassword").get();

        UserDTO user = service.getUser(userId).get();
        assertEquals(NormalUserID, user.userType);
        assertEquals(userId, user.userId);
        assertEquals("MyPassword", user.password);
        assertEquals("MyUser", user.username);

        // Ещё тестируем удаление наших функций
        int funcId = service.createUserFunction(userId, "MyFunc", "x").get();

        service.deleteUser(userId).get();
        assertNull(service.getUser(userId).get());
        assertNull(service.getUserFunction(userId, funcId).get());
    }

    @Test
    void testAddGetDeleteFunction() throws InterruptedException, ExecutionException {
        int userId = service.createUser(NormalUserID, "TestUser", "TestPassword").get();

        String expr = "3x+5";
        int funcId = service.createUserFunction(userId, "MyFunc", expr).get();
        OwnedFunctionDTO func = service.getUserFunction(userId, funcId).get();

        assertEquals(MathFunctionID, func.function.funcType);
        assertEquals(funcId, func.function.funcId);
        assertEquals(expr, func.function.expression);
        assertEquals(userId, func.ownership.userId);
        assertEquals("MyFunc", func.ownership.funcName);

        service.deleteUserFunction(userId, funcId).get();
        assertNull(service.getUserFunction(userId, funcId).get());
    }

    @Test
    void testSeveralFunctions() throws InterruptedException, ExecutionException {
        int userId = service.createUser(NormalUserID, "TestUser2", "TestPassword2").get();

        String expr = "3x+5";
        int mathId = service.createUserFunction(userId, "MyMath", expr).get();
        int tabId = service.createUserTabulatedFunction(userId, "MyTabulated", expr, -10, 10, 100).get();
        service.createUserPureTabulated(userId, "MyPure", new double[]{1, 2, 3}, new double[]{10, 20, 30}).get();
        service.createUserComposite(userId, "MyComposite", mathId, tabId).get();

        OwnedFunctionDTO[] functions = service.getUserFunctions(userId).get();
        assertEquals(4, functions.length);
        UserService.sortFunctionsDate(functions);
        assertEquals("MyMath", functions[0].ownership.funcName);
        assertEquals("MyTabulated", functions[1].ownership.funcName);
        assertEquals("MyPure", functions[2].ownership.funcName);
        assertEquals("MyComposite", functions[3].ownership.funcName);
        assertEquals(MathFunctionID, functions[0].function.funcType);
        assertEquals(TabulatedID, functions[1].function.funcType);
        assertEquals(PureTabulatedID, functions[2].function.funcType);
        assertEquals(CompositeID, functions[3].function.funcType);

        service.deleteUserFunction(userId, tabId).get();
        functions = service.getUserFunctions(userId).get();
        assertEquals(3, functions.length);
    }

    @Test
    void testSortUsers() throws InterruptedException, ExecutionException {
        Configurator.setLevel("ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility", Level.WARN);
        int startCount = 1000;
        int countDelta = 1000;
        int functionsCount = 5;
        int testAmount = 5;
        for (int count = startCount, it = 0; it < testAmount; count += countDelta, it++) {
            CompletableFuture<Integer>[] users = new CompletableFuture[count];
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                users[i] = service.createUser(NormalUserID, "SortUser" + i, "SortPassword" + i);
            }
            var gotUsers = new int[count];
            int funcTotal = count * functionsCount;
            CompletableFuture<Integer>[] functions = new CompletableFuture[funcTotal];
            for (int i = 0; i < count; i++) {
                gotUsers[i] = users[i].get();
                String expr = Math.random() + "x+" + Math.random();
                for (int j = 0; j < functionsCount; j++) {
                    functions[i * functionsCount + j] = service.createUserFunction(gotUsers[i], "Func" + count + "," + i + "," + j, expr);
                }
            }
            for (var f : functions) {
                f.get();
            }
            float tookMillis = System.currentTimeMillis() - startTime;
            float tookSeconds = tookMillis / 1000f;
            Log.warn("Write of {} users + {} functions took: {}s ({}+{}/s)",
                    count, funcTotal, tookSeconds, count / tookSeconds, funcTotal / tookSeconds);
        }
        UserDTO[] allUsers = service.getUsers().get();
        long startTime = System.nanoTime();
        UserService.sortUsersDate(allUsers);
        float tookTime = (System.nanoTime() - startTime) * 1e-9f;
        Log.warn("Took {}s to sort {} users", tookTime, allUsers.length);
    }
}