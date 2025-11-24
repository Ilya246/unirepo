package ru.ssau.tk._AMEBA_._PESEZ_.controllers.crud;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.CompositeFunctionResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.IdResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.PointsResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.ResultResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.*;

import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.Log;

@RestController
@RequestMapping("/functions")
@RequiredArgsConstructor
public class FunctionController {
    private final FunctionService functionService;
    private final CompositeFunctionService compositeService;

    @GetMapping
    @Operation(summary = "Получение функции по ID или всех функций")
    public Object getFunction(@RequestParam(required = false) Long id) {
        return functionService.getFunction1(id);
    }

    // Специализированные операции
    @PostMapping("/math")
    @Operation(summary = "Создание математической функции")
    public IdResponse createMathFunction(@RequestBody @Valid MathFunctionRequest request) {
        return new IdResponse(functionService.createMathFunction(request));
    }

    @PostMapping("/tabulated")
    @Operation(summary = "Создание табулированной функции")
    public IdResponse createTabulatedFunction(@RequestBody @Valid TabulatedFunctionRequest request) {
        return new IdResponse(functionService.createTabulatedFunction(request));
    }


    @PostMapping("/composite")
    @Operation(summary = "Создание композитной функции")
    public IdResponse createCompositeFunction(@RequestBody @Valid CompositeFunctionRequest request) {
        return new IdResponse(functionService.createCompositeFunction(request));
    }

    @PostMapping("/pure-tabulated")
    @Operation(summary = "Создание табулированной функции из готовых массивов значений")
    public IdResponse createPureTabulatedFunction(@RequestBody @Valid PureTabulatedRequest request) {
        return new IdResponse(functionService.createPureTabulatedFunction(request)) ;
    }

    @GetMapping("/calculate")
    @Operation(summary = "Вычисление значения функции в точке")
    public ResultResponse calculateFunction(@RequestParam Long id, @RequestParam Double x) {
        Log.info("Calculating function {} at x={}", id, x);
        try {
            Double result = functionService.calculateFunction(id, x);
            Log.info("Calculation result: {}", result);
            return new ResultResponse(result);
        } catch (Exception e) {
            Log.error("Error calculating function: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/calculaterange")
    @Operation(summary = "Вычисление значения функции в точке")
    public PointsResponse calculateFunction(@RequestParam Long id,
                                            @RequestParam Double from,
                                            @RequestParam Double to,
                                            @RequestParam("pts") Long points) {
        Log.info("Calculating function {} in range {}, {} with {} points", id, from, to, points);
        try {
            PointsResponse result = functionService.calculateFunctionRange(id, from, to, points);
            return result;
        } catch (Exception e) {
            Log.error("Error calculating function: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/composite")
    @Operation(summary = "Получение информации о композитной функции")
    public CompositeFunctionResponse getComposite(@RequestParam Long id) {
        return compositeService.getFunction(id);
    }
}
