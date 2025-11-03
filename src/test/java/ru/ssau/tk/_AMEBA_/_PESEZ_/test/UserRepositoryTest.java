package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.FunctionOwnershipDTO;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.UserDTO;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

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
        int id = repository.createNormalUser(username, password).get();
        UserDTO user = repository.getUser(id).get();
        assertEquals(username, user.username);
        assertEquals(password, user.password);

        repository.deleteUser(id).get();

        assertThrows(ExecutionException.class, () -> repository.getUser(id).get());
    }

    @Test
    void testAdminUser() throws InterruptedException, ExecutionException {
        int id = repository.createAdminUser("AdminUser", "adminPass").get();
        UserDTO user = repository.getUser(id).get();
        assertEquals("AdminUser", user.username);
        assertEquals("adminPass", user.password);
        assertEquals(UserRepository.UserType.Admin, user.userType);

        repository.deleteUser(id).get();
    }

    @Test
    void testUpdateUser() throws InterruptedException, ExecutionException {
        int id = repository.createNormalUser("OldName", "OldPass").get();
        
        repository.updateUser(id, "NewName", "NewPass").get();
        UserDTO user = repository.getUser(id).get();
        assertEquals("NewName", user.username);
        assertEquals("NewPass", user.password);

        repository.deleteUser(id).get();
    }

    @Test
    void testCRUDFunctionOwnership() throws InterruptedException, ExecutionException {
        int userId = repository.createNormalUser("FunctionOwner", "testPass").get();
        int funcId = functionRepo.createMathFunction("x^2").get();

        String funcName = "Quadratic";
        repository.addFunctionOwnership(userId, funcId, funcName).get();
        ArrayList<FunctionOwnershipDTO> ownerships = repository.getFunctionOwnerships(userId).get();
        assertEquals(1, ownerships.size());
        assertEquals(funcId, ownerships.get(0).funcId);
        assertEquals(funcName, ownerships.get(0).funcName);

        int newFuncId = functionRepo.createMathFunction("x^3").get();
        repository.updateFunctionOwnership(userId, funcId, newFuncId).get();
        ownerships = repository.getFunctionOwnerships(userId).get();
        assertEquals(1, ownerships.size());
        assertEquals(newFuncId, ownerships.get(0).funcId);

        repository.removeFunctionOwnership(userId, newFuncId).get();
        ownerships = repository.getFunctionOwnerships(userId).get();
        assertTrue(ownerships.isEmpty());
    }

    @Test
    void testMultipleFunctionOwnership() throws InterruptedException, ExecutionException {
        int userId = repository.createNormalUser("SeveralFunctionOwner", "testPass").get();
        int funcId1 = functionRepo.createMathFunction("2x^2").get();
        int funcId2 = functionRepo.createMathFunction("3x^3").get();
        int funcId3 = functionRepo.createMathFunction("4x^4").get();

        repository.addFunctionOwnership(userId, funcId1, "1").get();
        repository.addFunctionOwnership(userId, funcId2, "2").get();
        repository.addFunctionOwnership(userId, funcId3, "3").get();
        ArrayList<FunctionOwnershipDTO> ownerships = repository.getFunctionOwnerships(userId).get();
        assertEquals(3, ownerships.size());
        assertEquals("1", ownerships.get(0).funcName);
        assertEquals("2", ownerships.get(1).funcName);
        assertEquals("3", ownerships.get(2).funcName);

        repository.removeFunctionOwnership(userId, funcId2).get();
        ownerships = repository.getFunctionOwnerships(userId).get();
        assertEquals(2, ownerships.size());
        assertEquals("1", ownerships.get(0).funcName);
        assertEquals("3", ownerships.get(1).funcName);
    }
}
