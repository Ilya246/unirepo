package ru.ssau.tk._AMEBA_._PESEZ_.service;

import org.hibernate.SessionFactory;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.MathFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.CompositeFunctionRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionOwnershipRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.PointsRepository;
import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.Log;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class FunctionService {
    private final FunctionRepository functionRepository;
    private final FunctionOwnershipRepository ownershipRepository;
    private final CompositeFunctionRepository compositeRepository;
    private final PointsRepository pointsRepository;

    public FunctionService(SessionFactory sessionFactory) {
        this.functionRepository = new FunctionRepository(sessionFactory);
        this.ownershipRepository = new FunctionOwnershipRepository(sessionFactory);
        this.compositeRepository = new CompositeFunctionRepository(sessionFactory);
        this.pointsRepository = new PointsRepository(sessionFactory);
        Log.debug("Все репозитории успешно инициализированы");
    }

    public Optional<UserEntity> getFunctionOwner(int functionId) {
        try {
            Optional<UserEntity> owner = ownershipRepository.findOwnerByFunctionId(functionId);
            if (owner.isPresent()) {
                Log.debug("Найден владелец функции {}: пользователь ID {}", functionId, owner.get().getUserId());
            } else {
                Log.debug("Владелец для функции {} не найден", functionId);
            }
            return owner;
        } catch (Exception e) {
            Log.error("Ошибка при поиске владельца функции {}: {}", functionId, e.getMessage(), e);
            throw e;
        }
    }

    public FunctionEntity getFunctionById(int functionId) {
        try {
            FunctionEntity function = functionRepository.findById(functionId);
            if (function != null) {
                Log.debug("Функция найдена: ID {}, выражение: {}", functionId, function.getExpression());
            } else {
                Log.debug("Функция с ID {} не найдена", functionId);
            }
            return function;
        } catch (Exception e) {
            Log.error("Ошибка при получении функции {}: {}", functionId, e.getMessage(), e);
            throw e;
        }
    }

    public List<FunctionEntity> getAllFunctions() {
        try {
            List<FunctionEntity> functions = functionRepository.findAll();
            Log.debug("Получено {} функций из базы данных", functions.size());
            return functions;
        } catch (Exception e) {
            Log.error("Ошибка при получении списка всех функций: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<FunctionEntity> getFunctionsByType(int typeId) {
        try {
            List<FunctionEntity> functions = functionRepository.findByType(typeId);
            Log.debug("Найдено {} функций типа {}", functions.size(), typeId);
            return functions;
        } catch (Exception e) {
            Log.error("Ошибка при поиске функций типа {}: {}", typeId, e.getMessage(), e);
            throw e;
        }
    }

    public void saveFunc(FunctionEntity function) {
        try {
            functionRepository.save(function);
            Log.debug("Функция успешно сохранена с ID: {}", function.getFuncId());
        } catch (Exception e) {
            Log.error("Ошибка при сохранении функции '{}': {}", function.getExpression(), e.getMessage(), e);
            throw e;
        }
    }


    public List<FunctionEntity> getUserFunctionsSortedByDate(int userId, boolean descending) {
        String order = descending ? "убыванию" : "возрастанию";
        try {
            List<FunctionEntity> functions = ownershipRepository.findUserFunctionsOrderByDate(userId, descending);
            Log.debug("Найдено {} функций для пользователя {}, отсортированных по {}",
                    functions.size(), userId, order);
            return functions;
        } catch (Exception e) {
           Log.error("Ошибка при получении функций пользователя {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    public CompletableFuture<Integer> createMathFunction(String expression) {
        return functionRepository.createMathFunction(expression)
                .whenComplete((funcId, throwable) -> {
                    if (throwable != null) {
                        Log.error("Ошибка при создании математической функции '{}': {}",
                                expression, throwable.getMessage(), throwable);
                    } else {
                        Log.debug("Математическая функция создана с ID: {}", funcId);
                    }
                });
    }

    public CompletableFuture<Integer> createTabulated(String expression, double from, double to, int pointCount) {
        return functionRepository.createTabulated(expression, from, to, pointCount)
                .whenComplete((funcId, throwable) -> {
                    if (throwable != null) {
                        Log.error("Ошибка при создании табулированной функции '{}': {}",
                                expression, throwable.getMessage(), throwable);
                    } else {
                        Log.debug("Табулированная функция создана с ID: {}", funcId);
                    }
                });
    }

    public CompletableFuture<Integer> createComposite(int innerId, int outerId) {
        return functionRepository.createComposite(innerId, outerId)
                .whenComplete((funcId, throwable) -> {
                    if (throwable != null) {
                        Log.error("Ошибка при создании композитной функции (inner={}, outer={}): {}",
                                innerId, outerId, throwable.getMessage(), throwable);
                    } else {
                        Log.debug("Композитная функция создана с ID: {}", funcId);
                    }
                });
    }

    public CompletableFuture<MathFunction> getFunction(int funcId) {
        return functionRepository.getFunction(funcId)
                .whenComplete((mathFunction, throwable) -> {
                    if (throwable != null) {
                        Log.error("Ошибка при получении функции {} как MathFunction: {}",
                                funcId, throwable.getMessage(), throwable);
                    } else {
                       Log.debug("Функция {} успешно преобразована в MathFunction", funcId);
                    }
                });
    }

    public CompletableFuture<Void> updateComposite(int funcId, Integer newInner, Integer newOuter) {

        return functionRepository.updateComposite(funcId, newInner, newOuter)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        Log.error("Ошибка при обновлении композитной функции {}: {}",
                                funcId, throwable.getMessage(), throwable);
                    } else {
                        Log.debug("Композитная функция {} успешно обновлена", funcId);
                    }
                });
    }

    public CompletableFuture<Void> updatePoint(int funcId, double xValue, double newY) {
        return functionRepository.updatePoint(funcId, xValue, newY)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        Log.error("Ошибка при обновлении точки (x={}) функции {}: {}",
                                xValue, funcId, throwable.getMessage(), throwable);
                    } else {
                        Log.debug("Точка (x={}) функции {} успешно обновлена на y={}",
                                xValue, funcId, newY);
                    }
                });
    }

    public CompletableFuture<Void> deletePoint(int funcId, double xValue) {
        return functionRepository.deletePoint(funcId, xValue)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        Log.error("Ошибка при удалении точки (x={}) функции {}: {}",
                                xValue, funcId, throwable.getMessage(), throwable);
                    } else {
                        Log.debug("Точка (x={}) функции {} успешно удалена", xValue, funcId);
                    }
                });
    }


    public void saveAllFunctions(List<FunctionEntity> functions) {
        try {
            functionRepository.saveAll(functions);
            Log.debug("Все {} функций успешно сохранены", functions.size());
        } catch (Exception e) {
            Log.error("Ошибка при массовом сохранении {} функций: {}",
                    functions.size(), e.getMessage(), e);
            throw e;
        }
    }


}
