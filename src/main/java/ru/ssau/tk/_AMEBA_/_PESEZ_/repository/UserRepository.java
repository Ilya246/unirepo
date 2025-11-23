package ru.ssau.tk._AMEBA_._PESEZ_.repository;

import ru.ssau.tk._AMEBA_._PESEZ_.dto.FunctionDTO;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.FunctionOwnershipDTO;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.OwnedFunctionDTO;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.UserDTO;

import java.sql.*;
import java.util.concurrent.*;

import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.Log;

public class UserRepository extends Repository {
    private static final String USER_ENSURE_TABLE = readCommand("UserCreateTable");
    private static final String USER_INSERT = readCommand("UserCreate");
    private static final String USER_DELETE = readCommand("UserDelete");
    private static final String USER_SELECT = readCommand("UserRead");
    private static final String USER_GET = readCommand("UserGet");
    private static final String USER_GET_BY_NAME = readCommand("UserGetByName");
    private static final String USER_SELECT_ALL = readCommand("UserReadAll");
    private static final String USER_UPDATE = readCommand("UserUpdate");

    private static final String FUNCTION_OWNERSHIP_ENSURE_TABLE = readCommand("FunctionOwnershipCreateTable");
    private static final String FUNCTION_OWNERSHIP_INSERT = readCommand("FunctionOwnershipCreate");
    private static final String FUNCTION_OWNERSHIP_UPDATE = readCommand("FunctionOwnershipUpdate");
    private static final String FUNCTION_OWNERSHIP_DELETE = readCommand("FunctionOwnershipDelete");
    private static final String FUNCTION_OWNERSHIP_SELECT = readCommand("FunctionOwnershipRead");
    private static final String FUNCTION_OWNERSHIP_SELECT_MANY = readCommand("FunctionOwnershipReadMany");

    private static final String OWNED_FUNCTION_SELECT = readCommand("ReadUserFunctions");

    public enum UserType {
        Normal(1),
        Admin(1 << 1);

        public final int typeId;
        UserType(int typeId) {
            this.typeId = typeId;
        }

        public static UserType fromInt(int from) {
            return switch (from) {
                case 1 -> Normal;
                case 1 << 1 -> Admin;
                default -> throw new IllegalArgumentException("Illegal user type " + from);
            };
        }
    }

