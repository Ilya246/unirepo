package ru.ssau.tk._AMEBA_._PESEZ_.service;

import ru.ssau.tk._AMEBA_._PESEZ_.dto.*;
import ru.ssau.tk._AMEBA_._PESEZ_.exceptions.InvalidLoginException;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.*;
import static ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository.*;
import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.*;

public class UserService {
    private final FunctionRepository funcRepo;
    private final UserRepository userRepo;

    public UserService(String config) {
        funcRepo = new FunctionRepository(config);
        userRepo = new UserRepository(config);
        funcRepo.ensureTables();
        userRepo.ensureTables();
    }

    public CompletableFuture<OwnedFunctionDTO[]> getUserFunctions(int userId, int types) {
        return CompletableFuture.supplyAsync(() -> {
            OwnedFunctionDTO[] functions = userRepo.getFunctions(userId).join();
            int to = 0;
            for (OwnedFunctionDTO function : functions) {
                FunctionDTO func = function.function;
                if ((func.funcType & types) != 0)
                    to++;
            }
            return Arrays.copyOf(functions, to);
        });
    }

    public CompletableFuture<OwnedFunctionDTO[]> getUserFunctions(int userId) {
        return getUserFunctions(userId, ~0); // получаем функции всех типов
    }

    public CompletableFuture<OwnedFunctionDTO> getUserFunction(int userId, int funcId) {
        return CompletableFuture.supplyAsync(() -> {
            FunctionOwnershipDTO ownership = userRepo.getFunctionOwnership(userId, funcId).join();
            if (ownership == null)
                return null;
            return new OwnedFunctionDTO(funcRepo.getFunctionData(ownership.funcId).join(), ownership);
        });
    }

    public CompletableFuture<Void> deleteFunctionOwnership(int userId, int funcId) {
        return userRepo.removeFunctionOwnership(userId, funcId);
    }

    public CompletableFuture<UserDTO[]> getUsers(int types) {
        return CompletableFuture.supplyAsync(() -> {
            UserDTO[] users = userRepo.getAllUsers().join();

            if (types == ~0)
                return users;

            var userList = new ArrayList<UserDTO>();
            for (UserDTO user : users) {
                if ((user.userType.typeId & types) != 0)
                    userList.add(user);
            }
            return userList.toArray(new UserDTO[0]);
        });
    }

    public CompletableFuture<UserDTO[]> getUsers() {
        return getUsers(~0);
    }

    public CompletableFuture<UserDTO> getUser(int userId) {
            return userRepo.getUser(userId);
    }

    public CompletableFuture<UserDTO> getUserByCredentials(String username, String password) {
        return userRepo.getUser(username, getBase64Hash(password));
    }

    public CompletableFuture<Void> deleteUser(int userId) {
        return CompletableFuture.runAsync(() -> {
            FunctionOwnershipDTO[] ownerships = userRepo.getFunctionOwnerships(userId).join();
            CompletableFuture<Void>[] futures = new CompletableFuture[ownerships.length];
            for (int i = 0; i < ownerships.length; i++) {
                futures[i] = userRepo.removeFunctionOwnership(userId, ownerships[i].funcId);
            }
            CompletableFuture.allOf(futures).join();
            userRepo.deleteUser(userId).join();
        });
    }

    public CompletableFuture<Integer> createUserFunction(int userId, String name, String expression) {
        return CompletableFuture.supplyAsync(() -> {
            int funcId = funcRepo.createMathFunction(expression).join();
            userRepo.addFunctionOwnership(userId, funcId, name).join();
            Log.info("Created owned function ('{}': {}) for user {}", name, expression, userId);
            return funcId;
        });
    }

    public CompletableFuture<Integer> createUserTabulatedFunction(int userId, String name, String expression, double from, double to, int pointCount) {
        return CompletableFuture.supplyAsync(() -> {
            int funcId = funcRepo.createTabulated(expression, from, to, pointCount).join();
            userRepo.addFunctionOwnership(userId, funcId, name).join();
            Log.info("Created owned tabulated ('{}': {}, {} pts) for user {}", name, expression, pointCount, userId);
            return funcId;
        });
    }

    public CompletableFuture<Integer> createUserPureTabulated(int userId, String name, double[] xValues, double[] yValues) {
        return CompletableFuture.supplyAsync(() -> {
            int funcId = funcRepo.createPureTabulated(xValues, yValues).join();
            userRepo.addFunctionOwnership(userId, funcId, name).join();
            Log.info("Created owned pure tabulated ('{}', {} pts) for user {}", name, xValues.length, userId);
            return funcId;
        });
    }

    public CompletableFuture<Integer> createUserComposite(int userId, String name, int innerId, int outerId) {
        return CompletableFuture.supplyAsync(() -> {
            int funcId = funcRepo.createComposite(innerId, outerId).join();
            userRepo.addFunctionOwnership(userId, funcId, name).join();
            Log.info("Created owned composite ('{}', {}({})) for user {}", name, outerId, innerId, userId);
            return funcId;
        });
    }

    public CompletableFuture<Void> addOwnership(int userId, int funcId, String name) {
        return userRepo.addFunctionOwnership(userId, funcId, name);
    }

    public CompletableFuture<Void> updateOwnership(int userId, int funcId, String newName) {
        return userRepo.updateFunctionOwnership(userId, funcId, newName);
    }

    public CompletableFuture<Integer> createUser(UserType typeId, String username, String password) {
        return CompletableFuture.supplyAsync(() -> {
            if (username.isEmpty())
                throw new InvalidLoginException("Username cannot be empty.");
            if (password.isEmpty())
                throw new InvalidLoginException("Password cannot be empty.");
            if (userRepo.getUser(username).join() != null)
                throw new InvalidLoginException("User with this username already exists.");

            Log.info("Creating user '{}' of type {}", username, typeId);
            return userRepo.createUser(typeId, username, getBase64Hash(password)).join();
        });
    }

    public static void sortUsersDate(UserDTO[] in) {
        Arrays.sort(in, Comparator.comparing(dto -> dto.createdDate));
    }

    public static void sortFunctionsDate(OwnedFunctionDTO[] in) {
        Arrays.sort(in, Comparator.comparing(dto -> dto.ownership.createdDate));
    }

    public CompletableFuture<Void> updateUser(int userId, String newUserName, String newPassword, UserType newType) {
        Log.info("Updating user {} to: name '{}', type {}", userId, newUserName, newType);
        return userRepo.updateUser(userId, newUserName, getBase64Hash(newPassword), newType);
    }
}
