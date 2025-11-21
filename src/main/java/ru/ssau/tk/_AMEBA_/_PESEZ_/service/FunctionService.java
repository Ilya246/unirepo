package ru.ssau.tk._AMEBA_._PESEZ_.service;

import ru.ssau.tk._AMEBA_._PESEZ_.dto.*;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.MathFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository;

import java.util.concurrent.CompletableFuture;

import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.Log;

public class FunctionService {
    private final FunctionRepository funcRepo;

    public FunctionService(String config) {
        funcRepo = new FunctionRepository(config);
        funcRepo.ensureTables();
    }

    public CompletableFuture<Double> calculateFunction(int funcId, double x) {
        return CompletableFuture.supplyAsync(() -> {
            // TODO: caching
            MathFunction func = funcRepo.getFunction(funcId).join();
            return func.apply(x);
        });
    }

    public CompletableFuture<FunctionDTO> getFunction(int funcId) {
        return funcRepo.getFunctionData(funcId);
    }

    public CompletableFuture<FunctionDTO[]> getAllFunctions() {
        return funcRepo.getAllFunctionData();
    }

    public CompletableFuture<Integer> createMathFunction(String expression) {
        Log.info("Creating math function ({})", expression);
        return funcRepo.createMathFunction(expression);
    }

    public CompletableFuture<Integer> createTabulated(String expression, double from, double to, int pointCount) {
        Log.info("Creating tabulated function ({}) with {} pts", expression, pointCount);
        return funcRepo.createTabulated(expression, from, to, pointCount);
    }

    public CompletableFuture<Integer> createPureTabulated(double[] xValues, double[] yValues) {
        Log.info("Creating pure tabulated function with {} pts", xValues.length);
        return funcRepo.createPureTabulated(xValues, yValues);
    }

    public CompletableFuture<Void> createPoint(int funcId, double xValue, double yValue) {
        return funcRepo.createPoint(funcId, xValue, yValue);
    }

    public CompletableFuture<Integer> createComposite(int innerId, int outerId) {
        Log.info("Creating composite function {}({})", outerId, innerId);
        return funcRepo.createComposite(innerId, outerId);
    }

    public CompletableFuture<Void> deletePoint(int funcId, double xValue) {
        return funcRepo.deletePoint(funcId, xValue);
    }

    public CompletableFuture<Void> deleteFunction(int funcId) {
        Log.info("Deleting function {}", funcId);
        return funcRepo.deleteFunction(funcId);
    }

    public CompletableFuture<CompositeFunctionDTO> getCompositeData(int funcId) {
        return funcRepo.getCompositeData(funcId);
    }

    public CompletableFuture<PointsDTO> getPoints(int funcId) {
        return funcRepo.getPointsData(funcId);
    }

    public CompletableFuture<Void> updateComposite(int funcId, Integer newInner, Integer newOuter) {
        Log.info("Updating composite function {} to {}({})", funcId, newOuter, newInner);
        return funcRepo.updateComposite(funcId, newInner, newOuter);
    }

    public CompletableFuture<Void> updatePoint(int funcId, double xValue, double newY) {
        return funcRepo.updatePoint(funcId, xValue, newY);
    }
}
