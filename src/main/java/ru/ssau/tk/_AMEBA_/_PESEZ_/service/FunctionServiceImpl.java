package ru.ssau.tk._AMEBA_._PESEZ_.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.CompositeFunctionResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.FunctionResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.MathFunctionResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.TabulatedFunctionResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
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
    private final ObjectMapper mapper;

    @Override
    public FunctionResponse createFunction(FunctionRequest request) {
        FunctionEntity function = mapper.convertValue(request, FunctionEntity.class);
        function.setExpression(request.getExpression());
        function.setTypeId(request.getTypeId());

        functionRepo.save(function);
        log.info("Function created with id: {}", function.getFuncId());

        return mapper.convertValue(function, FunctionResponse.class);

    }

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
    public List<FunctionResponse> getAllFunctions() {
        List<FunctionEntity> functions = functionRepo.findAll();
        return functions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FunctionResponse> getFunctionsByType(Integer typeId) {
        List<FunctionEntity> functions = functionRepo.findByType(typeId);
        return functions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Специализированные операции
    @Override
    public MathFunctionResponse createMathFunction(MathFunctionRequest request) {
        try {
            Long funcId = functionRepo.createMathFunction(request.getExpression()).get();

            FunctionEntity function = getFunctionDb(funcId);
            return MathFunctionResponse.builder()
                    .funcId(funcId)
                    .expression(function.getExpression())
                    .build();

        } catch (InterruptedException | ExecutionException e) {
            log.error("Error creating math function: {}", e.getMessage());
            throw new CustomException("Failed to create math function: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public TabulatedFunctionResponse createTabulatedFunction(TabulatedFunctionRequest request) {
        try {
            Long funcId = functionRepo.createTabulated(
                    request.getExpression(),
                    request.getFrom(),
                    request.getTo(),
                    request.getPointCount()
            ).get();

            FunctionEntity function = getFunctionDb(funcId);
            return TabulatedFunctionResponse.builder()
                    .funcId(funcId)
                    .expression(function.getExpression())
                    .from(request.getFrom())
                    .to(request.getTo())
                    .pointCount(request.getPointCount())
                    .build();

        } catch (InterruptedException | ExecutionException e) {
            log.error("Error creating tabulated function: {}", e.getMessage());
            throw new CustomException("Failed to create tabulated function: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public FunctionResponse createPureTabulatedFunction(PureTabulatedRequest request) {
        try {
            Long funcId = functionRepo.createPureTabulated(request.getXValues(), request.getYValues()).get();
            return getFunction(funcId);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error creating pure tabulated function: {}", e.getMessage());
            throw new CustomException("Failed to create pure tabulated function: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public CompositeFunctionResponse createCompositeFunction(CompositeFunctionRequest request) {
        try {
            Long innerFuncId = request.getInnerFunction().getFuncId();
            Long outerFuncId = request.getOuterFunction().getFuncId();

            Long compositeFuncId = functionRepo.createComposite(innerFuncId, outerFuncId).get();

            FunctionEntity compositeFunction = getFunctionDb(compositeFuncId);
            FunctionEntity innerFunction = getFunctionDb(innerFuncId);
            FunctionEntity outerFunction = getFunctionDb(outerFuncId);

            CompositeFunctionResponse response = new CompositeFunctionResponse();
            response.setCompositeFunction(compositeFunction);
            response.setInnerFunction(innerFunction);
            response.setOuterFunction(outerFunction);

            log.info("Composite function created with id: {}", compositeFuncId);
            return response;

        } catch (InterruptedException | ExecutionException e) {
            log.error("Error creating composite function: {}", e.getMessage());
            throw new CustomException("Failed to create composite function: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public CompletableFuture<MathFunction> getMathFunction(Long funcId) {
        return functionRepo.getFunction(funcId, true);
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
        String typeName = switch (function.getTypeId()) {
            case 1 -> "MATH";
            case 2 -> "TABULATED";
            case 3 -> "COMPOSITE";
            default -> "UNKNOWN";
        };

        return FunctionResponse.builder()
                .funcId(function.getFuncId())
                .expression(function.getExpression())
                .typeId(function.getTypeId())
                .build();
    }
}