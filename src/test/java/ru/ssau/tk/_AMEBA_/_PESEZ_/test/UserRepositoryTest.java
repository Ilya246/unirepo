package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.*;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.*;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {
    static String databaseUrl = "jdbc:postgresql://localhost:5432/function_db_test";
    static FunctionRepository _functionRep;
    static UserRepository repository;

    @BeforeAll
    static void setup() {
        _functionRep = new FunctionRepository(databaseUrl);
        repository = new UserRepository(databaseUrl);
        _functionRep.ensureTables();
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
        UserRepository.User user = repository.getUser(id).get();
        assertEquals(username, user.username);
        assertEquals(password, user.password);

        repository.deleteUser(id).get();

        assertThrows(ExecutionException.class, () -> repository.getUser(id).get());
    }
}