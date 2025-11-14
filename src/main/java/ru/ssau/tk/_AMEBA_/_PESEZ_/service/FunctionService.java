package ru.ssau.tk._AMEBA_._PESEZ_.service;

import ru.ssau.tk._AMEBA_._PESEZ_.dto.*;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.MathFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository;

import java.util.concurrent.CompletableFuture;

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

    public CompletableFuture<Integer> createMathFunction(String expression) {
        return funcRepo.createMathFunction(expression);
    }

    public CompletableFuture<Integer> createTabulated(String expression, double from, double to, int pointCount) {
        return funcRepo.createTabulated(expression, from, to, pointCount);
    }

    public CompletableFuture<Integer> createPureTabulated(double[] xValues, double[] yValues) {
        return funcRepo.createPureTabulated(xValues, yValues);
    }

    public CompletableFuture<Void> createPoint(int funcId, double xValue, double yValue) {
        return funcRepo.createPoint(funcId, xValue, yValue);
    }

    public CompletableFuture<Integer> createComposite(int innerId, int outerId) {
        return funcRepo.createComposite(innerId, outerId);
    }

    public CompletableFuture<Void> deletePoint(int funcId, double xValue) {
        return funcRepo.deletePoint(funcId, xValue);
    }

    public CompletableFuture<Void> deleteFunction(int funcId) {
        return funcRepo.deleteFunction(funcId);
    }

    public CompletableFuture<CompositeFunctionDTO> getCompositeData(int funcId) {
        return funcRepo.getCompositeData(funcId);
    }

    public CompletableFuture<PointsDTO> getPoints(int funcId) {
        return funcRepo.getPointsData(funcId);
    }

    public CompletableFuture<Void> updateComposite(int funcId, Integer newInner, Integer newOuter) {
        return funcRepo.updateComposite(funcId, newInner, newOuter);
    }

    public CompletableFuture<Void> updatePoint(int funcId, double xValue, double newY) {
        return funcRepo.updatePoint(funcId, xValue, newY);
    }
}
