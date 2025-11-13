package ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces;

import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.UserRequest;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.UserResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest request);

    UserResponse getUser(Long id);

    UserEntity getUserDb(Long id);

    UserResponse updateUser(Long id, UserRequest request);

    void deleteUser(Long id);

    List<UserResponse> getAllUsers();

    List<UserResponse> getUsersByType(Integer typeId);

    List<UserResponse> getUsersSortedByDate(Boolean descending);

    List<FunctionEntity> getUserFunctions(Long userId);
}
