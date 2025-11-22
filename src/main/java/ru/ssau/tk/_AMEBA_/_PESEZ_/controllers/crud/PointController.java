package ru.ssau.tk._AMEBA_._PESEZ_.controllers.crud;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.PointRequest;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.PointsResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.PointService;

import java.util.Map;

@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
public class PointController {
    private final PointService pointsService;

    @PostMapping
    @Operation(summary = "Создание точки")
    public Map<String, Object> createPoint(
            @RequestParam Long id,
            @RequestParam Double x,
            @RequestParam Double y) {
        pointsService.createPoint(new PointRequest(id, x, y));
        return Map.of(
                "success", true,
                "message", "Point created successfully",
                "functionId", id,
                "x", x,
                "y", y
        );
    }


    @PutMapping
    @Operation(summary = "Обновление точки")
    public void updatePoint(
            @RequestParam Long id,
            @RequestParam Double x,
            @RequestParam Double y) {
        pointsService.updatePoint(id, x, new PointRequest(id, x, y));
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
    public PointsResponse getPointsByFunction(@RequestParam Long id) {
        return pointsService.getPointsByFunction(id);
    }


}
