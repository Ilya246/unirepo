package ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces;

import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.CompositeFunctionResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.FunctionResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.MathFunctionResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.TabulatedFunctionResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.MathFunction;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface FunctionService {
    FunctionResponse createFunction(FunctionRequest request);

    FunctionResponse getFunction(Long id);

    FunctionEntity getFunctionDb(Long id);

    List<FunctionResponse> getAllFunctions();

    List<FunctionResponse> getFunctionsByType(Integer typeId);

    // Специализированные операции
    MathFunctionResponse createMathFunction(MathFunctionRequest request);

    TabulatedFunctionResponse createTabulatedFunction(TabulatedFunctionRequest request);

    FunctionResponse createPureTabulatedFunction(PureTabulatedRequest request);

    CompositeFunctionResponse createCompositeFunction(CompositeFunctionRequest request);

    CompletableFuture<MathFunction> getMathFunction(Long funcId);

    Double calculateFunction(Long funcId, Double xValue);

    void updatePoint(Long funcId, Double xValue, Double newYValue);

    void deletePoint(Long funcId, Double xValue);

}
