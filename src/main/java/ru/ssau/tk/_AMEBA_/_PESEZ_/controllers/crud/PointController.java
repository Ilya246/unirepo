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
    public PointResponse createPoint(@RequestBody @Valid PointRequest request) {
        return pointsService.createPoint(request);
    }

    @GetMapping("/function/{functionId}/x/{xValue}")
    @Operation(summary = "Получение точки по ID функции и значению X")
    public PointResponse getPoint(
            @PathVariable Long functionId,
            @PathVariable Double xValue) {
        return pointsService.getPoint(functionId, xValue);
    }

    @PutMapping("/function/{functionId}/x/{xValue}")
    @Operation(summary = "Обновление точки")
    public PointResponse updatePoint(
            @PathVariable Long functionId,
            @PathVariable Double xValue,
            @RequestBody @Valid PointRequest request) {
        return pointsService.updatePoint(functionId, xValue, request);
    }

    @DeleteMapping("/function/{functionId}/x/{xValue}")
    @Operation(summary = "Удаление точки")
    public void deletePoint(
            @PathVariable Long functionId,
            @PathVariable Double xValue) {
        pointsService.deletePoint(functionId, xValue);
    }

    @DeleteMapping("/function/{functionId}")
    @Operation(summary = "Удаление всех точек функции")
    public void deletePointsByFunction(@PathVariable Long functionId) {
        pointsService.deletePointsByFunction(functionId);
    }

    @GetMapping("/function/{functionId}")
    @Operation(summary = "Получение всех точек функции")
    public List<PointResponse> getPointsByFunction(@PathVariable Long functionId) {
        return pointsService.getPointsByFunction(functionId);
    }

    @GetMapping("/function/{functionId}/count")
    @Operation(summary = "Получение количества точек функции")
    public Long countPointsByFunction(@PathVariable Long functionId) {
        return pointsService.countPointsByFunction(functionId);
    }

    @GetMapping
    @Operation(summary = "Получение всех точек")
    public List<PointResponse> getAllPoints() {
        return pointsService.getAllPoints();
    }
}
