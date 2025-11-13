package ru.ssau.tk._AMEBA_._PESEZ_.repository;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.CompositeFunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.PointsEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;
import ru.ssau.tk._AMEBA_._PESEZ_.operations.TabulatedFunctionOperationService;
import ru.ssau.tk._AMEBA_._PESEZ_.utility.HibernateSessionFactoryUtil;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class FunctionRepository {

    private final SessionFactory sessionFactory;
    private final PointsRepository pointsRepository;
    private final CompositeFunctionRepository compositeRepository;
    private final FunctionOwnershipRepository ownershipRepository;

    private static final int MATH_FUNCTION_ID = 1;
    private static final int TABULATED_ID = 2;
    private static final int COMPOSITE_ID = 3;
    private static final String PURE_TABULATED_EXPRESSION = "<TABULATED>";
  //  private final AtomicInteger idGenerator = new AtomicInteger(1000);
    public FunctionRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.pointsRepository = new PointsRepository(sessionFactory);
        this.compositeRepository = new CompositeFunctionRepository(sessionFactory);
        this.ownershipRepository = new FunctionOwnershipRepository(sessionFactory);
    }
    // Парсинг функции
    public static MathFunction parseFunction(String expression) {
        Expression expr = new ExpressionBuilder(expression).variable("x").build();
        return (double x) -> expr.setVariable("x", x).evaluate();
    }

    public FunctionEntity findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(FunctionEntity.class, id);
        }
    }

    public void save(FunctionEntity function) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(function);
            transaction.commit();
        }
    }

    public List<FunctionEntity> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM FunctionEntity", FunctionEntity.class).list();
        }
    }

    public List<FunctionEntity> findByType(int typeId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "FROM FunctionEntity WHERE typeId = :typeId", FunctionEntity.class)
                    .setParameter("typeId", typeId)
                    .list();
        }
    }



    // Создание математической функции
    public CompletableFuture<Long> createMathFunction(String expression) {
        return CompletableFuture.supplyAsync(() -> {
            // Валидация через парсинг
            parseFunction(expression);

            FunctionEntity function = new FunctionEntity();
            function.setTypeId(MATH_FUNCTION_ID);
            function.setExpression(expression);

            save(function);
            return function.getFuncId();
        });
    }

    public CompletableFuture<Long> createTabulated(String expression, double from, double to, int pointCount) {
        return CompletableFuture.supplyAsync(() -> {
            MathFunction mathFunc = parseFunction(expression);

            FunctionEntity function = new FunctionEntity();
            function.setTypeId(TABULATED_ID);
            function.setExpression(expression);
            save(function);

            TabulatedFunction tabFunc = new ArrayTabulatedFunction(mathFunc, from, to, pointCount);
            Point[] points = TabulatedFunctionOperationService.asPoints(tabFunc);


            for (Point point : points) {
                PointsEntity pointEntity = new PointsEntity(function, point.getX(), point.getY());
                pointsRepository.save(pointEntity);
            }

            return function.getFuncId();
        });
    }

    // Создание чисто табулированной функции
    public CompletableFuture<Long> createPureTabulated(double[] xValues, double[] yValues) {
        return CompletableFuture.supplyAsync(() -> {
            if (xValues.length != yValues.length) {
                throw new IllegalArgumentException("Arrays length mismatch");
            }

            FunctionEntity function = new FunctionEntity();
            function.setTypeId(TABULATED_ID);
            function.setExpression(PURE_TABULATED_EXPRESSION);
            save(function);


            for (int i = 0; i < xValues.length; i++) {
                PointsEntity point = new PointsEntity(function, xValues[i], yValues[i]);
                pointsRepository.save(point);
            }

            return function.getFuncId();
        });
    }

    // Создание композитной функции
    public CompletableFuture<Long> createComposite(Long innerId, Long outerId) {
        return CompletableFuture.supplyAsync(() -> {
            if (innerId == outerId) {
                throw new IllegalArgumentException("Inner and outer function IDs cannot be the same");
            }

            // Получаем функции из базы
            FunctionEntity innerFunc = findById(innerId);
            FunctionEntity outerFunc = findById(outerId);


            // Формируем выражение композитной функции
            String replaceWith = "(" + innerFunc.getExpression() + ")";
            String compositeExpression = outerFunc.getExpression().replaceAll("x", replaceWith);

            // Создаем основную функцию
            FunctionEntity compositeFunction = new FunctionEntity();
/*
            compositeFunction.setFuncId(idGenerator.getAndIncrement());
*/
            compositeFunction.setTypeId(COMPOSITE_ID);
            compositeFunction.setExpression(compositeExpression);
            save(compositeFunction);

            // Используем CompositeFunctionRepository для сохранения композитной связи
            CompositeFunctionEntity compositeEntity = new CompositeFunctionEntity();
            compositeEntity.setCompositeFunction(compositeFunction);
            compositeEntity.setInnerFunction(innerFunc);
            compositeEntity.setOuterFunction(outerFunc);
            compositeRepository.save(compositeEntity);

            return compositeFunction.getFuncId();
        });
    }

    // Получение функции как MathFunction
    public CompletableFuture<MathFunction> getFunction(Long funcId) {
        return getFunction(funcId, false);
    }

    public CompletableFuture<MathFunction> getFunction(Long funcId, boolean asMath) {
        return CompletableFuture.supplyAsync(() -> {
            FunctionEntity function = findById(funcId);
            if (function == null) {
                throw new IllegalArgumentException("Function not found with ID: " + funcId);
            }

            String expression = function.getExpression();
            int typeId = function.getTypeId();

            if (asMath) {
                if (expression.equals(PURE_TABULATED_EXPRESSION)) {
                    throw new RuntimeException("Can't return pure tabulated functions as a pure math function.");
                }
                return parseFunction(expression);
            }

            switch (typeId) {
                case MATH_FUNCTION_ID:
                    return parseFunction(expression);

                case TABULATED_ID:
                    return createTabulatedFunctionFromPoints(function);

                case COMPOSITE_ID:
                    return createCompositeFunction(funcId);

                default:
                    throw new IllegalArgumentException("Unknown function type: " + typeId);
            }
        });
    }

    // Обновление композитной функции
    public CompletableFuture<Void> updateComposite(Long funcId, Long newInner, Long newOuter) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<CompositeFunctionEntity> compositeOpt = compositeRepository.findById(funcId);
            FunctionEntity inner = findById(newInner);
            FunctionEntity outer = findById(newOuter);

            if (compositeOpt.isEmpty() || inner == null || outer == null) {
                throw new IllegalArgumentException("Composite function or inner/outer functions not found");
            }

            CompositeFunctionEntity composite = compositeOpt.get();
            composite.setInnerFunction(inner);
            composite.setOuterFunction(outer);

            compositeRepository.update(composite);
            return null;
        });
    }

    // Обновление точки
    public CompletableFuture<Void> updatePoint(Long funcId, double xValue, double newY) {
        return CompletableFuture.supplyAsync(() -> {
            pointsRepository.updateById(funcId, xValue, newY);
            return null;
        });
    }

    // Удаление точки
    public CompletableFuture<Void> deletePoint(Long funcId, double xValue) {
        return CompletableFuture.supplyAsync(() -> {
            pointsRepository.deleteById(funcId, xValue);
            return null;
        });
    }


    private TabulatedFunction createTabulatedFunctionFromPoints(FunctionEntity function) {
        // Используем PointsRepository для получения точек
        List<PointsEntity> pointsEntities = pointsRepository.findByFunction(function);

        if (pointsEntities.isEmpty()) {
            throw new RuntimeException("No points found for tabulated function");
        }

        // Сортируем точки по X
        pointsEntities.sort(Comparator.comparingDouble(PointsEntity::getXValue));

        double[] xValues = new double[pointsEntities.size()];
        double[] yValues = new double[pointsEntities.size()];

        for (int i = 0; i < pointsEntities.size(); i++) {
            PointsEntity point = pointsEntities.get(i);
            xValues[i] = point.getXValue();
            yValues[i] = point.getYValue();
        }

        return new ArrayTabulatedFunction(xValues, yValues);
    }

    private CompositeFunction createCompositeFunction(Long funcId) {
        // Используем CompositeFunctionRepository для получения композитной связи
        Optional<CompositeFunctionEntity> compositeOpt = compositeRepository.findById(funcId);
        if (compositeOpt.isEmpty()) {
            throw new RuntimeException("Composite function entity not found");
        }

        CompositeFunctionEntity composite = compositeOpt.get();
        Long innerId = composite.getInnerFunction().getFuncId();
        Long outerId = composite.getOuterFunction().getFuncId();

        if (innerId == funcId || outerId == funcId) {
            throw new RuntimeException("Attempt to make self-referential composite function");
        }

        try {
            CompletableFuture<MathFunction> innerFuture = getFunction(innerId);
            CompletableFuture<MathFunction> outerFuture = getFunction(outerId);
            return new CompositeFunction(innerFuture.get(), outerFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error creating composite function", e);
        }
    }

    public void saveAll(List<FunctionEntity> functions) {
        var session = sessionFactory.openSession();
        var transaction = session.beginTransaction();

        try {
            for (int i = 0; i < functions.size(); i++) {
                session.persist(functions.get(i));
                if (i % 50 == 0) {
                    session.flush();
                    session.clear();
                }
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error saving points batch", e);
        } finally {
            session.close();
        }
    }




}
