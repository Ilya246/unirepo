package ru.ssau.tk._AMEBA_._PESEZ_.controllers.crud;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.CompositeFunctionRequest;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.CompositeFunctionResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.CompositeFunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.CompositeFunctionService;

import java.util.List;

@RestController
@RequestMapping("/composite")
@RequiredArgsConstructor
public class CompositeFunctionController {
    private final CompositeFunctionService compositeFunctionService;
    @PostMapping
    @Operation(summary = "Создание композитной функции")
    public CompositeFunctionResponse create(@RequestBody @Valid CompositeFunctionRequest request) {
        return compositeFunctionService.create(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение композитной функции по ID")
    public CompositeFunctionResponse getFunction(@PathVariable Long id) {
        return compositeFunctionService.getFunction(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Редактирование композитной функции")
    public CompositeFunctionResponse update(@PathVariable Long id, @RequestBody @Valid CompositeFunctionRequest request) {
        return compositeFunctionService.update(id, request);
    }

    @GetMapping
    @Operation(summary = "Получение всех композитных функций")
    public List<CompositeFunctionEntity> getAllFunctions() {
        return compositeFunctionService.getAllFunctions();
    }
}
