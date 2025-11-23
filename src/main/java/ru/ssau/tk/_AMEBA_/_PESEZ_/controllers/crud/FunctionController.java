package ru.ssau.tk._AMEBA_._PESEZ_.controllers.crud;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.CompositeFunctionResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.*;

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
    public Long createMathFunction(@RequestBody @Valid MathFunctionRequest request) {
        return functionService.createMathFunction(request);
    }

    @PostMapping("/tabulated")
    @Operation(summary = "Создание табулированной функции")
    public Long createTabulatedFunction(@RequestBody @Valid TabulatedFunctionRequest request) {
        return functionService.createTabulatedFunction(request);
    }


    @PostMapping("/composite")
    @Operation(summary = "Создание композитной функции")
    public Long createCompositeFunction(@RequestBody @Valid CompositeFunctionRequest request) {
        return functionService.createCompositeFunction(request);
    }

    @PostMapping("/pure-tabulated")
    @Operation(summary = "Создание табулированной функции из готовых массивов значений")
    public Long createPureTabulatedFunction(@RequestBody @Valid PureTabulatedRequest request) {
        return functionService.createPureTabulatedFunction(request);
    }

    @GetMapping("/calculate")
    @Operation(summary = "Вычисление значения функции в точке")
    public Double calculateFunction(@RequestParam Long id, @RequestParam Double x) {
        return functionService.calculateFunction(id, x);
    }

    @GetMapping("/composite")
    @Operation(summary = "Получение информации о композитной функции")
    public CompositeFunctionResponse getComposite(@RequestParam Long id) {
        return compositeService.getFunction(id);
    }
}
