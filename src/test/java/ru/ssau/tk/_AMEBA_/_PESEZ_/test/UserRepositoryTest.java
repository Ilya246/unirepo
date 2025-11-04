package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.FunctionOwnershipDTO;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.UserDTO;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.*;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository.*;

class UserRepositoryTest {
    static String databaseUrl = "jdbc:postgresql://localhost:5432/function_db_test";
    static FunctionRepository functionRepo;
    static UserRepository repository;

    @BeforeAll
    static void setup() {
        functionRepo = new FunctionRepository(databaseUrl);
        repository = new UserRepository(databaseUrl);
        functionRepo.ensureTables();
        repository.ensureTables();
    }

    // Финальная очистка
    @AfterAll
    static void cleanup() throws SQLException {
        var database = new DatabaseConnection(databaseUrl);
        database.executeUpdate("DROP TABLE function_ownership");
        database.executeUpdate("DROP TABLE users");
        database.executeUpdate("DROP TABLE composite_function");
        database.executeUpdate("DROP TABLE points");
        database.executeUpdate("DROP TABLE function");
    }

    @Test
    void testCreateGetDeleteUser() throws InterruptedException, ExecutionException {
        String username = "TestUser";
        String password = "password";
        // Пишем в базу данных
        int id = repository.createUser(NormalUserID, username, password).get();
        UserDTO user = repository.getUser(id).get();
        assertEquals(username, user.username);
        assertEquals(password, user.password);

        repository.deleteUser(id).get();

        assertNull(repository.getUser(id).get());
    }

    @Test
    void testAdminUser() throws InterruptedException, ExecutionException {
        int id = repository.createUser(AdminUserID, "AdminUser", "adminPass").get();
        UserDTO user = repository.getUser(id).get();
        assertEquals("AdminUser", user.username);
        assertEquals("adminPass", user.password);
        assertEquals(AdminUserID, user.userType);

        repository.deleteUser(id).get();
    }

    @Test
    void testUpdateUser() throws InterruptedException, ExecutionException {
        int id = repository.createUser(NormalUserID, "OldName", "OldPass").get();
        
        repository.updateUser(id, "NewName", "NewPass").get();
        UserDTO user = repository.getUser(id).get();
        assertEquals("NewName", user.username);
        assertEquals("NewPass", user.password);

        repository.deleteUser(id).get();
    }

    @Test
    void testGetUsers() throws InterruptedException, ExecutionException {
        repository.createUser(NormalUserID, "User1", "Pass1").get();
        repository.createUser(NormalUserID, "User2", "Pass2").get();
        repository.createUser(NormalUserID, "User3", "Pass3").get();

        UserDTO[] all = repository.getAllUsers().get();
        assertTrue(Arrays.stream(all).anyMatch(a -> a.username.equals("User1") && a.password.equals("Pass1")));
        assertTrue(Arrays.stream(all).anyMatch(a -> a.username.equals("User2") && a.password.equals("Pass2")));
        assertTrue(Arrays.stream(all).anyMatch(a -> a.username.equals("User3") && a.password.equals("Pass3")));
    }

    @Test
    void testCRUDFunctionOwnership() throws InterruptedException, ExecutionException {
        int userId = repository.createUser(NormalUserID, "FunctionOwner", "testPass").get();
        int funcId = functionRepo.createMathFunction("x^2").get();

        String funcName = "Quadratic";
        repository.addFunctionOwnership(userId, funcId, funcName).get();
        FunctionOwnershipDTO ownership = repository.getFunctionOwnership(userId, funcId).get();
        assertEquals(funcId, ownership.funcId);
        assertEquals(funcName, ownership.funcName);

        int newFuncId = functionRepo.createMathFunction("x^3").get();
        repository.updateFunctionOwnership(userId, funcId, newFuncId).get();
        ownership = repository.getFunctionOwnership(userId, newFuncId).get();
        assertEquals(newFuncId, ownership.funcId);

        repository.removeFunctionOwnership(userId, newFuncId).get();
        ownership = repository.getFunctionOwnership(userId, newFuncId).get();
        assertNull(ownership);
    }

    @Test
    void testMultipleFunctionOwnership() throws InterruptedException, ExecutionException {
        int userId = repository.createUser(NormalUserID, "SeveralFunctionOwner", "testPass").get();
        int funcId1 = functionRepo.createMathFunction("2x^2").get();
        int funcId2 = functionRepo.createMathFunction("3x^3").get();
        int funcId3 = functionRepo.createMathFunction("4x^4").get();

        repository.addFunctionOwnership(userId, funcId1, "1").get();
        repository.addFunctionOwnership(userId, funcId2, "2").get();
        repository.addFunctionOwnership(userId, funcId3, "3").get();
        FunctionOwnershipDTO[] ownerships = repository.getFunctionOwnerships(userId).get();
        assertEquals(3, ownerships.length);
        assertEquals("1", ownerships[0].funcName);
        assertEquals("2", ownerships[1].funcName);
        assertEquals("3", ownerships[2].funcName);

        repository.removeFunctionOwnership(userId, funcId2).get();
        ownerships = repository.getFunctionOwnerships(userId).get();
        assertEquals(2, ownerships.length);
        assertEquals("1", ownerships[0].funcName);
        assertEquals("3", ownerships[1].funcName);
    }
}
