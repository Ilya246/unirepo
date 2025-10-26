package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.hibernate.SessionFactory;
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

class FunctionOwnershipRepositoryTest extends BaseRepositoryTest{
    private SessionFactory factory;
    private FunctionOwnershipRepository ownershipRepository;
    private UserRepository userRepository;
    private FunctionRepository functionRepository;
    @Test
    void testSaveAndFindById() {
        factory = TestHibernateSessionFactoryUtil.getSessionFactory();
        ownershipRepository = new FunctionOwnershipRepository(factory);
        userRepository = new UserRepository(factory);
        functionRepository = new FunctionRepository(factory);

        UserEntity user = new UserEntity(1, 1, "TestUser", "password");
        FunctionEntity function = new FunctionEntity(1, 1, "x^2");

        userRepository.save(user);
        functionRepository.save(function);

        FunctionOwnershipEntity ownership = new FunctionOwnershipEntity(
                user, function, new Date(), "My Quadratic Function"
        );
        ownershipRepository.save(ownership);

        Optional<FunctionOwnershipEntity> found = ownershipRepository.findById(1, 1);

        assertTrue(found.isPresent(), "Связь владения должна быть найдена");
        assertEquals("My Quadratic Function", found.get().getFuncName());
        assertEquals("TestUser", found.get().getUser().getUserName());
        assertEquals("x^2", found.get().getFunction().getExpression());
    }

    @Test
    void testFindAll() {
        factory = TestHibernateSessionFactoryUtil.getSessionFactory();
        ownershipRepository = new FunctionOwnershipRepository(factory);
        userRepository = new UserRepository(factory);
        functionRepository = new FunctionRepository(factory);

        UserEntity user1 = new UserEntity(1, 1, "User1", "pass1");
        UserEntity user2 = new UserEntity(2, 1, "User2", "pass2");
        userRepository.save(user1);
        userRepository.save(user2);

        FunctionEntity func1 = new FunctionEntity(1, 1, "sin(x)");
        FunctionEntity func2 = new FunctionEntity(2, 2, "cos(x)");
        FunctionEntity func3 = new FunctionEntity(3, 3, "x^3");
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
        factory = TestHibernateSessionFactoryUtil.getSessionFactory();
        ownershipRepository = new FunctionOwnershipRepository(factory);
        userRepository = new UserRepository(factory);
        functionRepository = new FunctionRepository(factory);

        UserEntity user = new UserEntity(1, 1, "DeleteUser", "pass");
        FunctionEntity function = new FunctionEntity(1, 1, "x + 1");

        userRepository.save(user);
        functionRepository.save(function);

        FunctionOwnershipEntity ownership = new FunctionOwnershipEntity(
                user, function, new Date(), "To Delete"
        );
        ownershipRepository.save(ownership);

        Optional<FunctionOwnershipEntity> foundBefore = ownershipRepository.findById(1, 1);
        assertTrue(foundBefore.isPresent(), "Связь должна существовать до удаления");

        ownershipRepository.deleteById(1, 1);

        Optional<FunctionOwnershipEntity> foundAfter = ownershipRepository.findById(1, 1);
        assertFalse(foundAfter.isPresent(), "Связь должна быть удалена");
    }

    @Test
    void testUpdateById() {
        factory = TestHibernateSessionFactoryUtil.getSessionFactory();
        ownershipRepository = new FunctionOwnershipRepository(factory);
        userRepository = new UserRepository(factory);
        functionRepository = new FunctionRepository(factory);

        UserEntity user = new UserEntity(1, 1, "UpdateUser", "pass");
        FunctionEntity function = new FunctionEntity(1, 1, "x^2");

        userRepository.save(user);
        functionRepository.save(function);

        FunctionOwnershipEntity ownership = new FunctionOwnershipEntity(
                user, function, new Date(), "Old Name"
        );
        ownershipRepository.save(ownership);

        ownershipRepository.updateById(1, 1, "New Updated Name");

        Optional<FunctionOwnershipEntity> updated = ownershipRepository.findById(1, 1);
        assertTrue(updated.isPresent(), "Связь должна существовать после обновления");
        assertEquals("New Updated Name", updated.get().getFuncName(), "Имя функции должно быть обновлено");
    }
}