package ru.ssau.tk._AMEBA_._PESEZ_.utility;

import org.hibernate.SessionFactory;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.*;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.*;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
        this.factory = HibernateSessionFactoryUtil.getSessionFactory();
        this.userRepository = new UserRepository(factory);
        this.pointsRepository = new PointsRepository(factory);
        this.functionRepository = new FunctionRepository(factory);
        this.compositeFunctionRepository = new CompositeFunctionRepository(factory);
        this.ownershipRepository = new FunctionOwnershipRepository(factory);
    }
    public void initializeAllData() {
        System.out.println("Начало заполнения базы данных...");
        long startTime = System.currentTimeMillis();
        try {
            // 1. Создаем пользователей
            List<UserEntity> users = createUsers();

            // 2. Создаем функции разных типов
            List<FunctionEntity> mathFunctions = createMathFunctions();
            List<FunctionEntity> tabulatedFunctions = createTabulatedFunctions();
            List<FunctionEntity> allFunctions = new ArrayList<>();
            allFunctions.addAll(mathFunctions);
            allFunctions.addAll(tabulatedFunctions);

            // 3. Создаем точки для табулированных функций
            createPointsForTabulatedFunctions(tabulatedFunctions);

            // 4. Создаем композитные функции
            List<CompositeFunctionEntity> compositeFunctions = createCompositeFunctions(mathFunctions);

            // 5. Создаем связи владения
            createFunctionOwnerships(users, allFunctions, compositeFunctions);

            long totalEndTime = System.currentTimeMillis();
            long totalTime = totalEndTime - startTime;

            System.out.println("База данных успешно заполнена!");
            displayStatistics(users.size(), allFunctions.size(), compositeFunctions.size(), totalTime);

        } catch (Exception e) {
            System.err.println("Ошибка при заполнении базы данных: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<UserEntity> createUsers() {
        System.out.println("👥 Создание пользователей...");
        List<UserEntity> users = new ArrayList<>();
        int counter = 0;

        for (int i = 0; i < TOTAL_RECORDS; i++) {
            UserEntity user = new UserEntity();
            user.setUserId(i);
            user.setTypeId(i % 2 == 0 ? 1 : 2);
            user.setUserName("user_" + i);
            user.setPassword("pass_" + i);
            user.setCreatedDate(new Date());

            users.add(user);
            counter++;

            // Сохраняем пачками для оптимизации
            if (users.size() % BATCH_SIZE == 0) {
                saveUsersBatch(users);
                System.out.println("Создано пользователей: " + counter);
                users.clear();
            }
        }

        // Сохраняем оставшихся
        if (!users.isEmpty()) {
            saveUsersBatch(users);
        }

        return userRepository.findAll();
    }

    private void saveUsersBatch(List<UserEntity> users) {
        for (UserEntity user : users) {
            userRepository.save(user);
        }
    }

    private List<FunctionEntity> createMathFunctions() {
        System.out.println("Создание математических функций...");
        List<FunctionEntity> functions = new ArrayList<>();
        String[] mathExpressions = {
                "x", "x^2", "x^3", "sin(x)", "cos(x)", "tan(x)",
                "log(x+1)", "exp(x)", "sqrt(x)", "1/x"
        };

        for (int i = 0; i < TOTAL_RECORDS / 2; i++) {
            FunctionEntity function = new FunctionEntity();
            function.setFuncId(i);
            function.setTypeId(1);
            function.setExpression(mathExpressions[i % mathExpressions.length] + " + " + (i % 10));

            functions.add(function);

            if (functions.size() % BATCH_SIZE == 0) {
                saveFunctionsBatch(functions);
                System.out.println("Создано математических функций: " + (i + 1));
                functions.clear();
            }
        }

        if (!functions.isEmpty()) {
            saveFunctionsBatch(functions);
        }

        return functionRepository.findByType(1);
    }

    private List<FunctionEntity> createTabulatedFunctions() {
        System.out.println("Создание табулированных функций...");
        List<FunctionEntity> functions = new ArrayList<>();

        for (int i = 0; i < TOTAL_RECORDS / 2; i++) {
            FunctionEntity function = new FunctionEntity();
            function.setFuncId(i+TOTAL_RECORDS / 2);
            function.setTypeId(2);
            function.setExpression("tabulated_" + i);

            functions.add(function);

            if (functions.size() % BATCH_SIZE == 0) {
                saveFunctionsBatch(functions);
                System.out.println("Создано табулированных функций: " + (i + 1));
                functions.clear();
            }
        }

        if (!functions.isEmpty()) {
            saveFunctionsBatch(functions);
        }

        return functionRepository.findByType(2);
    }

    private void saveFunctionsBatch(List<FunctionEntity> functions) {
        for (FunctionEntity function : functions) {
            functionRepository.save(function);
        }
    }
    private void createPointsForTabulatedFunctions(List<FunctionEntity> tabulatedFunctions) {
        System.out.println("Создание точек для табулированных функций...");
        int totalPoints = 0;

        for (FunctionEntity function : tabulatedFunctions) {
            List<PointsEntity> pointsBatch = new ArrayList<>();

            // Создаем 20 точек для каждой табулированной функции
            for (int j = 0; j < 20; j++) {
                double x = j * 0.5;
                double y = x*0.2;

                PointsEntity point = new PointsEntity();
                point.setFunction(function);
                point.set_xValue(x);
                point.set_yValue(y);

                pointsBatch.add(point);
                totalPoints++;

                if (pointsBatch.size() % BATCH_SIZE == 0) {
                    savePointsBatch(pointsBatch);
                    pointsBatch.clear();
                }
            }

            if (!pointsBatch.isEmpty()) {
                savePointsBatch(pointsBatch);
            }

            if (totalPoints % 1000 == 0) {
                System.out.println("Создано точек: " + totalPoints);
            }
        }

        System.out.println("Всего создано точек: " + totalPoints);
    }

    private void savePointsBatch(List<PointsEntity> points) {
        for (PointsEntity point : points) {
            pointsRepository.save(point);
        }
    }
    private List<CompositeFunctionEntity> createCompositeFunctions(List<FunctionEntity> mathFunctions) {
        System.out.println("Создание композитных функций...");
        List<CompositeFunctionEntity> composites = new ArrayList<>();

        // Создаем дополнительные функции для композиций
        List<FunctionEntity> compositeBaseFunctions = createCompositeBaseFunctions();

        for (int i = 0; i < TOTAL_RECORDS / 10; i++) {
            if (i >= compositeBaseFunctions.size() || i + 1 >= mathFunctions.size()) break;

            FunctionEntity innerFunc = mathFunctions.get(i);
            FunctionEntity outerFunc = mathFunctions.get(i + 1);
            FunctionEntity compositeFunc = compositeBaseFunctions.get(i % compositeBaseFunctions.size());

            CompositeFunctionEntity composite = new CompositeFunctionEntity();
            composite.setCompositeFunction(compositeFunc);
            composite.setInnerFunction(innerFunc);
            composite.setOuterFunction(outerFunc);

            composites.add(composite);

            if (composites.size() % BATCH_SIZE == 0) {
                saveCompositesBatch(composites);
                System.out.println("Создано композитных функций: " + composites.size());
                composites.clear();
            }
        }

        if (!composites.isEmpty()) {
            saveCompositesBatch(composites);
        }

        return compositeFunctionRepository.findAll();
    }
    private List<FunctionEntity> createCompositeBaseFunctions() {
        List<FunctionEntity> functions = new ArrayList<>();
        for (int i = 0; i < TOTAL_RECORDS / 10; i++) {
            FunctionEntity function = new FunctionEntity();
            function.setFuncId(i+TOTAL_RECORDS);
            function.setTypeId(3);
            function.setExpression("composite_" + i);
            functions.add(function);

            if (functions.size() % BATCH_SIZE == 0) {
                saveFunctionsBatch(functions);
                functions.clear();
            }
        }

        if (!functions.isEmpty()) {
            saveFunctionsBatch(functions);
        }

        return functionRepository.findByType(3);
    }

    private void saveCompositesBatch(List<CompositeFunctionEntity> composites) {
        for (CompositeFunctionEntity composite : composites) {
            compositeFunctionRepository.save(composite);
        }
    }

    private void createFunctionOwnerships(List<UserEntity> users,
                                          List<FunctionEntity> functions,
                                          List<CompositeFunctionEntity> compositeFunctions) {
        System.out.println("Создание связей владения...");

        // Собираем все функции включая композитные
        List<FunctionEntity> allFunctions = new ArrayList<>(functions);
        for (CompositeFunctionEntity composite : compositeFunctions) {
            allFunctions.add(composite.getCompositeFunction());
        }

        int ownershipCount = 0;

        for (int i = 0; i < Math.min(users.size(), allFunctions.size()); i++) {
            UserEntity user = users.get(i);
            FunctionEntity function = allFunctions.get(i);

            FunctionOwnershipEntity ownership = new FunctionOwnershipEntity();
            FunctionOwnershipId ownershipId = new FunctionOwnershipId();
            ownershipId.setUserId(user.getUserId());
            ownershipId.setFuncId(function.getFuncId());

            ownership.setId(ownershipId);
            ownership.setUser(user);
            ownership.setFunction(function);
            ownership.setFuncName("Description " );
            ownership.setCreatedDate(new Date());

            ownershipRepository.save(ownership);
            ownershipCount++;

            if (ownershipCount % BATCH_SIZE == 0) {
                System.out.println("Создано связей владения: " + ownershipCount);
            }

            // Останавливаемся когда достигли 10K
            if (ownershipCount >= TOTAL_RECORDS) {
                break;
            }
        }

}

    private void displayStatistics(int userCount, int functionCount, int compositeCount,long totalTime) {
        System.out.println("\nСТАТИСТИКА ЗАПОЛНЕНИЯ:");
        System.out.println("==========================");
        System.out.println("Пользователей: " + userCount);
        System.out.println("Функций: " + functionCount);
        System.out.println("Табулированных функций: " + functionRepository.findByType(2).size());
        System.out.println("Композитных функций: " + compositeCount);
        System.out.println("Связей владения: " + ownershipRepository.findAll().size());
        System.out.println("Время заполнения: " + formatTime(totalTime));
        System.out.println("==========================");
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
        try {
            // Очищаем в правильном порядке из-за foreign key constraints
            ownershipRepository.findAll().forEach(ownership -> {
                ownershipRepository.deleteById(
                        ownership.getId().getUserId(),
                        ownership.getId().getFuncId()
                );
            });

            pointsRepository.findAll().forEach(point -> {
                pointsRepository.deleteById(point.getFunction().getFuncId(), point.get_xValue());
            });

            compositeFunctionRepository.findAll().forEach(composite -> {
                compositeFunctionRepository.deleteById(composite.getCompositeFunction().getFuncId());
            });

            functionRepository.findAll().forEach(function -> {
                functionRepository.deleteById(function.getFuncId());
            });

            userRepository.findAll().forEach(user -> {
                userRepository.deleteById(user.getUserId());
            });
            long endTime = System.currentTimeMillis();
            System.out.println("База данных очищена!");
            System.out.println("Время очистки: " + formatTime(endTime - startTime));

        } catch (Exception e) {
            System.err.println("Ошибка при очистке базы данных: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        DataInitialization initializer = new DataInitialization();

        long totalStartTime = System.currentTimeMillis();

        //initializer.clearAllData();
        initializer.initializeAllData();

        long totalEndTime = System.currentTimeMillis();
        long totalProcessTime = totalEndTime - totalStartTime;
        System.out.println("Полное время процесса: " + initializer.formatTime(totalProcessTime));


    }
}



