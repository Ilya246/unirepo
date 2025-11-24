package ru.ssau.tk._AMEBA_._PESEZ_.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.FunctionResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.PointsResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.enums.FunctionType;
import ru.ssau.tk._AMEBA_._PESEZ_.exceptions.CustomException;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.MathFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.FunctionService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FunctionServiceImpl implements FunctionService {
    private final FunctionRepository functionRepo;

    @Override
    public FunctionResponse getFunction(Long id) {
        FunctionEntity function = getFunctionDb(id);
        return convertToResponse(function);
    }

    @Override
    public FunctionEntity getFunctionDb(Long id) {
        FunctionEntity function = functionRepo.findById(id);
        if (function == null) {
            throw new CustomException("Function not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        return function;
    }

    @Override
    public Object getFunction1(Long id) {
        if (id == null) {
            return getAllFunctions();
        }
        FunctionEntity function = getFunctionDb(id);
        return convertToResponse(function);
    }

    @Override
    public FunctionEntity getFunctionDb1(Long id) {
        FunctionEntity function = functionRepo.findById(id);
        if (function == null) {
            throw new CustomException("Function not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        return function;
    }

    @Override
    public List<FunctionResponse> getAllFunctions() {
        List<FunctionEntity> functions = functionRepo.findAll();
        return functions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }


/*    @Override
    public FunctionResponse getAllFunctions() {
        List<FunctionEntity> functions = functionRepo.findAll();
        return (FunctionResponse) functions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }*/

    @Override
    public List<FunctionResponse> getFunctionsByType(Integer typeId) {
        List<FunctionEntity> functions = functionRepo.findByType(typeId);
        return functions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Специализированные операции
    @Override
    public Long createMathFunction(MathFunctionRequest request) {
        try {
            return functionRepo.createMathFunction(request.getExpression()).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error creating math function: {}", e.getMessage());
            throw new CustomException("Failed to create math function: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Long createTabulatedFunction(TabulatedFunctionRequest request) {
        try {
            return functionRepo.createTabulated(
                    request.getExpression(),
                    request.getFrom(),
                    request.getTo(),
                    request.getPointCount()
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error creating tabulated function: {}", e.getMessage());
            throw new CustomException("Failed to create tabulated function: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Long createPureTabulatedFunction(PureTabulatedRequest request) {
        try {
            return functionRepo.createPureTabulated(request.getXValues(), request.getYValues()).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error creating pure tabulated function: {}", e.getMessage());
            throw new CustomException("Failed to create pure tabulated function: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Long createCompositeFunction(CompositeFunctionRequest request) {
        try {
            Long innerFuncId = request.getInnerFunctionId();
            Long outerFuncId = request.getOuterFunctionId();

            Long compositeFuncId = functionRepo.createComposite(innerFuncId, outerFuncId).get();

            /*FunctionEntity compositeFunction = getFunctionDb(compositeFuncId);
            FunctionEntity innerFunction = getFunctionDb(innerFuncId);
            FunctionEntity outerFunction = getFunctionDb(outerFuncId);*/

            log.info("Composite function created with id: {}", compositeFuncId);
            return compositeFuncId;

        } catch (InterruptedException | ExecutionException e) {
            log.error("Error creating composite function: {}", e.getMessage());
            throw new CustomException("Failed to create composite function: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public CompletableFuture<MathFunction> getMathFunction(Long funcId) {
        return functionRepo.getFunction(funcId);
    }

    @Override
    public Double calculateFunction(Long funcId, Double xValue) {
        try {
            MathFunction mathFunction = getMathFunction(funcId).get();
            return mathFunction.apply(xValue);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error calculating function: {}", e.getMessage());
            throw new CustomException("Failed to calculate function: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public PointsResponse calculateFunctionRange(Long id, Double from, Double to, Long points) {
        try {
            MathFunction mathFunction = getMathFunction(id).get();
            var xValues = new double[points.intValue()];
            var yValues = new double[points.intValue()];
            double step = (to - from) / points;
            for (int i = 0; i < points; i++) {
                double x = from + step * i;
                xValues[i] = x;
                yValues[i] = mathFunction.apply(x);
            }
            return new PointsResponse(id, xValues, yValues);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error calculating function: {}", e.getMessage());
            throw new CustomException("Failed to calculate function: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void updatePoint(Long funcId, Double xValue, Double newYValue) {
        try {
            functionRepo.updatePoint(funcId, xValue, newYValue).get();
            log.info("Point updated for function {}: x={}, y={}", funcId, xValue, newYValue);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error updating point: {}", e.getMessage());
            throw new CustomException("Failed to update point: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void deletePoint(Long funcId, Double xValue) {
        try {
            functionRepo.deletePoint(funcId, xValue).get();
            log.info("Point deleted for function {}: x={}", funcId, xValue);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error deleting point: {}", e.getMessage());
            throw new CustomException("Failed to delete point: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private FunctionResponse convertToResponse(FunctionEntity function) {
        return new FunctionResponse(function.getFuncId(), FunctionType.fromEntity(function), function.getExpression());
    }
}