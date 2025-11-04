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
    }

    public FunctionDTO[] getUserFunctions(int userId, int types) {
        try {
            FunctionOwnershipDTO[] ownerships = userRepo.getFunctionOwnerships(userId).get();
            Arrays.sort(ownerships, Comparator.comparing(a -> a.createdDate));
            int count = ownerships.length;

            CompletableFuture<FunctionDTO>[] futures = new CompletableFuture[count];
            for (int i = 0; i < count; i++) {
                futures[i] = funcRepo.getFunctionData(ownerships[i].funcId);
            }

            var functions = new ArrayList<FunctionDTO>();
            for (int i = 0; i < count; i++) {
                FunctionDTO func = futures[i].get();
                if ((func.funcType & types) != 0)
                    functions.add(func);
            }
            return functions.toArray(new FunctionDTO[0]);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public FunctionDTO[] getUserFunctions(int userId) {
        return getUserFunctions(userId, ~0); // получаем функции всех типов
    }

    public UserDTO[] getUsers(int types) {
        try {
            UserDTO[] users = userRepo.getAllUsers().get();
            Arrays.sort(users, Comparator.comparing(a -> a.createdDate));
            int count = users.length;

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
}