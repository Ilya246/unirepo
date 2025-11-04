package ru.ssau.tk._AMEBA_._PESEZ_.test;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.Log;

class UserRepositoryTest extends BaseRepositoryTest {
    private SessionFactory factory;
    private UserRepository repository;
    private final AtomicInteger idGenerator = new AtomicInteger(1000);


    @BeforeEach
    void setUp() {
        factory = TestHibernateSessionFactoryUtil.getSessionFactory();
        repository = new UserRepository(factory);
        clearDatabase();
    }

    private void clearDatabase() {
        try (var session = factory.openSession()) {
            var transaction = session.beginTransaction();
            session.createNativeQuery("DELETE FROM function_ownership").executeUpdate();
            session.createNativeQuery("DELETE FROM users").executeUpdate();
            transaction.commit();
        }
    }

    @Test
    void testSaveAndFindById() {
        UserEntity user = new UserEntity(1, 1, "Polina", "12345", new Date());
        repository.save(user);

        UserEntity found = repository.findById(1);

        assertNotNull(found, "Пользователь должен быть найден после сохранения");
        assertEquals("Polina", found.getUserName());
        assertEquals(1, found.getTypeId());
    }

    @Test
    void testFindAll() {
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
        UserEntity user = new UserEntity(1, 1, "DeleteMe", "secret");
        repository.save(user);

        repository.deleteById(1);

        UserEntity found = repository.findById(1);
        assertNull(found, "Пользователь должен быть удалён");
    }

