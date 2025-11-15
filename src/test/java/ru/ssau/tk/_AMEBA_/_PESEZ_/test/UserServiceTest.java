package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.*;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.DatabaseConnection;
import ru.ssau.tk._AMEBA_._PESEZ_.service.UserService;

import java.io.*;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import static ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository.*;
import static ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository.*;

import static org.junit.jupiter.api.Assertions.*;
import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.Log;

class UserServiceTest {
    static String databaseConfig = "test_config.properties";
    static UserService service;
    static Properties PROPERTIES = new Properties();

    @BeforeAll
    static void setup() {
        service = new UserService(databaseConfig);

        String filepath = DatabaseConnection.class.getClassLoader().getResource("config/" + databaseConfig).getPath();
        try (var propertiesReader = new FileReader(filepath)) {
            PROPERTIES.load(propertiesReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void cleanup() throws SQLException {
        var database = new DatabaseConnection(databaseConfig);
        database.executeUpdate("DROP TABLE points");
        database.executeUpdate("DROP TABLE composite_function");
        database.executeUpdate("DROP TABLE function_ownership");
        database.executeUpdate("DROP TABLE users");
        database.executeUpdate("DROP TABLE function");
    }

    @Test
    void testAddGetDeleteUser() {
        int userId = service.createUser(UserType.Normal, "MyUser", "MyPassword").join();

        UserDTO user = service.getUser(userId).join();
        assertEquals(UserType.Normal, user.userType);
        assertEquals(userId, user.userId);
        assertEquals("MyPassword", user.password);
        assertEquals("MyUser", user.username);

        // Ещё тестируем удаление наших функций
        int funcId = service.createUserFunction(userId, "MyFunc", "x").join();

        service.deleteUser(userId).join();
        assertNull(service.getUser(userId).join());
        assertNull(service.getUserFunction(userId, funcId).join());
    }

    @Test
    void testAddGetDeleteFunction() {
        int userId = service.createUser(UserType.Normal, "TestUser", "TestPassword").join();

        String expr = "3x+5";
        int funcId = service.createUserFunction(userId, "MyFunc", expr).join();
        OwnedFunctionDTO func = service.getUserFunction(userId, funcId).join();

        assertEquals(MathFunctionID, func.function.funcType);
        assertEquals(funcId, func.function.funcId);
        assertEquals(expr, func.function.expression);
        assertEquals(userId, func.ownership.userId);
        assertEquals("MyFunc", func.ownership.funcName);

        service.deleteUserFunction(userId, funcId).join();
        assertNull(service.getUserFunction(userId, funcId).join());
    }

    @Test
    void testSeveralFunctions() {
        int userId = service.createUser(UserType.Normal, "TestUser2", "TestPassword2").join();

        String expr = "3x+5";
        int mathId = service.createUserFunction(userId, "MyMath", expr).join();
        int tabId = service.createUserTabulatedFunction(userId, "MyTabulated", expr, -10, 10, 100).join();
        service.createUserPureTabulated(userId, "MyPure", new double[]{1, 2, 3}, new double[]{10, 20, 30}).join();
        service.createUserComposite(userId, "MyComposite", mathId, tabId).join();

        OwnedFunctionDTO[] functions = service.getUserFunctions(userId).join();
        assertEquals(4, functions.length);
        UserService.sortFunctionsDate(functions);

        OwnedFunctionDTO pFunc = functions[0];
        for (int i = 1; i < functions.length; i++) {
            assertTrue(functions[i].ownership.createdDate.compareTo(pFunc.ownership.createdDate) >= 0);
            pFunc = functions[i];
        }

        assertEquals("MyMath", functions[0].ownership.funcName);
        assertEquals("MyTabulated", functions[1].ownership.funcName);
        assertEquals("MyPure", functions[2].ownership.funcName);
        assertEquals("MyComposite", functions[3].ownership.funcName);
        assertEquals(MathFunctionID, functions[0].function.funcType);
        assertEquals(TabulatedID, functions[1].function.funcType);
        assertEquals(PureTabulatedID, functions[2].function.funcType);
        assertEquals(CompositeID, functions[3].function.funcType);

        service.deleteUserFunction(userId, tabId).join();
        functions = service.getUserFunctions(userId).join();
        assertEquals(3, functions.length);
    }

    @Test
    void benchmarkManyUsers() {
        if (PROPERTIES.getProperty("bench").equals("false"))
            return;

        Configurator.setLevel("ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility", Level.WARN);
        int startCount = 1000;
        int countDelta = 1000;
        int functionsCount = 50;
        int testAmount = 5;
        for (int count = startCount, it = 0; it < testAmount; count += countDelta, it++) {
            CompletableFuture<Void>[] users = new CompletableFuture[count];
            long startTime = System.currentTimeMillis();
            int funcTotal = count * functionsCount;
            for (int i = 0; i < count; i++) {
                final String nameFirst = "Func" + count + "," + i + ",";
                users[i] = service.createUser(UserType.Normal, "SortUser" + i, "SortPassword" + i).thenAccept(userId -> {
                    String expr = Math.random() + "x+" + Math.random();
                    CompletableFuture<Integer>[] functions = new CompletableFuture[functionsCount];
                    for (int j = 0; j < functionsCount; j++) {
                        functions[j] = service.createUserFunction(userId, nameFirst + j, expr);
                    }
                    CompletableFuture.allOf(functions).join();
                });
            }
            CompletableFuture.allOf(users).join();
            float tookMillis = System.currentTimeMillis() - startTime;
            float tookSeconds = tookMillis / 1000f;
            Log.warn("Write of {} users + {} functions took: {}s ({}+{}/s)",
                    count, funcTotal, tookSeconds, count / tookSeconds, funcTotal / tookSeconds);
        }
        UserDTO[] allUsers = service.getUsers().join();
        long startTime = System.nanoTime();
        UserService.sortUsersDate(allUsers);
        float tookTime = (System.nanoTime() - startTime) * 1e-9f;
        Log.warn("Took {}s to sort {} users", tookTime, allUsers.length);

        UserDTO pUser = allUsers[0];
        for (int i = 1; i < allUsers.length; i++) {
            assertTrue(allUsers[i].createdDate.compareTo(pUser.createdDate) >= 0);
            pUser = allUsers[i];
        }
    }
}