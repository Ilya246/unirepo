package ru.ssau.tk._AMEBA_._PESEZ_.service;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.FunctionOwnershipRequest;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.FunctionOwnershipResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.OwnedFunctionResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionOwnershipEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.exceptions.CustomException;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionOwnershipRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.BaseRepositoryTest;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.FunctionService;
import ru.ssau.tk._AMEBA_._PESEZ_.utility.TestHibernateSessionFactoryUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FunctionOwnershipServiceImplTest extends BaseRepositoryTest {

    private SessionFactory sessionFactory;
    private FunctionOwnershipServiceImpl ownershipService;
    private FunctionOwnershipRepository ownershipRepo;
    private UserRepository userRepo;
    private FunctionRepository functionRepo;
    private FunctionService functionService;

    @BeforeEach
    void setUp() {
        sessionFactory = TestHibernateSessionFactoryUtil.getSessionFactory();
        ownershipRepo = new FunctionOwnershipRepository(sessionFactory);
        userRepo = new UserRepository(sessionFactory);
        functionRepo = new FunctionRepository(sessionFactory);
        ownershipService = new FunctionOwnershipServiceImpl(ownershipRepo, userRepo, functionRepo, functionService);
    }

    private FunctionEntity createTestFunction(String expression, int typeId) {
        FunctionEntity function = new FunctionEntity();
        function.setExpression(expression);
        function.setTypeId(typeId);
        functionRepo.save(function);
        return function;
    }

    private UserEntity createTestUser(String username, Integer type, String password) {
        UserEntity user = new UserEntity();
        user.setUserName(username);
        user.setPassword(password);
        user.setTypeId(type);
        userRepo.save(user);
        return user;
    }

    @Test
    void testGetOwnership() {
        // Given - создаем пользователя, функцию и связь
        UserEntity user = createTestUser("testuser", 1, "test@example.com");
        FunctionEntity function = createTestFunction("x^2", 1);
        FunctionOwnershipRequest request = new FunctionOwnershipRequest(user.getUserId(), function.getFuncId(), "My Function");
        ownershipService.create(request);

        // When
        OwnedFunctionResponse response = ownershipService.getOwnership(user.getUserId(), function.getFuncId());

        // Then
        assertNotNull(response);
        assertEquals(user.getUserId(), response.ownership.getUserId());
        assertEquals(function.getFuncId(), response.ownership.getFunctionId());
        assertEquals("My Function", response.ownership.getFuncName());
    }

    @Test
    void testDeleteOwnership() {
        // Given - создаем пользователя, функцию и связь
        UserEntity user = createTestUser("testuser", 1, "test@example.com");
        FunctionEntity function = createTestFunction("x^2", 1);
        FunctionOwnershipRequest request = new FunctionOwnershipRequest(user.getUserId(), function.getFuncId(), "My Function");
        ownershipService.create(request);

        // When
        ownershipService.deleteOwnership(user.getUserId(), function.getFuncId());

        // Then - проверяем, что связь удалена
        CustomException exception = assertThrows(CustomException.class, () -> {
            ownershipService.getOwnership(user.getUserId(), function.getFuncId());
        });

        assertEquals("Function ownership not found for user: " + user.getUserId() + " and function: " + function.getFuncId(), exception.getMessage());
    }

    @Test
    void testGetAllOwnerships() {
        // Given - создаем несколько связей
        UserEntity user1 = createTestUser("user1", 1,"user1@example.com");
        UserEntity user2 = createTestUser("user2", 2,"user2@example.com");
        FunctionEntity function1 = createTestFunction("x^2", 1);
        FunctionEntity function2 = createTestFunction("sin(x)", 1);

        ownershipService.create(new FunctionOwnershipRequest(user1.getUserId(), function1.getFuncId(), "Function 1"));
        ownershipService.create(new FunctionOwnershipRequest(user2.getUserId(), function2.getFuncId(), "Function 2"));

        // When
        List<FunctionOwnershipEntity> ownerships = ownershipService.getAllOwnerships();

        // Then
        assertNotNull(ownerships);
        assertTrue(ownerships.size() >= 2);
    }

    @Test
    void testGetOwnershipsByUserId() {
        // Given - создаем пользователя и несколько функций для него
        UserEntity user = createTestUser("testuser", 1,"test@example.com");
        FunctionEntity function1 = createTestFunction("x^2", 1);
        FunctionEntity function2 = createTestFunction("sin(x)", 1);
        FunctionEntity function3 = createTestFunction("cos(x)", 1);

        ownershipService.create(new FunctionOwnershipRequest(user.getUserId(), function1.getFuncId(), "Square"));
        ownershipService.create(new FunctionOwnershipRequest(user.getUserId(), function2.getFuncId(), "Sine"));
        ownershipService.create(new FunctionOwnershipRequest(user.getUserId(), function3.getFuncId(), "Cosine"));

        // When
        List<OwnedFunctionResponse> ownerships = ownershipService.getOwnershipsByUserId(user.getUserId());

        // Then
        assertNotNull(ownerships);
        assertEquals(3, ownerships.size());

        // Проверяем, что все связи принадлежат правильному пользователю
        for (OwnedFunctionResponse ownership : ownerships) {
            assertEquals(user.getUserId(), ownership.ownership.getUserId());
        }
    }
}