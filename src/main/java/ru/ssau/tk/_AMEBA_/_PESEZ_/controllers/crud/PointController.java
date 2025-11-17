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

    @PostMapping("?id={id}&x={x}&y={y}")
    @Operation(summary = "Создание точки")
    public PointResponse createPoint(@RequestBody @Valid PointRequest request) {
        return pointsService.createPoint(request);
    }

    @PutMapping("?id={id}&x={x}&y={y}")
    @Operation(summary = "Обновление точки")
    public PointResponse updatePoint(
            @PathVariable Long functionId,
            @PathVariable Double xValue,
            @RequestBody @Valid PointRequest request) {
        return pointsService.updatePoint(functionId, xValue, request);
    }

    @DeleteMapping("?id={id}&x={x}")
    @Operation(summary = "Удаление точки")
    public void deletePoint(
            @PathVariable Long functionId,
            @PathVariable Double xValue) {
        pointsService.deletePoint(functionId, xValue);
    }

    @GetMapping("?id={id}")
    @Operation(summary = "Получение всех точек функции")
    public List<PointResponse> getPointsByFunction(@PathVariable Long functionId) {
        return pointsService.getPointsByFunction(functionId);
    }


}
