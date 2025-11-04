package ru.ssau.tk._AMEBA_._PESEZ_.repository;

import net.objecthunter.exp4j.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.*;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;
import ru.ssau.tk._AMEBA_._PESEZ_.operations.TabulatedFunctionOperationService;

import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.*;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

public class FunctionRepository extends Repository {
    private static final String FUNCTION_ENSURE_TABLE = readCommand("FunctionCreateTable");
    private static final String FUNCTION_INSERT = readCommand("FunctionCreate");
    private static final String FUNCTION_DELETE = readCommand("FunctionDelete");
    private static final String FUNCTION_SELECT = readCommand("FunctionRead");

    private static final String COMPOSITE_ENSURE_TABLE = readCommand("CompositeFunctionCreateTable");
    private static final String COMPOSITE_INSERT = readCommand("CompositeFunctionCreate");
    private static final String COMPOSITE_UPDATE = readCommand("CompositeFunctionUpdate");
    private static final String COMPOSITE_DELETE = readCommand("CompositeFunctionDelete");
    private static final String COMPOSITE_SELECT = readCommand("CompositeFunctionRead");

    private static final String POINTS_ENSURE_TABLE = readCommand("PointsCreateTable");
    private static final String POINTS_INSERT = readCommand("PointsCreate");
    private static final String POINTS_UPDATE = readCommand("PointsUpdate");
    private static final String POINTS_DELETE = readCommand("PointsDeleteMany");
    private static final String POINTS_DELETE_ONE = readCommand("PointsDeleteOne");
    private static final String POINTS_SELECT = readCommand("PointsRead");

    private static final int MathFunctionID = 1;
    private static final int TabulatedID = 2;
    private static final int CompositeID = 3;
    private static final int PureTabulatedID = 4;

    public enum FunctionType {MathFunctionType, TabulatedFunctionType, PureTabulatedType, CompositeType}

    public FunctionRepository(String url) {
        super(url);
    }

    public FunctionRepository(DatabaseConnection connection) {
        super(connection);
    }

    public void ensureTables() {
        try {
            ensureFunctionTable();
            ensurePointsTable();
            ensureCompositeTable();
        } catch (SQLException e) {
            Log.error("Error when trying to ensure SQL tables:", e);
        }
    }

    public CompletableFuture<Integer> createMathFunction(String expression) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DatabaseConnection database = databaseLocal.get();
                // Читаем функцию, чтобы убедиться, что она правильная
                parseFunction(expression);
                int funcId = database.executeUpdateAndGetId(FUNCTION_INSERT, MathFunctionID, expression);
                Log.info("Wrote math function {} into database with ID {}", expression, funcId);
                return funcId;
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<Integer> createTabulated(String expression, double from, double to, int pointCount) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DatabaseConnection database = databaseLocal.get();
                MathFunction func = parseFunction(expression);
                int funcId = database.executeUpdateAndGetId(FUNCTION_INSERT, TabulatedID, expression);
                Log.info("Wrote tabulated function {} into database with ID {}", expression, funcId);
                TabulatedFunction tabFunc = new ArrayTabulatedFunction(func, from, to, pointCount);
                Point[] points = TabulatedFunctionOperationService.asPoints(tabFunc);

