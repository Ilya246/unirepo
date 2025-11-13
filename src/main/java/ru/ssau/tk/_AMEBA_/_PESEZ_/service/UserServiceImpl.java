package ru.ssau.tk._AMEBA_._PESEZ_.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.UserRequest;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.UserResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.exceptions.CustomException;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionOwnershipRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final FunctionOwnershipRepository ownershipRepository;
    private final FunctionRepository functionRepository;

    @Override
    public UserResponse createUser(UserRequest request) {
        UserEntity user = new UserEntity();
        user.setTypeId(request.getTypeId());
        user.setUserName(request.getUserName());
        user.setPassword(request.getPassword());
        userRepository.save(user);
        log.info("User created with id: {}", user.getUserId());

        return convertToResponse(user);
    }

    @Override
    public UserResponse getUser(Long id) {
        UserEntity user = getUserDb(id);
        return convertToResponse(user);
    }

    @Override
    public UserEntity getUserDb(Long id) {
        UserEntity user = userRepository.findById(id);
        if (user == null) {
            throw new CustomException("User not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        return user;
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest request) {
        UserEntity user = getUserDb(id);


        if (request.getTypeId() != 0) {
            user.setTypeId(request.getTypeId());
        }
        if (request.getUserName() != null && !request.getUserName().isEmpty()) {
            user.setUserName(request.getUserName());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(request.getPassword());
        }
        if (request.getCreatedDate() != null) {
            user.setCreatedDate(request.getCreatedDate());
        }

        UserEntity updatedUser = userRepository.update(user);
        log.info("User updated with id: {}", id);

        return convertToResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        UserEntity user = getUserDb(id);
        userRepository.deleteById(id);
        log.info("User deleted with id: {}", id);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        log.info("Found {} users", users.size());

        return users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getUsersByType(Integer typeId) {
        List<UserEntity> users = userRepository.findByType(typeId);
        log.info("Found {} users with type: {}", users.size(), typeId);

        return users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getUsersSortedByDate(Boolean descending) {
        boolean sortDescending = descending != null ? descending : true;
        List<UserEntity> users = userRepository.findAllOrderByCreatedDate(sortDescending);
        log.info("Found {} users sorted by date {}", users.size(), sortDescending ? "DESC" : "ASC");

        return users.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FunctionEntity> getUserFunctions(Long userId) {
        UserEntity user = getUserDb(userId);

        List<FunctionEntity> functions = ownershipRepository.findByUserId(userId).stream()
                .map(ownership -> functionRepository.findById(ownership.getId().getFuncId()))
                .filter(function -> function != null)
                .collect(Collectors.toList());

        log.info("Found {} functions for user: {}", functions.size(), userId);
        return functions;
    }

    private UserResponse convertToResponse(UserEntity user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .typeId(user.getTypeId())
                .userName(user.getUserName())
                .password(user.getPassword()) // Осторожно! Возможно стоит не возвращать пароль
                .createdDate(user.getCreatedDate())
                .build();
    }
}