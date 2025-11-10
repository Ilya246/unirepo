package ru.ssau.tk._AMEBA_._PESEZ_.test.repserver;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionOwnershipEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionOwnershipRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.utility.TestHibernateSessionFactoryUtil;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FunctionOwnershipRepositoryTest extends BaseRepositoryTest {

    private SessionFactory factory;
    private FunctionOwnershipRepository ownershipRepository;
    private UserRepository userRepository;
    private FunctionRepository functionRepository;

    @BeforeEach
    void setUp() {
        factory = TestHibernateSessionFactoryUtil.getSessionFactory();
        ownershipRepository = new FunctionOwnershipRepository(factory);
        userRepository = new UserRepository(factory);
        functionRepository = new FunctionRepository(factory);
    }

    @Test
    void testSaveAndFindById() {
        UserEntity user = new UserEntity(1, "TestUser", "password");
        FunctionEntity function = new FunctionEntity(1, "x^2");

        userRepository.save(user);
        functionRepository.save(function);

        FunctionOwnershipEntity ownership = new FunctionOwnershipEntity(
                user, function, new Date(), "My Quadratic Function"
        );
        ownershipRepository.save(ownership);

        Optional<FunctionOwnershipEntity> found = ownershipRepository.findById(user.getUserId(), function.getFuncId());

        assertTrue(found.isPresent(), "Связь владения должна быть найдена");
        assertEquals("My Quadratic Function", found.get().getFuncName());
        assertEquals("TestUser", found.get().getUser().getUserName());
        assertEquals("x^2", found.get().getFunction().getExpression());
    }

    @Test
    void testFindAll() {
        UserEntity user1 = new UserEntity(1, "User1", "pass1");
        UserEntity user2 = new UserEntity(1, "User2", "pass2");
        userRepository.save(user1);
        userRepository.save(user2);

        FunctionEntity func1 = new FunctionEntity(1, "sin(x)");
        FunctionEntity func2 = new FunctionEntity(2, "cos(x)");
        FunctionEntity func3 = new FunctionEntity(3, "x^3");
        functionRepository.save(func1);
        functionRepository.save(func2);
        functionRepository.save(func3);

        FunctionOwnershipEntity ownership1 = new FunctionOwnershipEntity(
                user1, func1, new Date(), "Sine Function"
        );
        FunctionOwnershipEntity ownership2 = new FunctionOwnershipEntity(
                user1, func2, new Date(), "Cosine Function"
        );
        FunctionOwnershipEntity ownership3 = new FunctionOwnershipEntity(
                user2, func3, new Date(), "Cubic Function"
        );

        ownershipRepository.save(ownership1);
        ownershipRepository.save(ownership2);
        ownershipRepository.save(ownership3);

        List<FunctionOwnershipEntity> allOwnerships = ownershipRepository.findAll();

        assertEquals(3, allOwnerships.size(), "Должно быть три связи владения");
        assertTrue(allOwnerships.stream().anyMatch(o -> o.getFuncName().equals("Sine Function")));
        assertTrue(allOwnerships.stream().anyMatch(o -> o.getFuncName().equals("Cosine Function")));
        assertTrue(allOwnerships.stream().anyMatch(o -> o.getFuncName().equals("Cubic Function")));
    }

    @Test
    void testDeleteById() {
        UserEntity user = new UserEntity(1, "DeleteUser", "pass");
        FunctionEntity function = new FunctionEntity(1, "x + 1");

        userRepository.save(user);
        functionRepository.save(function);

        FunctionOwnershipEntity ownership = new FunctionOwnershipEntity(
                user, function, new Date(), "To Delete"
        );
        ownershipRepository.save(ownership);

        Optional<FunctionOwnershipEntity> foundBefore = ownershipRepository.findById(user.getUserId(), function.getFuncId());
        assertTrue(foundBefore.isPresent(), "Связь должна существовать до удаления");

        ownershipRepository.deleteById(user.getUserId(), function.getFuncId());

        Optional<FunctionOwnershipEntity> foundAfter = ownershipRepository.findById(user.getUserId(), function.getFuncId());
        assertFalse(foundAfter.isPresent(), "Связь должна быть удалена");
    }

    @Test
    void testUpdateById() {
        UserEntity user = new UserEntity(1, "UpdateUser", "pass");
        FunctionEntity function = new FunctionEntity(1, "x^2");

        userRepository.save(user);
        functionRepository.save(function);

        FunctionOwnershipEntity ownership = new FunctionOwnershipEntity(
                user, function, new Date(), "Old Name"
        );
        ownershipRepository.save(ownership);

        ownershipRepository.updateById(user.getUserId(), function.getFuncId(), "New Updated Name");

        Optional<FunctionOwnershipEntity> updated = ownershipRepository.findById(user.getUserId(), function.getFuncId());
        assertTrue(updated.isPresent(), "Связь должна существовать после обновления");
        assertEquals("New Updated Name", updated.get().getFuncName(), "Имя функции должно быть обновлено");
    }

    @Test
    void testFindByUserId() {
        UserEntity user1 = new UserEntity(1, "User1", "pass1");
        UserEntity user2 = new UserEntity(1, "User2", "pass2");
        userRepository.save(user1);
        userRepository.save(user2);

        FunctionEntity func1 = new FunctionEntity(1, "x^2");
        FunctionEntity func2 = new FunctionEntity(2, "sin(x)");
        FunctionEntity func3 = new FunctionEntity(3, "cos(x)");
        functionRepository.save(func1);
        functionRepository.save(func2);
        functionRepository.save(func3);

        FunctionOwnershipEntity ownership1 = new FunctionOwnershipEntity(
                user1, func1, new Date(), "Quadratic"
        );
        FunctionOwnershipEntity ownership2 = new FunctionOwnershipEntity(
                user1, func2, new Date(), "Sine"
        );
        FunctionOwnershipEntity ownership3 = new FunctionOwnershipEntity(
                user2, func3, new Date(), "Cosine"
        );

        ownershipRepository.save(ownership1);
        ownershipRepository.save(ownership2);
        ownershipRepository.save(ownership3);

        // Тестируем поиск по userId = 1
        List<FunctionOwnershipEntity> user1Ownerships = ownershipRepository.findByUserId(user1.getUserId());
        assertEquals(2, user1Ownerships.size(), "У пользователя 1 должно быть 2 функции");
        assertTrue(user1Ownerships.stream().anyMatch(o -> o.getFuncName().equals("Quadratic")));
        assertTrue(user1Ownerships.stream().anyMatch(o -> o.getFuncName().equals("Sine")));

        // Тестируем поиск по userId = 2
        List<FunctionOwnershipEntity> user2Ownerships = ownershipRepository.findByUserId(user2.getUserId());
        assertEquals(1, user2Ownerships.size(), "У пользователя 2 должна быть 1 функция");
        assertEquals("Cosine", user2Ownerships.get(0).getFuncName());


    }

    @Test
    void testFindOwnerByFunctionId() {
        UserEntity user = new UserEntity( 1, "FunctionOwner", "password");
        FunctionEntity function = new FunctionEntity(1, "x^3");

        userRepository.save(user);
        functionRepository.save(function);

        FunctionOwnershipEntity ownership = new FunctionOwnershipEntity(
                user, function, new Date(), "Cubic Function"
        );
        ownershipRepository.save(ownership);

        // Тестируем поиск владельца по functionId
        Optional<UserEntity> owner = ownershipRepository.findOwnerByFunctionId(function.getFuncId());
        assertTrue(owner.isPresent(), "Владелец должен быть найден");
        assertEquals("FunctionOwner", owner.get().getUserName());
        assertEquals(1, owner.get().getUserId());

    }

    @Test
    void testFindUserFunctionsOrderByDate() {
        UserEntity user = new UserEntity(1, "TestUser", "password");
        userRepository.save(user);

        FunctionEntity func1 = new FunctionEntity(1, "x^2");
        FunctionEntity func2 = new FunctionEntity(2, "sin(x)");
        FunctionEntity func3 = new FunctionEntity(3, "cos(x)");
        functionRepository.save(func1);
        functionRepository.save(func2);
        functionRepository.save(func3);

        // Создаем ownership с разными датами
        Date oldestDate = new Date(System.currentTimeMillis() - 30000); // 30 секунд назад
        Date middleDate = new Date(System.currentTimeMillis() - 20000); // 20 секунд назад
        Date newestDate = new Date(System.currentTimeMillis() - 10000); // 10 секунд назад

        FunctionOwnershipEntity ownership1 = new FunctionOwnershipEntity(user, func1, oldestDate, "Oldest");
        FunctionOwnershipEntity ownership2 = new FunctionOwnershipEntity(user, func2, middleDate, "Middle");
        FunctionOwnershipEntity ownership3 = new FunctionOwnershipEntity(user, func3, newestDate, "Newest");

        ownershipRepository.save(ownership1);
        ownershipRepository.save(ownership2);
        ownershipRepository.save(ownership3);

        // Тестируем сортировку по возрастанию (старые сначала)
        List<FunctionEntity> ascendingOrder = ownershipRepository.findUserFunctionsOrderByDate(user.getUserId(), false);
        assertEquals(3, ascendingOrder.size());
        assertEquals("x^2", ascendingOrder.get(0).getExpression()); // Самая старая
        assertEquals("sin(x)", ascendingOrder.get(1).getExpression());
        assertEquals("cos(x)", ascendingOrder.get(2).getExpression()); // Самая новая

        // Тестируем сортировку по убыванию (новые сначала)
        List<FunctionEntity> descendingOrder = ownershipRepository.findUserFunctionsOrderByDate(user.getUserId(), true);
        assertEquals(3, descendingOrder.size());
        assertEquals("cos(x)", descendingOrder.get(0).getExpression()); // Самая новая
        assertEquals("sin(x)", descendingOrder.get(1).getExpression());
        assertEquals("x^2", descendingOrder.get(2).getExpression()); // Самая старая

    }


}