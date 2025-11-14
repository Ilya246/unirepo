package ru.ssau.tk._AMEBA_._PESEZ_.controllers.crud;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.CompositeFunctionResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.FunctionResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.MathFunctionResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.TabulatedFunctionResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.MathFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.FunctionService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/functions")
@RequiredArgsConstructor
public class FunctionController {
    private final FunctionService functionService;

    // Базовые CRUD операции
    @PostMapping
    @Operation(summary = "Создание функции")
    public FunctionResponse createFunction(@RequestBody @Valid FunctionRequest request) {
        return functionService.createFunction(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение функции по ID")
    public FunctionResponse getFunction(@PathVariable Long id) {
        return functionService.getFunction(id);
    }

    // Специализированные операции
    @PostMapping("/math")
    @Operation(summary = "Создание математической функции")
    public MathFunctionResponse createMathFunction(@RequestBody @Valid MathFunctionRequest request) {
        return functionService.createMathFunction(request);
    }

    @PostMapping("/tabulated")
    @Operation(summary = "Создание табулированной функции")
    public TabulatedFunctionResponse createTabulatedFunction(@RequestBody @Valid TabulatedFunctionRequest request) {
        return functionService.createTabulatedFunction(request);
    }


    @PostMapping("/composite")
    @Operation(summary = "Создание композитной функции")
    public CompositeFunctionResponse createCompositeFunction(@RequestBody @Valid CompositeFunctionRequest request) {
        return functionService.createCompositeFunction(request);
    }


    @GetMapping("/{id}/calculate")
    @Operation(summary = "Вычисление значения функции в точке")
    public Double calculateFunction(@PathVariable Long id, @RequestParam Double x) {
        return functionService.calculateFunction(id, x);
    }

    // Операции с точками
    @PutMapping("/{id}/points")
    @Operation(summary = "Обновление точки табулированной функции")
    public void updatePoint(@PathVariable Long id, @RequestParam Double x, @RequestParam Double y) {
        functionService.updatePoint(id, x, y);
    }

    @DeleteMapping("/{id}/points")
    @Operation(summary = "Удаление точки табулированной функции")
    public void deletePoint(@PathVariable Long id, @RequestParam Double x) {
        functionService.deletePoint(id, x);
    }

}
