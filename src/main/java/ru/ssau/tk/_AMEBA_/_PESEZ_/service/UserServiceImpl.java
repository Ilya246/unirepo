package ru.ssau.tk._AMEBA_._PESEZ_.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.UserRequest;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.UserResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.enums.UserType;
import ru.ssau.tk._AMEBA_._PESEZ_.exceptions.CustomException;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionOwnershipRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.UserService;
import ru.ssau.tk._AMEBA_._PESEZ_.utility.HashUtil;
import ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.getBase64Hash;

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
        user.setTypeId(request.getUserType().typeId);
        user.setUserName(request.getUserName());
        user.setPassword(getBase64Hash(request.getPassword()));
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


        if (request.getUserType() != null) {
            user.setTypeId(request.getUserType().typeId);
        }
        if (request.getUserName() != null && !request.getUserName().isEmpty()) {
            user.setUserName(request.getUserName());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(getBase64Hash(request.getPassword()));
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
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        log.info("Found {} functions for user: {}", functions.size(), userId);
        return functions;
    }

    @Override
    public UserResponse convertToResponse(UserEntity user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setUserType(new Long(user.getTypeId()));
        response.setUsername(user.getUserName());
        response.setPasswordHash(user.getPassword());
        response.setCreatedDate(Timestamp.from(user.getCreatedDate().toInstant()));
        return response;
    }



    @Override
    public UserType toType(int typeId){
        return switch (typeId) {
            case 1 -> UserType.Normal;
            case 2 -> UserType.Admin;
            default -> throw new IllegalArgumentException("Illegal user type " + typeId);
        };
    }

    @Override
    public UserEntity findByCredentials(String userName, String password) {
        UserEntity user = userRepository.findByCredentials(userName,getBase64Hash(password));
        if (user == null) {
            throw new CustomException("User not found with id: " + userName, HttpStatus.NOT_FOUND);
        }
        return user;
    }


    /**
     * Аутентифицирует пользователя и проверяет роль
     * @param userName - имя пользователя
     * @param password - пароль в открытом виде
     * @return UserEntity если аутентификация успешна
     */
    @Override
    public UserEntity authenticate(String userName, String password) {
        System.out.println("=== USER SERVICE AUTH ===");
        System.out.println("Input username: '" + userName + "'");
        System.out.println("Input password: '" + password + "'");

        // Хешируем пароль для поиска в БД
        String hashedPassword = Utility.getBase64Hash(password);
        System.out.println("Hashed password: " + hashedPassword);

        System.out.println("Searching in database...");
        UserEntity user = userRepository.findByCredentials(userName, hashedPassword);

        if (user == null) {
            System.out.println("USER NOT FOUND in database");
            // Давайте посмотрим, что вообще есть в базе
            List<UserEntity> allUsers = userRepository.findAll();
            System.out.println("Total users in DB: " + allUsers.size());
            for (UserEntity u : allUsers) {
                System.out.println("DB User: " + u.getUserName() + ", ID: " + u.getUserId());
            }

            throw new CustomException("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }

        System.out.println("USER FOUND: " + user.getUserName() + ", ID: " + user.getUserId());
        return user;
    }


    @Override
    public UserEntity authenticateWithRole(String userName, String password, UserType requiredRole) {
        UserEntity user = userRepository.findByCredentials(userName,password);
        if (user == null) {
            log.warn("Authentication failed: user not found - {}", userName);
            throw new CustomException("User not found", HttpStatus.UNAUTHORIZED);
        }

        // Проверяем роль, если нужно
        if (requiredRole != null) {
            UserType userRole = toType(user.getTypeId());
            if (!userRole.equals(requiredRole)) {
                log.warn("Authorization failed: user {} does not have required role {}", userName, requiredRole);
                throw new CustomException("Access denied", HttpStatus.FORBIDDEN);
            }
        }

        log.info("User authenticated successfully: {} with role {}", userName, toType(user.getTypeId()));
        return user;
    }
}