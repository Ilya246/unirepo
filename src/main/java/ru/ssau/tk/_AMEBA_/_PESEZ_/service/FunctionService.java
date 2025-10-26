package ru.ssau.tk._AMEBA_._PESEZ_.service;

import net.objecthunter.exp4j.*;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;
import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;

public class FunctionService {
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

    private static String readCommand(String filename) {
        try (InputStream instream = FunctionService.class.getClassLoader().getResourceAsStream("scripts/" + filename + ".sql")) {
            return new String(instream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException | NullPointerException e) {
            Log.error("Failed to read SQL command:", e);
            return null;
        }
    }

    public static void ensureTables(DatabaseConnection database) {
        try {
            ensureFunctionTable(database);
            ensurePointsTable(database);
            ensureCompositeTable(database);
        } catch (SQLException e) {
            Log.error("Error when trying to ensure SQL tables:", e);
        }
    }

    public static int createMathFunction(String expression, DatabaseConnection database) throws SQLException {
        // Читаем функцию, чтобы убедиться, что она правильная
        parseFunction(expression);
        int funcId = getNextFunctionId(database);
        Log.info("Writing math function {} into database as ID {}", expression, funcId);
        database.executeUpdate(FUNCTION_INSERT, funcId, MathFunctionID, expression);
        return funcId;
    }

    public static int createTabulated(String expression, double from, double to, int pointCount, DatabaseConnection database) throws SQLException {
        MathFunction func = parseFunction(expression);
        int funcId = getNextFunctionId(database);
        Log.info("Writing tabulated function {} into database as ID {}", expression, funcId);
        database.executeUpdate(FUNCTION_INSERT, funcId, TabulatedID, expression);
        TabulatedFunction tabFunc = new ArrayTabulatedFunction(func, from, to, pointCount);
        for (Point p : tabFunc) {
            database.executeUpdate(POINTS_INSERT, funcId, p.getX(), p.getY());
        }
        return funcId;
    }

    public static int createComposite(int innerId, int outerId, DatabaseConnection database) throws SQLException {
        int funcId = getNextFunctionId(database);
        try (ResultSet inner = database.executeQuery(FUNCTION_SELECT, innerId);
         ResultSet outer = database.executeQuery(FUNCTION_SELECT, outerId)) {
            inner.first();
            outer.first();
            String replaceWith = "(" + inner.getString("expression") + ")";
            String expression = outer.getString("expression").replaceAll("x", replaceWith);
            Log.info("Writing composite function from IDs {}({}) with expression: `{}` as ID {}", outer, innerId, expression, funcId);
            database.executeUpdate(FUNCTION_INSERT, funcId, CompositeID, expression);
            database.executeUpdate(COMPOSITE_INSERT, funcId, innerId, outerId);
            return funcId;
        }
    }

    public static MathFunction getFunction(int funcId, DatabaseConnection database) throws SQLException {
        return getFunction(funcId, false, database);
    }

    public static MathFunction getFunction(int funcId, boolean asMath, DatabaseConnection database) throws SQLException {
        try (ResultSet results = database.executeQuery(FUNCTION_SELECT, funcId)) {
            results.first();
            String expr = results.getString("expression");
            if (asMath) return parseFunction(expr);
            int typeId = results.getInt("type_id");
            switch (typeId) {
                case (MathFunctionID): {
                    return parseFunction(expr);
                }
                case (TabulatedID): {
                    try (ResultSet points = database.executeQuery(POINTS_SELECT, funcId)) {
                        points.last();
                        int count = points.getRow();
                        var xValues = new double[count];
                        var yValues = new double[count];
                        points.first();
                        for (int i = 0; i < count; i++) {
                            double x = points.getDouble(1);
                            double y = points.getDouble(2);
                            xValues[i] = x;
                            yValues[i] = y;
                            points.relative(1);
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
                        return new CompositeFunction(getFunction(inner, database), getFunction(outer, database));
                    }
                }
            }
        }
        return null;
    }

    // Обновляет композитную функцию, аргументы могут быть null чтобы не обновлять этот параметр
    public static void updateComposite(int funcId, Integer newInner, Integer newOuter, DatabaseConnection database) throws SQLException {
        try (ResultSet row = database.executeQuery(COMPOSITE_SELECT, funcId)) {
            row.first();
            int inner = row.getInt("inner_func_id");
            int outer = row.getInt("outer_func_id");
            if (newInner != null) inner = newInner;
            if (newOuter != null) outer = newOuter;
            database.executeUpdate(COMPOSITE_UPDATE, funcId, inner, outer);
        }
    }

    public static void updatePoint(int funcId, double xValue, double newY, DatabaseConnection database) throws SQLException {
        database.executeUpdate(POINTS_UPDATE, funcId, xValue, newY);
    }

    public static void deletePoint(int funcId, double xValue, DatabaseConnection database) throws SQLException {
        database.executeUpdate(POINTS_DELETE_ONE, funcId, xValue);
    }

    public static void deleteFunction(int funcId, DatabaseConnection database) throws SQLException {
        database.executeUpdate(POINTS_DELETE, funcId);
        database.executeUpdate(COMPOSITE_DELETE, funcId);
        database.executeUpdate(FUNCTION_DELETE, funcId);
    }

    private static int getFunctionTypeId(MathFunction function) {
        if (function instanceof CompositeFunction) return CompositeID;
        if (function instanceof TabulatedFunction) return TabulatedID;
        else return MathFunctionID;
    }

    private static int getNextFunctionId(DatabaseConnection database) throws SQLException {
        try (ResultSet rs = database.executeQuery("SELECT MAX(func_id) FROM function")) {
            return rs.first() ? rs.getInt(1) + 1 : 1;
        }
    }

    private static void ensureFunctionTable(DatabaseConnection database) throws SQLException {
        database.executeUpdate(FUNCTION_ENSURE_TABLE);
    }

    private static void ensureCompositeTable(DatabaseConnection database) throws SQLException {
        database.executeUpdate(COMPOSITE_ENSURE_TABLE);
    }

    private static void ensurePointsTable(DatabaseConnection database) throws SQLException {
        database.executeUpdate(POINTS_ENSURE_TABLE);
    }

    public static MathFunction parseFunction(String expression) {
        Expression expr = new ExpressionBuilder(expression).variable("x").build();
        return (double x) -> expr.setVariable("x", x).evaluate();
    }
}
