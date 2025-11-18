package ru.ssau.tk._AMEBA_._PESEZ_.service;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.UserRequest;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.UserResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.exceptions.CustomException;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionOwnershipRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.BaseRepositoryTest;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.utility.TestHibernateSessionFactoryUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest extends BaseRepositoryTest {

    private UserServiceImpl service;
    private SessionFactory sessionFactory;
    @BeforeEach
    void setUp() {
        sessionFactory  = TestHibernateSessionFactoryUtil.getSessionFactory();
        UserRepository userRepo = new UserRepository(sessionFactory);
        FunctionRepository funcRepo = new FunctionRepository(sessionFactory);
        FunctionOwnershipRepository ownershipRepo = new FunctionOwnershipRepository(sessionFactory);
        service = new UserServiceImpl(userRepo, ownershipRepo, funcRepo);
    }

/*    @Test
    void testCreateAndGetUser() {
        // Создаем пользователя
        UserRequest request = new UserRequest();
        request.setUserName("test");
        request.setPassword("123");


        UserResponse created = service.createUser(request);
        assertNotNull(created.getUserId());

        // Получаем пользователя
        UserResponse found = service.getUser(created.getUserId());
        assertEquals("test", found.getUserName());
    }*/

    @Test
    void testUpdateUser() {
        // Создаем пользователя
        UserEntity user = new UserEntity();
        user.setUserName("old");
        user.setPassword("oldpass");
        user.setTypeId(1);
        // Сохраняем через репозиторий напрямую
        UserRepository userRepo = new UserRepository(sessionFactory);
        userRepo.save(user);

        // Обновляем
        UserRequest update = new UserRequest();
        update.setUserName("new");

        UserResponse updated = service.updateUser(user.getUserId(), update);
        assertEquals("new", updated.getUserName());
    }

    @Test
    void testDeleteUser() {
        // Создаем пользователя
        UserEntity user = new UserEntity();
        user.setUserName("todelete");
        user.setTypeId(1);
        UserRepository userRepo = new UserRepository(sessionFactory);
        userRepo.save(user);

        // Удаляем
        service.deleteUser(user.getUserId());

        // Проверяем что удален
        assertThrows(CustomException.class, () -> {
            service.getUser(user.getUserId());
        });
    }

    @Test
    void testGetAllUsers() {
        // Создаем несколько пользователей
        UserRepository userRepo = new UserRepository(sessionFactory);

        UserEntity user1 = new UserEntity();
        user1.setUserName("user1");
        user1.setTypeId(1);
        userRepo.save(user1);

        UserEntity user2 = new UserEntity();
        user2.setUserName("user2");
        user2.setTypeId(2);
        userRepo.save(user2);

        // Получаем всех
        List<UserResponse> users = service.getAllUsers();
        assertTrue(users.size() >= 2);
    }
}