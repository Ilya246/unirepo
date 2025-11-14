package ru.ssau.tk._AMEBA_._PESEZ_.exceptions;

public class DatabaseConnectionFailedException extends RuntimeException {
    public DatabaseConnectionFailedException(Exception e) {
        super(e);
    }

    public DatabaseConnectionFailedException(String message) {
        super(message);
    }

    public DatabaseConnectionFailedException() {}
}
