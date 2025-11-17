package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.FunctionOwnershipDTO;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.OwnedFunctionDTO;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.UserDTO;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.*;
import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.*;

import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository.*;

class UserRepositoryTest {
    static String databaseConfig = "test_config.properties";
    static FunctionRepository functionRepo;
    static UserRepository repository;

    @BeforeAll
    static void setup() {
        functionRepo = new FunctionRepository(databaseConfig);
        repository = new UserRepository(databaseConfig);
        functionRepo.ensureTables();
        repository.ensureTables();
    }

    // Финальная очистка
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
    void testCreateGetDeleteUser() {
        String username = "TestUser";
        String password = "password";
        // Пишем в базу данных
        int id = repository.createUser(UserType.Normal, username, getBase64Hash(password)).join();
        UserDTO user = repository.getUser(id).join();
        assertEquals(username, user.username);
        assertEquals(getBase64Hash(password), user.passwordHash);

        repository.deleteUser(id).join();

        assertNull(repository.getUser(id).join());
    }

    @Test
    void testAdminUser() {
        int id = repository.createUser(UserType.Admin, "AdminUser", getBase64Hash("adminPass")).join();
        UserDTO user = repository.getUser(id).join();
        assertEquals("AdminUser", user.username);
        assertEquals(getBase64Hash("adminPass"), user.passwordHash);
        assertEquals(UserType.Admin, user.userType);

        repository.deleteUser(id).join();
    }

    @Test
    void testUpdateUser() {
        int id = repository.createUser(UserType.Normal, "OldName", "OldPass").join();
        
        repository.updateUser(id, "NewName", getBase64Hash("NewPass"), UserType.Normal).join();
        UserDTO user = repository.getUser(id).join();
        assertEquals("NewName", user.username);
        assertEquals( getBase64Hash("NewPass"), user.passwordHash);

        repository.deleteUser(id).join();
    }

    @Test
    void testGetUsers() {
        repository.createUser(UserType.Normal, "User1", getBase64Hash("Pass1")).join();
        repository.createUser(UserType.Normal, "User2", getBase64Hash("Pass2")).join();
        repository.createUser(UserType.Normal, "User3", getBase64Hash("Pass3")).join();

        UserDTO[] all = repository.getAllUsers().join();
        assertTrue(Arrays.stream(all).anyMatch(a -> a.username.equals("User1") && a.passwordHash.equals(getBase64Hash("Pass1"))));
        assertTrue(Arrays.stream(all).anyMatch(a -> a.username.equals("User2") && a.passwordHash.equals(getBase64Hash("Pass2"))));
        assertTrue(Arrays.stream(all).anyMatch(a -> a.username.equals("User3") && a.passwordHash.equals(getBase64Hash("Pass3"))));
    }

    @Test
    void testCRUDFunctionOwnership() {
        int userId = repository.createUser(UserType.Normal, "FunctionOwner", "testPass").join();
        int funcId = functionRepo.createMathFunction("x^2").join();

        String funcName = "Quadratic";
        repository.addFunctionOwnership(userId, funcId, funcName).join();
        FunctionOwnershipDTO ownership = repository.getFunctionOwnership(userId, funcId).join();
        assertEquals(funcId, ownership.funcId);
        assertEquals(funcName, ownership.funcName);

        String newName = "New";
        repository.updateFunctionOwnership(userId, funcId, newName).join();
        ownership = repository.getFunctionOwnership(userId, funcId).join();
        assertEquals(funcId, ownership.funcId);
        assertEquals(newName, ownership.funcName);

        repository.removeFunctionOwnership(userId, funcId).join();
        ownership = repository.getFunctionOwnership(userId, funcId).join();
        assertNull(ownership);
    }

    @Test
    void testMultipleFunctionOwnership() {
        int userId = repository.createUser(UserType.Normal, "SeveralFunctionOwner", "testPass").join();
        int funcId1 = functionRepo.createMathFunction("2x^2").join();
        int funcId2 = functionRepo.createMathFunction("3x^3").join();
        int funcId3 = functionRepo.createMathFunction("4x^4").join();

        repository.addFunctionOwnership(userId, funcId1, "1").join();
        repository.addFunctionOwnership(userId, funcId2, "2").join();
        repository.addFunctionOwnership(userId, funcId3, "3").join();
        FunctionOwnershipDTO[] ownerships = repository.getFunctionOwnerships(userId).join();
        assertEquals(3, ownerships.length);
        assertEquals("1", ownerships[0].funcName);
        assertEquals("2", ownerships[1].funcName);
        assertEquals("3", ownerships[2].funcName);

        repository.removeFunctionOwnership(userId, funcId2).join();
        ownerships = repository.getFunctionOwnerships(userId).join();
        assertEquals(2, ownerships.length);
        assertEquals("1", ownerships[0].funcName);
        assertEquals("3", ownerships[1].funcName);
    }

    @Test
    void testGetFunctions() {
        int userId = repository.createUser(UserType.Normal, "SeveralFunctionOwner", "testPass").join();
        int funcId1 = functionRepo.createMathFunction("2x^2").join();
        int funcId2 = functionRepo.createMathFunction("3x^3").join();
        int funcId3 = functionRepo.createMathFunction("4x^4").join();

        repository.addFunctionOwnership(userId, funcId1, "1").join();
        repository.addFunctionOwnership(userId, funcId2, "2").join();
        repository.addFunctionOwnership(userId, funcId3, "3").join();
        OwnedFunctionDTO[] ownerships = repository.getFunctions(userId).join();
        assertEquals(3, ownerships.length);
        assertEquals("1", ownerships[0].ownership.funcName);
        assertEquals("2", ownerships[1].ownership.funcName);
        assertEquals("3", ownerships[2].ownership.funcName);
        assertEquals("2x^2", ownerships[0].function.expression);
        assertEquals("3x^3", ownerships[1].function.expression);
        assertEquals("4x^4", ownerships[2].function.expression);
    }
}
