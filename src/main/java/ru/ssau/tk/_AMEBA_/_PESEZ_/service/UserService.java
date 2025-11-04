package ru.ssau.tk._AMEBA_._PESEZ_.service;

import ru.ssau.tk._AMEBA_._PESEZ_.dto.*;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.*;

public class UserService {
    private final FunctionRepository funcRepo;
    private final UserRepository userRepo;

    public UserService(String url) {
        funcRepo = new FunctionRepository(url);
        userRepo = new UserRepository(url);
        funcRepo.ensureTables();
        userRepo.ensureTables();
    }

    public OwnedFunctionDTO[] getUserFunctions(int userId, int types) {
        try {
            FunctionOwnershipDTO[] ownerships = userRepo.getFunctionOwnerships(userId).get();
            Arrays.sort(ownerships, Comparator.comparing(a -> a.createdDate));
            int count = ownerships.length;

            CompletableFuture<FunctionDTO>[] futures = new CompletableFuture[count];
            for (int i = 0; i < count; i++) {
                futures[i] = funcRepo.getFunctionData(ownerships[i].funcId);
            }

            var functions = new ArrayList<OwnedFunctionDTO>();
            for (int i = 0; i < count; i++) {
                FunctionDTO func = futures[i].get();
                if ((func.funcType & types) != 0)
                    functions.add(new OwnedFunctionDTO(func, ownerships[i]));
            }
            return functions.toArray(new OwnedFunctionDTO[0]);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public OwnedFunctionDTO[] getUserFunctions(int userId) {
        return getUserFunctions(userId, ~0); // получаем функции всех типов
    }

    public OwnedFunctionDTO getUserFunction(int userId, int funcId) {
        try {
            FunctionOwnershipDTO ownership = userRepo.getFunctionOwnership(userId, funcId).get();
            if (ownership == null)
                return null;
            return new OwnedFunctionDTO(funcRepo.getFunctionData(ownership.funcId).get(), ownership);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteUserFunction(int userId, int funcId) {
        try {
            userRepo.removeFunctionOwnership(userId, funcId).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public UserDTO[] getUsers(int types) {
        try {
            UserDTO[] users = userRepo.getAllUsers().get();
            Arrays.sort(users, Comparator.comparing(a -> a.createdDate));

            if (types == ~0)
                return users;

            var userList = new ArrayList<UserDTO>();
            for (UserDTO user : users) {
                if ((user.userType & types) != 0)
                    userList.add(user);
            }
            return userList.toArray(new UserDTO[0]);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public UserDTO[] getUsers() {
        return getUsers(~0);
    }

    public UserDTO getUser(int userId) {
        try {
            return userRepo.getUser(userId).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteUser(int userId) {
        try {
            FunctionOwnershipDTO[] ownerships = userRepo.getFunctionOwnerships(userId).get();
            for (FunctionOwnershipDTO ownership : ownerships) {
                userRepo.removeFunctionOwnership(userId, ownership.funcId);
            }
            userRepo.deleteUser(userId).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public int createUserFunction(int userId, String name, String expression) {
        try {
            int funcId = funcRepo.createMathFunction(expression).get();
            userRepo.addFunctionOwnership(userId, funcId, name).get();
            return funcId;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public int createUserTabulatedFunction(int userId, String name, String expression, double from, double to, int pointCount) {
        try {
            int funcId = funcRepo.createTabulated(expression, from, to, pointCount).get();
            userRepo.addFunctionOwnership(userId, funcId, name).get();
            return funcId;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public int createUserPureTabulated(int userId, String name, double[] xValues, double[] yValues) {
        try {
            int funcId = funcRepo.createPureTabulated(xValues, yValues).get();
            userRepo.addFunctionOwnership(userId, funcId, name).get();
            return funcId;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public int createUserComposite(int userId, String name, int innerId, int outerId) {
        try {
            int funcId = funcRepo.createComposite(innerId, outerId).get();
            userRepo.addFunctionOwnership(userId, funcId, name).get();
            return funcId;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public int createUser(int typeId, String username, String password) {
        try {
            return userRepo.createUser(typeId, username, password).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}