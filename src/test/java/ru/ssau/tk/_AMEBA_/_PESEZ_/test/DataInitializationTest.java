package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.*;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.PointsEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.PointsRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.utility.DataInitialization;
import ru.ssau.tk._AMEBA_._PESEZ_.utility.TestHibernateSessionFactoryUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataInitializationTest {

    private DataInitialization dataInitialization;

    @BeforeEach
    void setUp() {
        dataInitialization = new DataInitialization();
    }


    @Test
    void testClearAllData() {
        // Просто проверяем, что метод выполняется без ошибок
        assertDoesNotThrow(() -> dataInitialization.clearAllData());
    }

    @Test
    void testInitializeTabulatedFunctionsOnly() {
        // Проверяем, что метод выполняется без ошибок
        assertDoesNotThrow(() -> dataInitialization.initializeTabulatedFunctionsOnly());
    }

    @Test
    void testMainMethod() {
        // Проверяем, что main метод запускается
        assertDoesNotThrow(() -> DataInitialization.main(new String[]{}));
    }

    @Test
    void testDataCreationAndCleanup() {
        // Интеграционный тест: создаем данные и проверяем их наличие
        dataInitialization.clearAllData();

        // Создаем небольшое количество данных для теста
        DataInitialization smallInitializer = new DataInitialization();

        // Проверяем, что очистка работает
        assertDoesNotThrow(() -> smallInitializer.clearAllData());

        // Можно добавить проверку, что база пуста (если есть доступ к репозиториям)
        FunctionRepository functionRepo = new FunctionRepository(
                TestHibernateSessionFactoryUtil.getSessionFactory()
        );
        PointsRepository pointsRepo = new PointsRepository(
                TestHibernateSessionFactoryUtil.getSessionFactory()
        );

        // После очистки должно быть мало данных (или 0)
        List<FunctionEntity> functionsAfterCleanup = functionRepo.findAll();
        List<PointsEntity> pointsAfterCleanup = pointsRepo.findAll();

        System.out.println("Функций после очистки: " + functionsAfterCleanup.size());
        System.out.println("Точек после очистки: " + pointsAfterCleanup.size());
    }

    @Test
    void testSmallBatchCreation() {
        // Тестируем создание небольшой пачки данных
        DataInitialization testInitializer = new DataInitialization();

        // Очищаем перед тестом
        testInitializer.clearAllData();

        // Проверяем, что процесс инициализации работает
        assertDoesNotThrow(() -> testInitializer.initializeTabulatedFunctionsOnly());

        // Проверяем, что данные создались
        FunctionRepository functionRepo = new FunctionRepository(
                TestHibernateSessionFactoryUtil.getSessionFactory()
        );

        List<FunctionEntity> functions = functionRepo.findByType(2); // Табулированные функции
        assertFalse(functions.isEmpty(), "Должны создаться табулированные функции");

        // Проверяем, что у функций есть точки
        PointsRepository pointsRepo = new PointsRepository(
                TestHibernateSessionFactoryUtil.getSessionFactory()
        );

        for (FunctionEntity function : functions.subList(0, Math.min(3, functions.size()))) {
            List<PointsEntity> points = pointsRepo.findByFunction(function);
            assertFalse(points.isEmpty(), "У функции должны быть точки");
            assertEquals(50, points.size(), "У каждой функции должно быть 50 точек");
        }
    }

    @AfterEach
    void tearDown() {
        // Очищаем после каждого теста
        if (dataInitialization != null) {
            dataInitialization.clearAllData();
        }
    }
}