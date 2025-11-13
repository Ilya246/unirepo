package ru.ssau.tk._AMEBA_._PESEZ_.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.PointRequest;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.PointResponse;
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
    public PointResponse createPoint(PointRequest request) {
        FunctionEntity function = functionRepo.findById(request.getFunctionId());
        if (function == null) {
            throw new CustomException("Function not found with id: " + request.getFunctionId(), HttpStatus.NOT_FOUND);
        }

        pointsRepo.findById(function, request.getXValue())
                .ifPresent(point -> {
                    throw new CustomException("Point already exists for function " + request.getFunctionId() + " with x=" + request.getXValue(), HttpStatus.CONFLICT);
                });

        PointsEntity point = new PointsEntity(function, request.getXValue(), request.getYValue());
        pointsRepo.save(point);

        log.info("Point created for function {}: x={}, y={}", request.getFunctionId(), request.getXValue(), request.getYValue());
        return convertToResponse(point);
    }

    @Override
    public PointResponse getPoint(Long functionId, Double xValue) {
        FunctionEntity function = functionRepo.findById(functionId);
        if (function == null) {
            throw new CustomException("Function not found with id: " + functionId, HttpStatus.NOT_FOUND);
        }

        PointsEntity point = pointsRepo.findById(function, xValue)
                .orElseThrow(() -> new CustomException("Point not found for function " + functionId + " with x=" + xValue, HttpStatus.NOT_FOUND));

        return convertToResponse(point);
    }

    @Override
    public PointResponse updatePoint(Long functionId, Double xValue, PointRequest request) {
        FunctionEntity function = functionRepo.findById(functionId);
        if (function == null) {
            throw new CustomException("Function not found with id: " + functionId, HttpStatus.NOT_FOUND);
        }

        pointsRepo.findById(function, xValue)
                .orElseThrow(() -> new CustomException("Point not found for function " + functionId + " with x=" + xValue, HttpStatus.NOT_FOUND));

        pointsRepo.updateById(functionId, xValue, request.getYValue());

        log.info("Point updated for function {}: x={}, new y={}", functionId, xValue, request.getYValue());

        return PointResponse.builder()
                .functionId(functionId)
                .xValue(xValue)
                .yValue(request.getYValue())
                .build();
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
    public void deletePointsByFunction(Long functionId) {
        FunctionEntity function = functionRepo.findById(functionId);
        if (function == null) {
            throw new CustomException("Function not found with id: " + functionId, HttpStatus.NOT_FOUND);
        }

        pointsRepo.deleteByFunction(function);
        log.info("All points deleted for function: {}", functionId);
    }

    @Override
    public List<PointResponse> getPointsByFunction(Long functionId) {
        FunctionEntity function = functionRepo.findById(functionId);
        if (function == null) {
            throw new CustomException("Function not found with id: " + functionId, HttpStatus.NOT_FOUND);
        }

        List<PointsEntity> points = pointsRepo.findByFunction(function);
        log.info("Found {} points for function: {}", points.size(), functionId);

        return points.stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public Long countPointsByFunction(Long functionId) {
        FunctionEntity function = functionRepo.findById(functionId);
        if (function == null) {
            throw new CustomException("Function not found with id: " + functionId, HttpStatus.NOT_FOUND);
        }

        Long count = pointsRepo.countByFunction(function);
        log.info("Counted {} points for function: {}", count, functionId);
        return count;
    }

    @Override
    public List<PointResponse> getAllPoints() {
        List<PointsEntity> points = pointsRepo.findAll();
        log.info("Found {} points in total", points.size());

        return points.stream()
                .map(this::convertToResponse)
                .toList();
    }

    private PointResponse convertToResponse(PointsEntity point) {
        return PointResponse.builder()
                .functionId(point.getId().getFunctionId())
                .xValue(point.getId().getXValue())
                .yValue(point.getYValue())
                .build();
    }
}
