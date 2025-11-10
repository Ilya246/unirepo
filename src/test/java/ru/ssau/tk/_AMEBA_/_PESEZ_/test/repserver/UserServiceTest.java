package ru.ssau.tk._AMEBA_._PESEZ_.test.repserver;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.service.FunctionService;
import ru.ssau.tk._AMEBA_._PESEZ_.service.UserService;
import ru.ssau.tk._AMEBA_._PESEZ_.utility.TestHibernateSessionFactoryUtil;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest extends BaseRepositoryTest {

    private SessionFactory sessionFactory;
    private FunctionService functionService;
    private FunctionRepository functionRepository;
    private UserRepository userRepository;
    private UserService userService;
    //private final AtomicInteger idGenerator = new AtomicInteger(1000);


    @BeforeEach
    void setUp() {
        sessionFactory = TestHibernateSessionFactoryUtil.getSessionFactory();
        functionService = new FunctionService(sessionFactory);
        functionRepository = new FunctionRepository(sessionFactory);
        userRepository = new UserRepository(sessionFactory);
        userService=new UserService(sessionFactory);
    }

    @Test
    void testGetUserById() {

        UserEntity user = new UserEntity(1, "Test User", "password123");
        userService.saveUser(user);
        Long userId = user.getUserId();


        UserEntity foundUser = userService.getUserById(userId);

        assertNotNull(foundUser);
        assertEquals(userId, foundUser.getUserId());
        assertEquals("Test User", foundUser.getUserName());
        assertEquals(1, foundUser.getTypeId());
        assertEquals("password123", foundUser.getPassword());
        assertNotNull(foundUser.getCreatedDate());
    }


    @Test
    void testGetAllUsers() {
        UserEntity user1 = new UserEntity(1, "Test User", "password123");

        UserEntity user2 = new UserEntity(1, "Test User", "password123");


        userService.saveUser(user1);
        userService.saveUser(user2);

        List<UserEntity> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(2, users.size());
    }

    @Test
    void testGetUsersByType() {

        UserEntity user1 = new UserEntity(1, "Test User", "password123");

        UserEntity user2 = new UserEntity(2, "Test User", "password123");

        UserEntity user3 = new UserEntity(1, "Test User", "password123");


        userService.saveUser(user1);
        userService.saveUser(user2);
        userService.saveUser(user3);


        List<UserEntity> type1Users = userService.getUsersByType(1);


        assertNotNull(type1Users);
        assertEquals(2, type1Users.size());
        assertTrue(type1Users.stream().allMatch(user -> user.getTypeId() == 1));
    }


    @Test
    void testSaveUser() {

        UserEntity user = new UserEntity(1, "Test User", "password123");


        userService.saveUser(user);
        UserEntity savedUser = userService.getUserById(user.getUserId());
        assertNotNull(savedUser);
        assertEquals("Test User", savedUser.getUserName());
        assertEquals(1, savedUser.getTypeId());
        assertNotNull(savedUser.getCreatedDate());
    }

    @Test
    void testUpdateUser() {

        UserEntity user = new UserEntity(1, "Test User", "password123");
        userService.saveUser(user);

        user.setUserName("Updated Name");
        user.setTypeId(2);

        UserEntity updatedUser = userService.updateUser(user);

        assertNotNull(updatedUser);
        assertEquals("Updated Name", updatedUser.getUserName());
        assertEquals(2, updatedUser.getTypeId());

        UserEntity userFromDb = userService.getUserById(user.getUserId());
        assertEquals("Updated Name", userFromDb.getUserName());
        assertEquals(2, userFromDb.getTypeId());
    }

    @Test
    void testDeleteUser() {

        UserEntity user = new UserEntity(1, "Test User", "password123");

        userService.saveUser(user);
        Long userId = user.getUserId();

        assertNotNull(userService.getUserById(userId));

        userService.deleteUser(userId);

        UserEntity deletedUser = userService.getUserById(userId);
        assertNull(deletedUser);
    }


    @Test
    void testSortByDateAscending() throws InterruptedException {
        // Arrange
        UserEntity user1 = new UserEntity(1, "User1", "password123");

        Thread.sleep(100); // Задержка для разницы во времени
        UserEntity user2 = new UserEntity(1, "Test User", "password123");


        userService.saveUser(user1);
        userService.saveUser(user2);

        List<UserEntity> ascendingUsers = userService.sortByDate(false);
        assertNotNull(ascendingUsers);
        assertEquals(2, ascendingUsers.size());

        assertEquals("User1", ascendingUsers.get(0).getUserName());
    }

    @Test
    void testSortByDateDescending() throws InterruptedException {
        // Arrange
        UserEntity user1 = new UserEntity(1, "User1", "password123");

        Thread.sleep(100); // Задержка для разницы во времени
        UserEntity user2 = new UserEntity(1, "User2", "password123");


        userService.saveUser(user1);
        userService.saveUser(user2);

        List<UserEntity> descendingUsers = userService.sortByDate(true);

        assertNotNull(descendingUsers);
        assertEquals(2, descendingUsers.size());
        assertEquals("User2", descendingUsers.get(0).getUserName());
    }


    @Test
    void testGetUserFunctions_WhenUserHasNoFunctions_ShouldReturnEmptyList() {
        // Arrange
        UserEntity user1 = new UserEntity(1, "User1", "password123");
        userService.saveUser(user1);

        // Act
        List<FunctionEntity> functions = userService.getUserFunctions(user1.getUserId());

        // Assert
        assertNotNull(functions);
        assertTrue(functions.isEmpty());
    }

}