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

    public CompletableFuture<OwnedFunctionDTO[]> getUserFunctions(int userId, int types) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                FunctionOwnershipDTO[] ownerships = userRepo.getFunctionOwnerships(userId).get();
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
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<OwnedFunctionDTO[]> getUserFunctions(int userId) {
        return getUserFunctions(userId, ~0); // получаем функции всех типов
    }

    public CompletableFuture<OwnedFunctionDTO> getUserFunction(int userId, int funcId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                FunctionOwnershipDTO ownership = userRepo.getFunctionOwnership(userId, funcId).get();
                if (ownership == null)
                    return null;
                return new OwnedFunctionDTO(funcRepo.getFunctionData(ownership.funcId).get(), ownership);
            } catch (InterruptedException | ExecutionException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<Void> deleteUserFunction(int userId, int funcId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                userRepo.removeFunctionOwnership(userId, funcId).get();
                return null;
            } catch (InterruptedException | ExecutionException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<UserDTO[]> getUsers(int types) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                UserDTO[] users = userRepo.getAllUsers().get();

                if (types == ~0)
                    return users;

                var userList = new ArrayList<UserDTO>();
                for (UserDTO user : users) {
                    if ((user.userType & types) != 0)
                        userList.add(user);
                }
                return userList.toArray(new UserDTO[0]);
            } catch (InterruptedException | ExecutionException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<UserDTO[]> getUsers() {
        return getUsers(~0);
    }

    public CompletableFuture<UserDTO> getUser(int userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return userRepo.getUser(userId).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<Void> deleteUser(int userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                FunctionOwnershipDTO[] ownerships = userRepo.getFunctionOwnerships(userId).get();
                CompletableFuture<Void>[] futures = new CompletableFuture[ownerships.length];
                for (int i = 0; i < ownerships.length; i++) {
                    futures[i] = userRepo.removeFunctionOwnership(userId, ownerships[i].funcId);
                }
                for (var f : futures) {
                    f.get();
                }
                userRepo.deleteUser(userId).get();
                return null;
            } catch (InterruptedException | ExecutionException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<Integer> createUserFunction(int userId, String name, String expression) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                int funcId = funcRepo.createMathFunction(expression).get();
                userRepo.addFunctionOwnership(userId, funcId, name).get();
                return funcId;
            } catch (InterruptedException | ExecutionException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<Integer> createUserTabulatedFunction(int userId, String name, String expression, double from, double to, int pointCount) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                int funcId = funcRepo.createTabulated(expression, from, to, pointCount).get();
                userRepo.addFunctionOwnership(userId, funcId, name).get();
                return funcId;
            } catch (InterruptedException | ExecutionException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<Integer> createUserPureTabulated(int userId, String name, double[] xValues, double[] yValues) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                int funcId = funcRepo.createPureTabulated(xValues, yValues).get();
                userRepo.addFunctionOwnership(userId, funcId, name).get();
                return funcId;
            } catch (InterruptedException | ExecutionException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<Integer> createUserComposite(int userId, String name, int innerId, int outerId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                int funcId = funcRepo.createComposite(innerId, outerId).get();
                userRepo.addFunctionOwnership(userId, funcId, name).get();
                return funcId;
            } catch (InterruptedException | ExecutionException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<Integer> createUser(int typeId, String username, String password) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return userRepo.createUser(typeId, username, password).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new CompletionException(e);
            }
        });
    }

    public static void sortUsersDate(UserDTO[] in) {
        Arrays.sort(in, Comparator.comparing(dto -> dto.createdDate));
    }

    public static void sortFunctionsDate(OwnedFunctionDTO[] in) {
        Arrays.sort(in, Comparator.comparing(dto -> dto.ownership.createdDate));
    }
}