    public UserRepository(String config) {
        super(config);
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

    public CompletableFuture<Integer> createUser(UserType typeId, String userName, String passwordHash) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DatabaseConnection database = databaseLocal.get();
                int userId = database.executeUpdateAndGetId(USER_INSERT, typeId.typeId, userName, passwordHash);
                Log.info("Created user {} with ID {}", userName, userId);
                return userId;
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<Void> updateUser(int userId, String newUserName, String newPasswordHash, UserType newType) {
        return CompletableFuture.runAsync(() -> {
            try {
                DatabaseConnection database = databaseLocal.get();
                database.executeUpdate(USER_UPDATE, newUserName, newPasswordHash, newType.typeId, userId);
                Log.info("Updated user ID {}", userId);
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<Void> deleteUser(int userId) {
        return CompletableFuture.runAsync(() -> {
            try {
                DatabaseConnection database = databaseLocal.get();
                database.executeUpdate(USER_DELETE, userId);
                Log.info("Deleted user ID {}", userId);
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<UserDTO> getUser(int userId) {
        return CompletableFuture.supplyAsync(() -> {
            try (ResultSet rs = databaseLocal.get().executeQuery(USER_SELECT, userId)) {
                if (!rs.first())
                    return null;
                return new UserDTO(rs.getInt("user_id"),
                        UserType.fromInt(rs.getInt("type_id")),
                        rs.getString("user_name"),
                        rs.getString("password"),
                        rs.getTimestamp("created_date"));
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<UserDTO> getUser(String username, String passwordHash) {
        return CompletableFuture.supplyAsync(() -> {
            try (ResultSet rs = databaseLocal.get().executeQuery(USER_GET, username, passwordHash)) {
                if (!rs.first())
                    return null;
                return new UserDTO(rs.getInt("user_id"),
                        UserType.fromInt(rs.getInt("type_id")),
                        rs.getString("user_name"),
                        rs.getString("password"),
                        rs.getTimestamp("created_date"));
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<UserDTO> getUser(String username) {
        return CompletableFuture.supplyAsync(() -> {
            try (ResultSet rs = databaseLocal.get().executeQuery(USER_GET_BY_NAME, username)) {
                if (!rs.first())
                    return null;
                return new UserDTO(rs.getInt("user_id"),
                        UserType.fromInt(rs.getInt("type_id")),
                        rs.getString("user_name"),
                        rs.getString("password"),
                        rs.getTimestamp("created_date"));
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<Void> addFunctionOwnership(int userId, int funcId, String funcName) {
        return CompletableFuture.runAsync(() -> {
            try {
                DatabaseConnection database = databaseLocal.get();
                database.executeUpdate(FUNCTION_OWNERSHIP_INSERT, userId, funcId, funcName);
                Log.info("Added ownership for user {} to function {}", userId, funcId);
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<Void> updateFunctionOwnership(int userId, int funcId, String newName) {
        return CompletableFuture.runAsync(() -> {
            try {
                DatabaseConnection database = databaseLocal.get();
                database.executeUpdate(FUNCTION_OWNERSHIP_UPDATE, newName, userId, funcId);
                Log.info("Updated ownership {} for user {} to name {}", funcId, userId, newName);
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<Void> removeFunctionOwnership(int userId, int funcId) {
        return CompletableFuture.runAsync(() -> {
            try {
                DatabaseConnection database = databaseLocal.get();
                database.executeUpdate(FUNCTION_OWNERSHIP_DELETE, userId, funcId);
                Log.info("Removed ownership for user {} to function {}", userId, funcId);
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<FunctionOwnershipDTO> getFunctionOwnership(int userId, int funcId) {
        return CompletableFuture.supplyAsync(() -> {
            try (ResultSet rs = databaseLocal.get().executeQuery(FUNCTION_OWNERSHIP_SELECT, userId, funcId)) {
                if (!rs.first())
                    return null;
                return new FunctionOwnershipDTO(userId,
                        rs.getInt("func_id"),
                        rs.getTimestamp("created_date"),
                        rs.getString("func_name"));
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<FunctionOwnershipDTO[]> getFunctionOwnerships(int userId) {
        return CompletableFuture.supplyAsync(() -> {
            try (ResultSet rs = databaseLocal.get().executeQuery(FUNCTION_OWNERSHIP_SELECT_MANY, userId)) {
                rs.last();
                int count = rs.getRow();
                var result = new FunctionOwnershipDTO[count];
                rs.first();
                for (int i = 0; i < count; i++) {
                    result[i] = new FunctionOwnershipDTO(userId,
                            rs.getInt("func_id"),
                            rs.getTimestamp("created_date"),
                            rs.getString("func_name"));
                    rs.next();
                }
                return result;
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<OwnedFunctionDTO[]> getFunctions(int userId) {
        return CompletableFuture.supplyAsync(() -> {
            try (ResultSet rs = databaseLocal.get().executeQuery(OWNED_FUNCTION_SELECT, userId)) {
                rs.last();
                int count = rs.getRow();
                var result = new OwnedFunctionDTO[count];
                rs.first();
                for (int i = 0; i < count; i++) {
                    result[i] = new OwnedFunctionDTO(new FunctionDTO(
                            rs.getInt("func_id"),
                            rs.getInt("type_id"),
                            rs.getString("expression")
                        ), new FunctionOwnershipDTO(
                            userId,
                            rs.getInt("func_id"),
                            rs.getTimestamp("created_date"),
                            rs.getString("func_name")));
                    rs.next();
                }
                return result;
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<UserDTO[]> getAllUsers() {
        return CompletableFuture.supplyAsync(() -> {
            try (ResultSet rs = databaseLocal.get().executeQuery(USER_SELECT_ALL)) {
                rs.last();
                int count = rs.getRow();
                var result = new UserDTO[count];
                rs.first();
                for (int i = 0; i < count; i++) {
                    result[i] = new UserDTO(rs.getInt("user_id"),
                            UserType.fromInt(rs.getInt("type_id")),
                            rs.getString("user_name"),
                            rs.getString("password"),
                            rs.getTimestamp("created_date"));
                    rs.next();
                }
                return result;
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }
}
