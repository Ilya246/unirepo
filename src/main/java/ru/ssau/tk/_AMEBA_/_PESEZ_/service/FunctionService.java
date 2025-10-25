package ru.ssau.tk._AMEBA_._PESEZ_.service;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;
import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.*;

import java.io.*;
import java.nio.file.*;
import java.sql.*;

public class FunctionService {
    private static final String FUNCTION_INSERT = readCommand("FunctionCreate");
    private static final String FUNCTION_DELETE = readCommand("FunctionDelete");
    private static final String FUNCTION_SELECT = readCommand("FunctionRead");

    private static final String COMPOSITE_INSERT = readCommand("CompositeFunctionCreate");
    private static final String COMPOSITE_UPDATE = readCommand("CompositeFunctionUpdate");
    private static final String COMPOSITE_DELETE = readCommand("CompositeFunctionDelete");
    private static final String COMPOSITE_SELECT = readCommand("CompositeFunctionRead");

    private static final String POINTS_INSERT = readCommand("PointsCreate");
    private static final String POINTS_UPDATE = readCommand("PointsUpdate");
    private static final String POINTS_DELETE = readCommand("PointsDeleteMany");
    private static final String POINTS_DELETE_ONE = readCommand("PointsDeleteOne");
    private static final String POINTS_SELECT = readCommand("PointsRead");

    private static final int MathFunctionID = 1;
    private static final int TabulatedID = 2;
    private static final int CompositeID = 3;

    private static String readCommand(String filename) {
        try {
            return Files.readString(Paths.get(filename));
        } catch (IOException e) {
            Log.error("Failed to read SQL command:", e);
            return null;
        }
    }

    public static int createTabulated(String expression, double from, double to, int pointCount) throws SQLException {
        MathFunction func = new IdentityFunction(); // placeholder
        int funcId = getNextFunctionId();
        DatabaseConnection.executeUpdate(FUNCTION_INSERT, funcId, TabulatedID, expression);
        TabulatedFunction tabFunc = new ArrayTabulatedFunction(func, from, to, pointCount);
        for (Point p : tabFunc) {
            DatabaseConnection.executeUpdate(POINTS_INSERT, funcId, p.getX(), p.getY());
        }
        return funcId;
    }

    public static int createComposite(int innerId, int outerId) throws SQLException {
        int funcId = getNextFunctionId();
        try (ResultSet inner = DatabaseConnection.executeQuery(FUNCTION_SELECT, innerId);
         ResultSet outer = DatabaseConnection.executeQuery(FUNCTION_SELECT, outerId)) {
            inner.first();
            outer.first();
            String expression = outer.getString("expression") + "(" + inner.getString("expression") + ")";
            DatabaseConnection.executeUpdate(FUNCTION_INSERT, funcId, CompositeID, expression);
            DatabaseConnection.executeUpdate(COMPOSITE_INSERT, funcId, innerId, outerId);
            return funcId;
        }
    }

    public static MathFunction getFunction(int funcId) throws SQLException {
        try (ResultSet results = DatabaseConnection.executeQuery(FUNCTION_SELECT, funcId)) {
            results.first();
            int typeId = results.getInt("type_id");
            switch (typeId) {
                case (MathFunctionID): {
                    return null;
                }
                case (TabulatedID): {
                    try (ResultSet points = DatabaseConnection.executeQuery(POINTS_SELECT, funcId)) {
                        points.last();
                        int count = points.getRow();
                        var xValues = new double[count];
                        var yValues = new double[count];
                        points.first();
                        for (int i = 0; i < count; i++) {
                            double x = points.getDouble(0);
                            double y = points.getDouble(1);
                            xValues[i] = x;
                            yValues[i] = y;
                            points.relative(1);
                        }
                        return new ArrayTabulatedFunction(xValues, yValues);
                    }
                }
                case (CompositeID): {
                    try (ResultSet functions = DatabaseConnection.executeQuery(COMPOSITE_SELECT, funcId)) {
                        functions.first();
                        int inner = functions.getInt("inner_func_id");
                        int outer = functions.getInt("outer_func_id");
                        if (inner == funcId || outer == funcId) {
                            throw new RuntimeException("Попытка сделать композитную функцию из самой себя");
                        }
                        return new CompositeFunction(getFunction(inner), getFunction(outer));
                    }
                }
            }
        }
        return null;
    }

    // Обновляет композитную функцию, аргументы могут быть null чтобы не обновлять этот параметр
    public static void updateComposite(int funcId, Integer newInner, Integer newOuter) throws SQLException {
        try (ResultSet row = DatabaseConnection.executeQuery(COMPOSITE_SELECT, funcId)) {
            row.first();
            int inner = row.getInt("inner_func_id");
            int outer = row.getInt("outer_func_id");
            if (newInner != null) inner = newInner;
            if (newOuter != null) outer = newOuter;
            DatabaseConnection.executeUpdate(COMPOSITE_UPDATE, funcId, inner, outer);
        }
    }

    public static void updatePoint(int funcId, double xValue, double newY) throws SQLException {
        DatabaseConnection.executeUpdate(POINTS_UPDATE, funcId, xValue, newY);
    }

    public static void deletePoint(int funcId, double xValue) throws SQLException {
        DatabaseConnection.executeUpdate(POINTS_DELETE_ONE, funcId, xValue);
    }

    public static void deleteFunction(int funcId) throws SQLException {
        DatabaseConnection.executeUpdate(FUNCTION_DELETE, funcId);
        DatabaseConnection.executeUpdate(POINTS_DELETE, funcId);
        DatabaseConnection.executeUpdate(COMPOSITE_DELETE, funcId);
    }

    private static int getFunctionTypeId(MathFunction function) {
        if (function instanceof CompositeFunction) return CompositeID;
        if (function instanceof TabulatedFunction) return TabulatedID;
        else return MathFunctionID;
    }

    private static int getNextFunctionId() throws SQLException {
        try (ResultSet rs = DatabaseConnection.executeQuery("SELECT MAX(func_id) FROM function")) {
            return rs.next() ? rs.getInt(1) + 1 : 1;
        }
    }
}
