package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.utility.TestHibernateSessionFactoryUtil;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest extends BaseRepositoryTest{
    private SessionFactory factory;
    private UserRepository repository;

    @Test

    void testSaveAndFindById() {
        factory = TestHibernateSessionFactoryUtil.getSessionFactory();
        repository = new UserRepository(factory);
        UserEntity user = new UserEntity(1, 1, "Polina", "12345", new Date());
        repository.save(user);

        UserEntity found = repository.findById(1);

        assertNotNull(found, "Пользователь должен быть найден после сохранения");
        assertEquals("Polina", found.getUserName());
        assertEquals(1, found.getTypeId());
    }


    @Test

    void testFindAll() {
        factory = TestHibernateSessionFactoryUtil.getSessionFactory();
        repository = new UserRepository(factory);
        UserEntity u1 = new UserEntity(1, 1, "Pola", "pass1");
        UserEntity u2 = new UserEntity(2, 2, "Ameba", "pass2");
        repository.save(u1);
        repository.save(u2);

        List<UserEntity> users = repository.findAll();

        assertEquals(2, users.size(), "Должно быть два пользователя в базе");
        assertTrue(users.stream().anyMatch(u -> u.getUserName().equals("Pola")));
        assertTrue(users.stream().anyMatch(u -> u.getUserName().equals("Ameba")));
    }


    @Test

    void testUpdateUser() {
        factory = TestHibernateSessionFactoryUtil.getSessionFactory();
        repository = new UserRepository(factory);
        UserEntity user = new UserEntity(1, 2, "OldName", "oldpass");
        repository.save(user);

        user.setUserName("NewName");
        user.setPassword("newpass");

        UserEntity updated = repository.update(user);
        assertNotNull(updated);
        assertEquals("NewName", updated.getUserName());
        assertEquals("newpass", updated.getPassword());

        // Проверим через новый сеанс, чтобы убедиться, что реально обновилось в БД
        UserEntity fromDb = repository.findById(1);
        assertEquals("NewName", fromDb.getUserName());
    }


    @Test

    void testDeleteById() {
        factory = TestHibernateSessionFactoryUtil.getSessionFactory();
        repository = new UserRepository(factory);
        UserEntity user = new UserEntity(1, 1, "DeleteMe", "secret");
        repository.save(user);

        repository.deleteById(1);

        UserEntity found = repository.findById(1);
        assertNull(found, "Пользователь должен быть удалён");
    }

    @Test
    void testFindByType() {
        factory = TestHibernateSessionFactoryUtil.getSessionFactory();
        repository = new UserRepository(factory);

        UserEntity user1 = new UserEntity(1, 1, "Alice", "pass1");
        UserEntity user2 = new UserEntity(2, 1, "Bob", "pass2");
        UserEntity user3 = new UserEntity(3, 2, "Charlie", "pass3");

        repository.save(user1);
        repository.save(user2);
        repository.save(user3);

        List<UserEntity> type1Users = repository.findByType(1);
        assertEquals(2, type1Users.size(), "Должно быть найдено 2 пользователя с typeId = 1");

        assertTrue(type1Users.stream().anyMatch(u -> u.getUserName().equals("Alice")));
        assertTrue(type1Users.stream().anyMatch(u -> u.getUserName().equals("Bob")));
        assertFalse(type1Users.stream().anyMatch(u -> u.getUserName().equals("Charlie")));

        List<UserEntity> type300Users = repository.findByType(300);
        assertTrue(type300Users.isEmpty(), "Не должно быть пользователей с typeId = 300");
    }
    }


