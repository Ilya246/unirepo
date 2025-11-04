package ru.ssau.tk._AMEBA_._PESEZ_.utility;

import org.hibernate.SessionFactory;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.*;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.*;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.Log;

public class DataInitialization {

    private final SessionFactory factory;
    private final UserRepository userRepository;
    private final PointsRepository pointsRepository;
    private final FunctionRepository functionRepository;
    private final CompositeFunctionRepository compositeFunctionRepository;
    private final FunctionOwnershipRepository ownershipRepository;

    // Константы для количества записей
    private static final int TOTAL_RECORDS = 10000;
    private static final int BATCH_SIZE = 100;

    public DataInitialization() {
        this.factory = TestHibernateSessionFactoryUtil.getSessionFactory();
        this.userRepository = new UserRepository(factory);
        this.pointsRepository = new PointsRepository(factory);
        this.functionRepository = new FunctionRepository(factory);
        this.compositeFunctionRepository = new CompositeFunctionRepository(factory);
        this.ownershipRepository = new FunctionOwnershipRepository(factory);
    }

    private final AtomicInteger idCounter = new AtomicInteger(1000000);

    private int generateUniqueId() {
        return idCounter.getAndIncrement();
    }
    private void createTabulatedFunctionBatch(int batchSize, int pointCount) {
        try {
            for (int i = 0; i < batchSize; i++) {
                // 1. Создаем функцию
                FunctionEntity function = new FunctionEntity();
                function.setFuncId(generateUniqueId());
                function.setTypeId(2);
                function.setExpression("fast_tabulated_" + System.currentTimeMillis() + "_" + i);
                functionRepository.save(function);

                // 2. Создаем все точки для функции сразу
                double currentX = -Math.random() * pointCount / 4;
                List<PointsEntity> pointsBatch = new ArrayList<>();

                for (int j = 0; j < pointCount; j++) {
                    currentX += Math.max(Math.random(), 0.001);
                    double yValue = Math.random() * 10 - 5;

                    PointsEntity point = new PointsEntity();
                    point.setFunction(function);
                    point.set_xValue(currentX);
                    point.set_yValue(yValue);
                    pointsBatch.add(point);
                }

                // Сохраняем точки пачкой
                savePointsBatch(pointsBatch);

                // Каждые 20 функций выводим прогресс
                if (i % 20 == 0) {
                    System.out.println("Создано функций: " + (i + 1));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void savePointsBatch(List<PointsEntity> points) {
        pointsRepository.saveAll(points);
    }




    private String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        if (hours > 0) {
            return String.format("%d ч %d мин %d сек %d мс",
                    hours, minutes % 60, seconds % 60, milliseconds % 1000);
        } else if (minutes > 0) {
            return String.format("%d мин %d сек %d мс",
                    minutes, seconds % 60, milliseconds % 1000);
        } else if (seconds > 0) {
            return String.format("%d сек %d мс", seconds, milliseconds % 1000);
        } else {
            return String.format("%d мс", milliseconds);
        }
    }

    public void clearAllData() {
        System.out.println("🧹 Очистка базы данных...");
        long startTime = System.currentTimeMillis();
        try (var session = factory.openSession()) {
            var transaction = session.beginTransaction();

            try {
                // Удаляем в правильном порядке из-за foreign key constraints
                session.createQuery("DELETE FROM PointsEntity").executeUpdate();
                session.createQuery("DELETE FROM CompositeFunctionEntity").executeUpdate();
                session.createQuery("DELETE FROM FunctionOwnershipEntity").executeUpdate();
                session.createQuery("DELETE FROM FunctionEntity").executeUpdate();
                session.createQuery("DELETE FROM UserEntity").executeUpdate();

                transaction.commit();
                long endTime = System.currentTimeMillis();
                System.out.println("База данных очищена!");
                System.out.println("Время очистки: " + formatTime(endTime - startTime));
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                Log.warn("Warning during database cleanup: " + e.getMessage());
            }
        }
    }

    public void initializeTabulatedFunctionsOnly() {
        System.out.println("🚀 Быстрое заполнение табулированных функций...");
        long startTime = System.currentTimeMillis();

        int functionCount = 5000;
        int pointCount = 50;
        int batchSize = 100;

        ExecutorService executor = Executors.newFixedThreadPool(8); // Параллелизм

        try {
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (int batchStart = 0; batchStart < functionCount; batchStart += batchSize) {
                int currentBatchSize = Math.min(batchSize, functionCount - batchStart);

                CompletableFuture<Void> batchFuture = CompletableFuture.runAsync(() -> {
                    // Создаем функции и точки пачками в отдельной транзакции
                    createTabulatedFunctionBatch(currentBatchSize, pointCount);
                }, executor);

                futures.add(batchFuture);
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();

            long totalTime = System.currentTimeMillis() - startTime;
            long totalPoints = (long) functionCount * pointCount;
            double recordsPerSecond = (double) (functionCount) / (totalTime / 1000.0);
            System.out.println("Создано функций: " + functionCount);
            System.out.println("Создано точек: " + totalPoints);
            System.out.println("⚡ Время: " + formatTime(totalTime));
            System.out.printf("Скорость: %.2f записей/сек%n", recordsPerSecond);
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    public static void main(String[] args) {
        DataInitialization initializer = new DataInitialization();

        long totalStartTime = System.currentTimeMillis();

        initializer.clearAllData();
        //initializer.initializeTabulatedFunctionsOnly();

        long totalEndTime = System.currentTimeMillis();
        long totalProcessTime = totalEndTime - totalStartTime;
        System.out.println("Полное время процесса: " + initializer.formatTime(totalProcessTime));


    }
}