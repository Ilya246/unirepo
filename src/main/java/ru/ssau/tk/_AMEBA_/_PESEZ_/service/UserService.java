package ru.ssau.tk._AMEBA_._PESEZ_.service;

import org.hibernate.SessionFactory;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionOwnershipRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository;
import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.Log;

import java.util.List;

public class UserService {

    private final UserRepository userRepository;
    private final FunctionOwnershipRepository ownershipRepository;
    private final FunctionRepository functionRepository;

    public UserService(SessionFactory sessionFactory) {
        this.userRepository = new UserRepository(sessionFactory);
        this.ownershipRepository = new FunctionOwnershipRepository(sessionFactory);
        this.functionRepository = new FunctionRepository(sessionFactory);
        Log.debug("Все репозитории UserService успешно инициализированы");
    }

    public List<FunctionEntity> getUserFunctions(Long userId) {
        try {
            UserEntity user = userRepository.findById(userId);
            if (user == null) {
                throw new IllegalArgumentException("User not found with ID: " + userId);
            }

            List<FunctionEntity> functions = ownershipRepository.findByUserId(userId).stream()
                    .map(ownership -> functionRepository.findById(ownership.getId().getFuncId()))
                    .filter(function -> function != null)
                    .toList();

            Log.debug("Для пользователя {} найдено {} функций", userId, functions.size());
            return functions;
        } catch (Exception e) {
            Log.error("Ошибка при получении функций пользователя {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    public UserEntity getUserById(Long userId) {
        try {
            UserEntity user = userRepository.findById(userId);
            if (user != null) {
                Log.debug("Пользователь найден: ID {}, имя: {}", userId, user.getUserName());
            } else {
                Log.debug("Пользователь с ID {} не найден", userId);
            }
            return user;
        } catch (Exception e) {
            Log.error("Ошибка при получении пользователя {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    public List<UserEntity> getAllUsers() {
        try {
            List<UserEntity> users = userRepository.findAll();
            Log.debug("Получено {} пользователей из базы данных", users.size());
            return users;
        } catch (Exception e) {
            Log.error("Ошибка при получении списка всех пользователей: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<UserEntity> getUsersByType(int typeId) {
        try {
            List<UserEntity> users = userRepository.findByType(typeId);
            Log.debug("Найдено {} пользователей типа {}", users.size(), typeId);
            return users;
        } catch (Exception e) {
            Log.error("Ошибка при поиске пользователей типа {}: {}", typeId, e.getMessage(), e);
            throw e;
        }
    }

    public void saveUser(UserEntity user) {
        try {
            userRepository.save(user);
            Log.debug("Пользователь успешно сохранен с ID: {}", user.getUserId());
        } catch (Exception e) {
            Log.error("Ошибка при сохранении пользователя '{}': {}", user.getUserName(), e.getMessage(), e);
            throw e;
        }
    }

    public void deleteUser(Long userId) {
        try {
            userRepository.deleteById(userId);
            Log.debug("Пользователь с ID {} успешно удален", userId);
        } catch (Exception e) {
            Log.error("Ошибка при удалении пользователя {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    public UserEntity updateUser(UserEntity user) {
        try {
            UserEntity updatedUser = userRepository.update(user);
            Log.debug("Пользователь с ID {} успешно обновлен. Новое имя: {}",
                    user.getUserId(), updatedUser.getUserName());
            return updatedUser;
        } catch (Exception e) {
            Log.error("Ошибка при обновлении пользователя {}: {}", user.getUserId(), e.getMessage(), e);
            throw e;
        }
    }

    public List<UserEntity> sortByDate(boolean descending) {
        String order = descending ? "убыванию" : "возрастанию";
        try {
            List<UserEntity> sortedUsers = userRepository.findAllOrderByCreatedDate(descending);
            Log.debug("Получено {} пользователей, отсортированных по {}", sortedUsers.size(), order);
            return sortedUsers;
        } catch (Exception e) {
            Log.error("Ошибка при сортировке пользователей по дате: {}", e.getMessage(), e);
            throw e;
        }
    }
}