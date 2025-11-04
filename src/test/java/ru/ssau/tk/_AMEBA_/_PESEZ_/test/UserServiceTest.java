package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.*;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.DatabaseConnection;
import ru.ssau.tk._AMEBA_._PESEZ_.service.UserService;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;

import static ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository.*;

import static ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository.*;

import static org.junit.jupiter.api.Assertions.*;

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
    void testAddGetDeleteUser() {
        int userId = service.createUser(NormalUserID, "MyUser", "MyPassword");

        UserDTO user = service.getUser(userId);
        assertEquals(NormalUserID, user.userType);
        assertEquals(userId, user.userId);
        assertEquals("MyPassword", user.password);
        assertEquals("MyUser", user.username);

        // Ещё тестируем удаление наших функций
        int funcId = service.createUserFunction(userId, "MyFunc", "x");

        service.deleteUser(userId);
        assertNull(service.getUser(userId));
        assertNull(service.getUserFunction(userId, funcId));
    }

    @Test
    void testAddGetDeleteFunction() {
        int userId = service.createUser(NormalUserID, "TestUser", "TestPassword");

        String expr = "3x+5";
        int funcId = service.createUserFunction(userId, "MyFunc", expr);
        OwnedFunctionDTO func = service.getUserFunction(userId, funcId);

        assertEquals(MathFunctionID, func.function.funcType);
        assertEquals(funcId, func.function.funcId);
        assertEquals(expr, func.function.expression);
        assertEquals(userId, func.ownership.userId);
        assertEquals("MyFunc", func.ownership.funcName);

        service.deleteUserFunction(userId, funcId);
        assertNull(service.getUserFunction(userId, funcId));
    }

    @Test
    void testSeveralFunctions() {
        int userId = service.createUser(NormalUserID, "TestUser2", "TestPassword2");

        String expr = "3x+5";
        int mathId = service.createUserFunction(userId, "MyMath", expr);
        int tabId = service.createUserTabulatedFunction(userId, "MyTabulated", expr, -10, 10, 100);
        service.createUserPureTabulated(userId, "MyPure", new double[]{1, 2, 3}, new double[]{10, 20, 30});
        service.createUserComposite(userId, "MyComposite", mathId, tabId);

        OwnedFunctionDTO[] functions = service.getUserFunctions(userId);
        assertEquals(4, functions.length);
        Arrays.sort(functions, Comparator.comparing(owned -> owned.ownership.createdDate));
        assertEquals("MyMath", functions[0].ownership.funcName);
        assertEquals("MyTabulated", functions[1].ownership.funcName);
        assertEquals("MyPure", functions[2].ownership.funcName);
        assertEquals("MyComposite", functions[3].ownership.funcName);
        assertEquals(MathFunctionID, functions[0].function.funcType);
        assertEquals(TabulatedID, functions[1].function.funcType);
        assertEquals(PureTabulatedID, functions[2].function.funcType);
        assertEquals(CompositeID, functions[3].function.funcType);

        service.deleteUserFunction(userId, tabId);
        functions = service.getUserFunctions(userId);
        assertEquals(3, functions.length);
    }
}