    @Test
    void testFindByType() {
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

    @Test
    void testFindAllOrderByCreatedDateAscending() {
        // Создаем пользователей с разными датами
        Date oldestDate = new Date(System.currentTimeMillis() - 30000); // 30 секунд назад
        Date middleDate = new Date(System.currentTimeMillis() - 20000); // 20 секунд назад
        Date newestDate = new Date(System.currentTimeMillis() - 10000); // 10 секунд назад

        UserEntity user1 = new UserEntity(1, 1, "OldestUser", "pass1", oldestDate);
        UserEntity user2 = new UserEntity(2, 1, "MiddleUser", "pass2", middleDate);
        UserEntity user3 = new UserEntity(3, 1, "NewestUser", "pass3", newestDate);

        repository.save(user1);
        repository.save(user2);
        repository.save(user3);

        // Тестируем сортировку по возрастанию (старые сначала)
        List<UserEntity> ascendingUsers = repository.findAllOrderByCreatedDate(false);

        assertEquals(3, ascendingUsers.size(), "Должно быть 3 пользователя");
        assertEquals("OldestUser", ascendingUsers.get(0).getUserName(), "Первый должен быть самый старый");
        assertEquals("MiddleUser", ascendingUsers.get(1).getUserName(), "Второй - средний");
        assertEquals("NewestUser", ascendingUsers.get(2).getUserName(), "Последний - самый новый");
    }

    @Test
    void testFindAllOrderByCreatedDateDescending() {
        // Создаем пользователей с разными датами
        Date oldestDate = new Date(System.currentTimeMillis() - 30000);
        Date middleDate = new Date(System.currentTimeMillis() - 20000);
        Date newestDate = new Date(System.currentTimeMillis() - 10000);

        UserEntity user1 = new UserEntity(1, 1, "OldestUser", "pass1", oldestDate);
        UserEntity user2 = new UserEntity(2, 1, "MiddleUser", "pass2", middleDate);
        UserEntity user3 = new UserEntity(3, 1, "NewestUser", "pass3", newestDate);

        repository.save(user1);
        repository.save(user2);
        repository.save(user3);

        // Тестируем сортировку по убыванию (новые сначала)
        List<UserEntity> descendingUsers = repository.findAllOrderByCreatedDate(true);

        assertEquals(3, descendingUsers.size(), "Должно быть 3 пользователя");
        assertEquals("NewestUser", descendingUsers.get(0).getUserName(), "Первый должен быть самый новый");
        assertEquals("MiddleUser", descendingUsers.get(1).getUserName(), "Второй - средний");
        assertEquals("OldestUser", descendingUsers.get(2).getUserName(), "Последний - самый старый");
    }

    @Test
    void testFindAllOrderByCreatedDateWithSameDates() {
        // Создаем пользователей с одинаковыми датами
        Date sameDate = new Date();
        UserEntity user1 = new UserEntity(1, 1, "User1", "pass1", sameDate);
        UserEntity user2 = new UserEntity(2, 1, "User2", "pass2", sameDate);
        UserEntity user3 = new UserEntity(3, 1, "User3", "pass3", sameDate);

        repository.save(user1);
        repository.save(user2);
        repository.save(user3);

        // При одинаковых датах порядок может быть любым, но список должен содержать всех пользователей
        List<UserEntity> users = repository.findAllOrderByCreatedDate(false);
        assertEquals(3, users.size(), "Должно быть 3 пользователя независимо от порядка");
        assertTrue(users.stream().anyMatch(u -> u.getUserName().equals("User1")));
        assertTrue(users.stream().anyMatch(u -> u.getUserName().equals("User2")));
        assertTrue(users.stream().anyMatch(u -> u.getUserName().equals("User3")));
    }

    @Test
    void testFindAllOrderByCreatedDateEmptyDatabase() {
        // Тестируем на пустой базе данных
        List<UserEntity> users = repository.findAllOrderByCreatedDate(true);
        assertTrue(users.isEmpty(), "Для пустой базы должен вернуться пустой список");
    }

    @Test
    void testUpdateNonExistentUser() {
        // Пытаемся обновить несуществующего пользователя
        UserEntity nonExistentUser = new UserEntity(999, 1, "NonExistent", "pass");

        // merge для несуществующего пользователя создаст новую запись
        UserEntity result = repository.update(nonExistentUser);

        assertNotNull(result, "Merge несуществующего пользователя должен создать новую запись");
        assertEquals("NonExistent", result.getUserName());

        // Проверяем, что пользователь действительно создан
        UserEntity fromDb = repository.findById(999);
        assertNotNull(fromDb, "Пользователь должен быть создан через merge");
    }

    @Test
    void testDeleteNonExistentUser() {
        // Удаление несуществующего пользователя не должно вызывать исключений
        assertDoesNotThrow(() -> repository.deleteById(999),
                "Удаление несуществующего пользователя не должно вызывать исключений");
    }

    @Test
    void testFindByIdNonExistent() {
        // Поиск несуществующего пользователя
        UserEntity user = repository.findById(999);
        assertNull(user, "Для несуществующего ID должен вернуться null");
    }


    @Test
    void testSaveUserWithEmptyStrings() {
        // Тестируем сохранение пользователя с пустыми строками
        UserEntity user = new UserEntity(1, 1, "", "", new Date());
        repository.save(user);

        UserEntity found = repository.findById(1);
        assertNotNull(found);
        assertEquals("", found.getUserName());
        assertEquals("", found.getPassword());
    }

    @Test
    void testMultipleUpdates() {
        // Тестируем множественные обновления одного пользователя
        UserEntity user = new UserEntity(1, 1, "InitialName", "InitialPass", new Date());
        repository.save(user);

        // Первое обновление
        user.setUserName("FirstUpdate");
        user.setPassword("FirstPass");
        UserEntity firstUpdate = repository.update(user);
        assertEquals("FirstUpdate", firstUpdate.getUserName());

        // Второе обновление
        user.setUserName("SecondUpdate");
        user.setPassword("SecondPass");
        UserEntity secondUpdate = repository.update(user);
        assertEquals("SecondUpdate", secondUpdate.getUserName());

        // Проверяем финальное состояние в базе
        UserEntity finalUser = repository.findById(1);
        assertEquals("SecondUpdate", finalUser.getUserName());
        assertEquals("SecondPass", finalUser.getPassword());
    }

    @Test
    void testUserTypeBoundaryValues() {
        // Тестируем граничные значения для typeId
        UserEntity minTypeUser = new UserEntity(1, 1, "MinTypeUser", "pass");
        UserEntity maxTypeUser = new UserEntity(2, 2, "MaxTypeUser", "pass");

        repository.save(minTypeUser);
        repository.save(maxTypeUser);

        // Проверяем, что пользователи сохранились и находятся по typeId
        List<UserEntity> minTypeUsers = repository.findByType(1);
        assertEquals(1, minTypeUsers.size());
        assertEquals("MinTypeUser", minTypeUsers.get(0).getUserName());

        List<UserEntity> maxTypeUsers = repository.findByType(2);
        assertEquals(1, maxTypeUsers.size());
        assertEquals("MaxTypeUser", maxTypeUsers.get(0).getUserName());

       }

    @Test
    void testFindAllWithLargeDataset() {
        // Тестируем с большим количеством пользователей
        int userCount = 50;
        for (int i = 1; i <= userCount; i++) {
            UserEntity user = new UserEntity(i, 1, "User" + i, "pass" + i, new Date());
            repository.save(user);
        }

        List<UserEntity> allUsers = repository.findAll();
        assertEquals(userCount, allUsers.size(), "Должно быть найдено " + userCount + " пользователей");

        // Проверяем сортировку с большим набором данных
        List<UserEntity> sortedUsers = repository.findAllOrderByCreatedDate(false);
        assertEquals(userCount, sortedUsers.size(), "Сортировка должна работать с большими наборами данных");
    }
    @Test
    void testSortUsersAsync() throws InterruptedException, ExecutionException {
        int startCount = 1000;
        int countDelta = 1000;
        int functionsCount = 5;
        int testAmount = 5;

        UserRepository userRepository = new UserRepository(factory);
        FunctionRepository functionRepository = new FunctionRepository(factory);
        FunctionOwnershipRepository ownershipRepository = new FunctionOwnershipRepository(factory);

        for (int count = startCount, it = 0; it < testAmount; count += countDelta, it++) {
            CompletableFuture<Void>[] userFutures = new CompletableFuture[count];
            List<UserEntity> users = Collections.synchronizedList(new ArrayList<>());

            long startTime = System.currentTimeMillis();

            // Асинхронное создание пользователей
            for (int i = 0; i < count; i++) {
                final int index = i;
                userFutures[i] = CompletableFuture.runAsync(() -> {
                    UserEntity user = new UserEntity();
                    user.setUserId(idGenerator.getAndIncrement());
                    user.setTypeId(1);
                    user.setUserName("SortUser" + index);
                    user.setPassword("SortPassword" + index);
                    user.setCreatedDate(new Date());

                    userRepository.save(user);
                    users.add(user);
                });
            }

            CompletableFuture.allOf(userFutures).get();

            // Асинхронное создание функций и связей владения
            int funcTotal = count * functionsCount;
            CompletableFuture<Void>[] functionFutures = new CompletableFuture[funcTotal];

            for (int i = 0; i < count; i++) {
                final UserEntity user = users.get(i);
                final String expr = Math.random() + "x+" + Math.random();

                for (int j = 0; j < functionsCount; j++) {
                    final int functionIndex = i * functionsCount + j;
                    functionFutures[functionIndex] = CompletableFuture.runAsync(() -> {
                        // Создаем функцию
                        FunctionEntity function = new FunctionEntity();
                        function.setFuncId(idGenerator.getAndIncrement());
                        function.setTypeId(1);
                        function.setExpression(expr);
                        functionRepository.save(function);

                        // Создаем связь владения через конструктор
                        FunctionOwnershipEntity ownership = new FunctionOwnershipEntity(
                                user,
                                function,
                                new Date(),
                                "Func_" + user.getUserId() + "_" + function.getFuncId()
                        );
                        ownershipRepository.save(ownership);
                    });
                }
            }

            CompletableFuture.allOf(functionFutures).get();

            float tookMillis = System.currentTimeMillis() - startTime;
            float tookSeconds = tookMillis / 1000f;

            Log.warn("Write of {} users + {} functions took: {}s ({}+{}/s)",
                    count, funcTotal, tookSeconds,
                    count / tookSeconds, funcTotal / tookSeconds);
        }

        List<UserEntity> allUsers = userRepository.findAll();
        long sortStartTime = System.nanoTime();

        List<UserEntity> sortedUsers = userRepository.findAllOrderByCreatedDate(true);

        float sortTime = (System.nanoTime() - sortStartTime) * 1e-9f;
        Log.warn("Took {}s to sort {} users by date", sortTime, allUsers.size());
    }
}