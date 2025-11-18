package ru.ssau.tk._AMEBA_._PESEZ_.controllers.crud;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.PointRequest;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.PointResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.PointService;

import java.util.List;

@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
public class PointController {
    private final PointService pointsService;

    @PostMapping
    @Operation(summary = "Создание точки")
    public PointResponse createPoint(
            @RequestParam Long id,
            @RequestParam Double x,
            @RequestParam Double y,
            @RequestBody @Valid PointRequest request) {
        return pointsService.createPoint(request);
    }


    @PutMapping
    @Operation(summary = "Обновление точки")
    public PointResponse updatePoint(
            @RequestParam Long id,
            @RequestParam Double x,
            @RequestParam Double y) {
        return pointsService.updatePoint(id, x, new PointRequest(id, x, y));
    }

    @DeleteMapping
    @Operation(summary = "Удаление точки")
    public void deletePoint(
            @RequestParam Long id,
            @RequestParam Double x) {
        pointsService.deletePoint(id, x);
    }

    @GetMapping
    @Operation(summary = "Получение всех точек функции")
    public List<PointResponse> getPointsByFunction(@RequestParam Long id) {
        return pointsService.getPointsByFunction(id);
    }


}
