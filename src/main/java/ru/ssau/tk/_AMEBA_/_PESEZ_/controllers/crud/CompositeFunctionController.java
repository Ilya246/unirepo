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
@RequestMapping("/composite-functions")
@RequiredArgsConstructor
public class CompositeFunctionController {
    private final CompositeFunctionService compositeFunctionService;

    @GetMapping("?id={id}")
    @Operation(summary = "Получение композитной функции по ID")
    public CompositeFunctionResponse getFunction(@PathVariable Long id) {
        return compositeFunctionService.getFunction(id);
    }

}
