package ru.ssau.tk._AMEBA_._PESEZ_.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.PointRequest;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.PointsResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.PointsEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.exceptions.CustomException;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.PointsRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.PointService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {
    private final PointsRepository pointsRepo;
    private final FunctionRepository functionRepo;

    @Override
    public void createPoint(PointRequest request) {
        // Находим функцию и проверяем, что она существует
        FunctionEntity function = functionRepo.findById(request.getFunctionId());
        if (function == null) {
            throw new CustomException("Function not found with id: " + request.getFunctionId(), HttpStatus.NOT_FOUND);
        }

        // Проверяем, что поля не null
        if (request.getXValue() == null) {

            throw new CustomException("xValue cannot be null", HttpStatus.BAD_REQUEST);
        }
        if (request.getYValue() == null) {

            throw new CustomException("yValue cannot be null", HttpStatus.BAD_REQUEST);
        }

        // Проверяем, не существует ли уже точка с таким xValue для этой функции
        pointsRepo.findById(function, request.getXValue())
                .ifPresent(point -> {
                    throw new CustomException("Point already exists for function " + request.getFunctionId() + " with x=" + request.getXValue(), HttpStatus.CONFLICT);
                });

        // Создаем новую точку
        PointsEntity point = new PointsEntity(function, request.getXValue(), request.getYValue());

        pointsRepo.save(point);
    }

    @Override
    public void updatePoint(Long functionId, Double xValue, PointRequest request) {
        FunctionEntity function = functionRepo.findById(functionId);
        if (function == null) {
            throw new CustomException("Function not found with id: " + functionId, HttpStatus.NOT_FOUND);
        }

        pointsRepo.findById(function, xValue)
                .orElseThrow(() -> new CustomException("Point not found for function " + functionId + " with x=" + xValue, HttpStatus.NOT_FOUND));

        pointsRepo.updateById(functionId, xValue, request.getYValue());

        log.info("Point updated for function {}: x={}, new y={}", functionId, xValue, request.getYValue());
    }

    @Override
    public void deletePoint(Long functionId, Double xValue) {
        FunctionEntity function = functionRepo.findById(functionId);
        if (function == null) {
            throw new CustomException("Function not found with id: " + functionId, HttpStatus.NOT_FOUND);
        }

        pointsRepo.deleteById(functionId, xValue);
        log.info("Point deleted for function {}: x={}", functionId, xValue);
    }

    @Override
    public PointsResponse getPointsByFunction(Long functionId) {
        FunctionEntity function = functionRepo.findById(functionId);
        if (function == null) {
            throw new CustomException("Function not found with id: " + functionId, HttpStatus.NOT_FOUND);
        }

        List<PointsEntity> points = pointsRepo.findByFunction(function);
        log.info("Found {} points for function: {}", points.size(), functionId);
        var xValues = new double[points.size()];
        var yValues = new double[points.size()];
        for (int i = 0; i < xValues.length; i++) {
            xValues[i] = points.get(i).getXValue();
            yValues[i] = points.get(i).getYValue();
        }

        return new PointsResponse(functionId, xValues, yValues);
    }
}