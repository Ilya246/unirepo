package ru.ssau.tk._AMEBA_._PESEZ_.repository;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.*;

import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.Log;

public class UserRepository extends Repository {
    private static final String USER_ENSURE_TABLE = readCommand("UserCreateTable");
    private static final String USER_INSERT = readCommand("UserCreate");
    private static final String USER_DELETE = readCommand("UserDelete");
    private static final String USER_SELECT = readCommand("UserRead");
    private static final String USER_UPDATE = readCommand("UserUpdate");

    private static final String FUNCTION_OWNERSHIP_ENSURE_TABLE = readCommand("FunctionOwnershipCreateTable");
    private static final String FUNCTION_OWNERSHIP_INSERT = readCommand("FunctionOwnershipCreate");
    private static final String FUNCTION_OWNERSHIP_UPDATE = readCommand("FunctionOwnershipUpdate");
    private static final String FUNCTION_OWNERSHIP_DELETE = readCommand("FunctionOwnershipDelete");
    private static final String FUNCTION_OWNERSHIP_SELECT = readCommand("FunctionOwnershipRead");

    private static final int NormalUserID = 1;
    private static final int AdminUserID = 2;

    public UserRepository(String url) {
        super(url);
    }

    public UserRepository(DatabaseConnection connection) {
        super(connection);
    }

    public void ensureTables() {
        try {
            ensureUserTable();
            ensureFunctionOwnershipTable();
        } catch (SQLException e) {
            Log.error("Error when trying to ensure SQL tables:", e);
        }
    }

    private void ensureUserTable() throws SQLException {
        DatabaseConnection database = databaseLocal.get();
        database.executeUpdate(USER_ENSURE_TABLE);
    }

    private void ensureFunctionOwnershipTable() throws SQLException {
        DatabaseConnection database = databaseLocal.get();
        database.executeUpdate(FUNCTION_OWNERSHIP_ENSURE_TABLE);
    }

    public CompletableFuture<Integer> createNormalUser(String userName, String password) {
        return createUser(NormalUserID, userName, password);
    }

    public CompletableFuture<Integer> createAdminUser(String userName, String password) {
        return createUser(AdminUserID, userName, password);
    }

    private CompletableFuture<Integer> createUser(int typeId, String userName, String password) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DatabaseConnection database = databaseLocal.get();
                int userId = database.executeUpdateAndGetId(USER_INSERT, typeId, userName, password);
                Log.info("Created user {} with ID {}", userName, userId);
                return userId;
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<Void> updateUser(int userId, String newUserName, String newPassword) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DatabaseConnection database = databaseLocal.get();
                database.executeUpdate(USER_UPDATE, newUserName, newPassword, userId);
                Log.info("Updated user ID {}", userId);
                return null;
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<Void> deleteUser(int userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DatabaseConnection database = databaseLocal.get();
                database.executeUpdate(USER_DELETE, userId);
                Log.info("Deleted user ID {}", userId);
                return null;
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public enum UserType {Normal, Admin}

    public static class User {
        public int userId;
        public UserType userType;
        public String username;
        public String password;
        public Timestamp createdDate;

        public User(int userId, int typeId, String username, String password, Timestamp createdDate) {
            this.userId = userId;
            this.userType = typeId == 1 ? UserType.Normal : UserType.Admin;
            this.username = username;
            this.password = password;
            this.createdDate = createdDate;
        }
    }

    public CompletableFuture<User> getUser(int userId) {
        return CompletableFuture.supplyAsync(() -> {
            try (ResultSet rs = databaseLocal.get().executeQuery(USER_SELECT, userId)) {
                rs.next();
                return new User(rs.getInt("user_id"),
                        rs.getInt("type_id"),
                        rs.getString("user_name"),
                        rs.getString("password"),
                        rs.getTimestamp("created_date"));
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<Void> addFunctionOwnership(int userId, int funcId, String funcName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DatabaseConnection database = databaseLocal.get();
                database.executeUpdate(FUNCTION_OWNERSHIP_INSERT, userId, funcId, funcName);
                Log.info("Added ownership for user {} to function {}", userId, funcId);
                return null;
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<Void> updateFunctionOwnership(int userId, int oldFuncId, int newFuncId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DatabaseConnection database = databaseLocal.get();
                database.executeUpdate(FUNCTION_OWNERSHIP_UPDATE, newFuncId, userId, oldFuncId);
                Log.info("Updated ownership for user {} from {} to {}", userId, oldFuncId, newFuncId);
                return null;
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<Void> removeFunctionOwnership(int userId, int funcId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DatabaseConnection database = databaseLocal.get();
                database.executeUpdate(FUNCTION_OWNERSHIP_DELETE, userId, funcId);
                Log.info("Removed ownership for user {} to function {}", userId, funcId);
                return null;
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public static class FunctionOwnership {
        public int funcId;
        public Timestamp createdDate;
        public String funcName;

        public FunctionOwnership(int funcId, Timestamp createdDate, String funcName) {
            this.funcId = funcId;
            this.createdDate = createdDate;
            this.funcName = funcName;
        }
    }

    public CompletableFuture<ArrayList<FunctionOwnership>> getFunctionOwnerships(int userId) {
        return CompletableFuture.supplyAsync(() -> {
            ArrayList<FunctionOwnership> result = new ArrayList<>();
            try (ResultSet rs = databaseLocal.get().executeQuery(FUNCTION_OWNERSHIP_SELECT, userId)) {
                while (rs.next()) {
                    result.add(new FunctionOwnership(rs.getInt("func_id"),
                            rs.getTimestamp("created_date"),
                            rs.getString("func_name")));
                }
                return result;
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }
}
