package ru.ssau.tk._AMEBA_._PESEZ_.repository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.Log;

public abstract class Repository {
    protected ThreadLocal<DatabaseConnection> databaseLocal;

    public Repository(String config) {
        this.databaseLocal = ThreadLocal.withInitial(() -> new DatabaseConnection(config));
    }

    public Repository(DatabaseConnection connection) {
        this.databaseLocal = ThreadLocal.withInitial(() -> connection);
    }

    protected static String readCommand(String filename) {
        try (InputStream instream = FunctionRepository.class.getClassLoader().getResourceAsStream("scripts/" + filename + ".sql")) {
            return new String(instream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException | NullPointerException e) {
            Log.error("Failed to read SQL command:", e);
            return null;
        }
    }
}