                // Строим запрос для помещения всех точек в таблицу сразу
                try (PreparedStatement stmt = database.getConnection().prepareStatement(POINTS_INSERT)) {
                    for (Point p : points) {
                        stmt.setInt(1, funcId);
                        stmt.setDouble(2, p.getX());
                        stmt.setDouble(3, p.getY());
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
                return funcId;
            } catch (SQLException  e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<Integer> createPureTabulated(double[] xValues, double[] yValues) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DatabaseConnection database = databaseLocal.get();
                int funcId = database.executeUpdateAndGetId(FUNCTION_INSERT, PureTabulatedID, "<TABULATED>");
                Log.info("Wrote pure tabulated function into database with ID {}", funcId);

                // Строим запрос для помещения всех точек в таблицу сразу
                try (PreparedStatement stmt = database.getConnection().prepareStatement(POINTS_INSERT)) {
                    for (int i = 0; i < xValues.length; i++) {
                        stmt.setInt(1, funcId);
                        stmt.setDouble(2, xValues[i]);
                        stmt.setDouble(3, yValues[i]);
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
                return funcId;
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<Void> createPoint(int funcId, double xValue, double yValue) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DatabaseConnection database = databaseLocal.get();
                database.executeUpdate(POINTS_INSERT, funcId, xValue, yValue);
                Log.info("Wrote single point into database for function ID {}: ({}, {})", funcId, xValue, yValue);
                return null;
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<Integer> createComposite(int innerId, int outerId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DatabaseConnection database = databaseLocal.get();
                try (ResultSet inner = database.executeQuery(FUNCTION_SELECT, innerId);
                 ResultSet outer = database.executeQuery(FUNCTION_SELECT, outerId)) {
                    if (!inner.first() || !outer.first())
                        return null;
                    String replaceWith = "(" + inner.getString("expression") + ")";
                    String expression = outer.getString("expression").replaceAll("x", replaceWith);
                    int funcId = database.executeUpdateAndGetId(FUNCTION_INSERT, CompositeID, expression);
                    database.executeUpdate(COMPOSITE_INSERT, funcId, innerId, outerId);
                    Log.info("Wrote composite function from IDs {}({}) with expression: `{}` and ID {}", outerId, innerId, expression, funcId);
                    return funcId;
                }
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<FunctionDTO> getFunctionData(int funcId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DatabaseConnection database = databaseLocal.get();
                try (ResultSet results = database.executeQuery(FUNCTION_SELECT, funcId)) {
                    if (!results.first())
                        return null;
                    int typeId =results.getInt("type_id");
                    FunctionType type = switch (typeId) {
                        case MathFunctionID -> FunctionType.MathFunctionType;
                        case TabulatedID -> FunctionType.TabulatedFunctionType;
                        case CompositeID -> FunctionType.CompositeType;
                        case PureTabulatedID -> FunctionType.PureTabulatedType;
                        default -> throw new RuntimeException("Unexpected value: " + typeId);
                    };
                    return new FunctionDTO(funcId,
                            type,
                            results.getString("expression")
                    );
                }
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<CompositeFunctionDTO> getCompositeData(int funcId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DatabaseConnection database = databaseLocal.get();
                try (ResultSet results = database.executeQuery(COMPOSITE_SELECT, funcId)) {
                    if (!results.first())
                        return null;
                    return new CompositeFunctionDTO(funcId,
                            results.getInt("inner_func_id"),
                            results.getInt("outer_func_id")
                    );
                }
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<PointsDTO> getPointsData(int funcId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DatabaseConnection database = databaseLocal.get();
                try (ResultSet points = database.executeQuery(POINTS_SELECT, funcId)) {
                    if (!points.last())
                        return null;
                    int count = points.getRow();
                    points.first();
                    var xValues = new double[count];
                    var yValues = new double[count];
                    for (int i = 0; i < count; i++) {
                        xValues[i] = points.getDouble(1);
                        yValues[i] = points.getDouble(2);
                        points.relative(1);
                    }
                    return new PointsDTO(funcId, xValues, yValues, false);
                }
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<MathFunction> getFunction(int funcId) {
        return getFunction(funcId, false);
    }

    public CompletableFuture<MathFunction> getFunction(int funcId, boolean asMath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                FunctionDTO func = getFunctionData(funcId).get();
                if (asMath) {
                    if (func.funcType == FunctionType.PureTabulatedType)
                        throw new RuntimeException("Can't return pure tabulated functions as a pure math function.");
                    return parseFunction(func.expression);
                }
                switch (func.funcType) {
                    case MathFunctionType: {
                        return parseFunction(func.expression);
                    }
                    case PureTabulatedType:
                    case TabulatedFunctionType: {
                        PointsDTO pts = getPointsData(funcId).get();
                        return new ArrayTabulatedFunction(pts.xValues, pts.yValues);
                    }
                    case CompositeType: {
                        CompositeFunctionDTO composite = getCompositeData(funcId).get();
                        CompletableFuture<MathFunction> innerFunc = getFunction(composite.innerFuncId);
                        CompletableFuture<MathFunction> outerFunc = getFunction(composite.outerFuncId);
                        return new CompositeFunction(innerFunc.get(), outerFunc.get());
                    }
                }
                return null;
            } catch (InterruptedException | ExecutionException e) {
                throw new CompletionException(e);
            }
        });
    }

    // Обновляет композитную функцию, аргументы могут быть null чтобы не обновлять этот параметр
    public CompletableFuture<Void> updateComposite(int funcId, Integer newInner, Integer newOuter) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DatabaseConnection database = databaseLocal.get();
                try (ResultSet row = database.executeQuery(COMPOSITE_SELECT, funcId)) {
                    row.first();
                    int inner = row.getInt("inner_func_id");
                    int outer = row.getInt("outer_func_id");
                    if (newInner != null) inner = newInner;
                    if (newOuter != null) outer = newOuter;
                    database.executeUpdate(COMPOSITE_UPDATE, inner, outer, funcId);
                    return null;
                }
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<Void> updatePoint(int funcId, double xValue, double newY) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DatabaseConnection database = databaseLocal.get();
                database.executeUpdate(POINTS_UPDATE, newY, xValue, funcId);
                return null;
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<Void> deletePoint(int funcId, double xValue) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DatabaseConnection database = databaseLocal.get();
                database.executeUpdate(POINTS_DELETE_ONE, funcId, xValue);
                return null;
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<Void> deleteFunction(int funcId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DatabaseConnection database = databaseLocal.get();
                database.executeUpdate(POINTS_DELETE, funcId);
                database.executeUpdate(COMPOSITE_DELETE, funcId);
                database.executeUpdate(FUNCTION_DELETE, funcId);
                return null;
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        });
    }

    private void ensureFunctionTable() throws SQLException {
        DatabaseConnection database = databaseLocal.get();
        database.executeUpdate(FUNCTION_ENSURE_TABLE);
    }

    private void ensureCompositeTable() throws SQLException {
        DatabaseConnection database = databaseLocal.get();
        database.executeUpdate(COMPOSITE_ENSURE_TABLE);
    }

    private void ensurePointsTable() throws SQLException {
        DatabaseConnection database = databaseLocal.get();
        database.executeUpdate(POINTS_ENSURE_TABLE);
    }

    public static MathFunction parseFunction(String expression) {
        Expression expr = new ExpressionBuilder(expression).variable("x").build();
        return (double x) -> expr.setVariable("x", x).evaluate();
    }
}
