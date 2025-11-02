package ru.ssau.tk._AMEBA_._PESEZ_.service;

import net.objecthunter.exp4j.*;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;
import ru.ssau.tk._AMEBA_._PESEZ_.operations.TabulatedFunctionOperationService;

import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FunctionRepository {
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

    private static Map<String, AtomicInteger> NextFunctionIDs = new ConcurrentHashMap<>();

    private static final String PureTabulatedExpression = "<TABULATED>";

    private ThreadLocal<DatabaseConnection> databaseLocal;

    private static String readCommand(String filename) {
        try (InputStream instream = FunctionRepository.class.getClassLoader().getResourceAsStream("scripts/" + filename + ".sql")) {
            return new String(instream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException | NullPointerException e) {
            Log.error("Failed to read SQL command:", e);
            return null;
        }
    }

    public FunctionRepository(String url) {
        this.databaseLocal = ThreadLocal.withInitial(() -> new DatabaseConnection(url));;
    }

    public FunctionRepository(DatabaseConnection connection) {
        this.databaseLocal = ThreadLocal.withInitial(() -> connection);
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
                int funcId = getNextFunctionId();
                Log.info("Writing math function {} into database as ID {}", expression, funcId);
                database.executeUpdate(FUNCTION_INSERT, funcId, MathFunctionID, expression);
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
                int funcId = getNextFunctionId();
                Log.info("Writing tabulated function {} into database as ID {}", expression, funcId);
                database.executeUpdate(FUNCTION_INSERT, funcId, TabulatedID, expression);
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
                int funcId = getNextFunctionId();
                Log.info("Writing pure tabulated function into database as ID {}", funcId);
                database.executeUpdate(FUNCTION_INSERT, funcId, TabulatedID, "<TABULATED>");

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

    public CompletableFuture<Integer> createComposite(int innerId, int outerId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DatabaseConnection database = databaseLocal.get();
                int funcId = getNextFunctionId();
                try (ResultSet inner = database.executeQuery(FUNCTION_SELECT, innerId);
                 ResultSet outer = database.executeQuery(FUNCTION_SELECT, outerId)) {
                    inner.first();
                    outer.first();
                    String replaceWith = "(" + inner.getString("expression") + ")";
                    String expression = outer.getString("expression").replaceAll("x", replaceWith);
                    Log.info("Writing composite function from IDs {}({}) with expression: `{}` as ID {}", outerId, innerId, expression, funcId);
                    database.executeUpdate(FUNCTION_INSERT, funcId, CompositeID, expression);
                    database.executeUpdate(COMPOSITE_INSERT, funcId, innerId, outerId);
                    return funcId;
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
                DatabaseConnection database = databaseLocal.get();
                try (ResultSet results = database.executeQuery(FUNCTION_SELECT, funcId)) {
                    results.first();
                    String expr = results.getString("expression");
                    int typeId = results.getInt("type_id");
                    if (asMath) {
                        if (expr.equals(PureTabulatedExpression)) throw new RuntimeException("Can't return pure tabulated functions as a pure math function.");
                        return parseFunction(expr);
                    }
                    switch (typeId) {
                        case (MathFunctionID): {
                            return parseFunction(expr);
                        }
                        case (TabulatedID): {
                            try (ResultSet points = database.executeQuery(POINTS_SELECT, funcId)) {
                                points.last();
                                int count = points.getRow();
                                var newPoints = new Point[count];
                                points.first();
                                for (int i = 0; i < count; i++) {
                                    double x = points.getDouble(1);
                                    double y = points.getDouble(2);
                                    newPoints[i] = new Point(x, y);
                                    points.relative(1);
                                }
                                Arrays.sort(newPoints, Comparator.comparingDouble(Point::getX));
                                var xValues = new double[count];
                                var yValues = new double[count];
                                for (int i = 0; i < count; i++) {
                                    Point p = newPoints[i];
                                    xValues[i] = p.getX();
                                    yValues[i] = p.getY();
                                }
                                return new ArrayTabulatedFunction(xValues, yValues);
                            }
                        }
                        case (CompositeID): {
                            try (ResultSet functions = database.executeQuery(COMPOSITE_SELECT, funcId)) {
                                functions.first();
                                int inner = functions.getInt("inner_func_id");
                                int outer = functions.getInt("outer_func_id");
                                if (inner == funcId || outer == funcId) {
                                    throw new RuntimeException("Attempt to make self-referential composite function");
                                }
                                CompletableFuture<MathFunction> innerFunc = getFunction(inner);
                                CompletableFuture<MathFunction> outerFunc = getFunction(outer);
                                return new CompositeFunction(innerFunc.get(), outerFunc.get());
                            }
                        }
                    }
                }
                return null;
            } catch (SQLException | InterruptedException | ExecutionException e) {
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

    private int getNextFunctionId() throws SQLException {
        DatabaseConnection database = databaseLocal.get();
        AtomicInteger nextFunctionID = NextFunctionIDs.computeIfAbsent(database.getURL(), k -> new AtomicInteger(-1));

        synchronized (nextFunctionID) {
            if (nextFunctionID.get() == -1) {
                try (ResultSet rs = database.executeQuery("SELECT MAX(func_id) FROM function")) {
                    nextFunctionID.set(rs.first() ? rs.getInt(1) + 1 : 1);
                }
            }
            return nextFunctionID.getAndIncrement();
        }
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
