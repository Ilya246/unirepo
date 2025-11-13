package ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces;

import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.PointRequest;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.PointResponse;

import java.util.List;

public interface PointService {
    PointResponse createPoint(PointRequest request);

    PointResponse getPoint(Long functionId, Double xValue);

    PointResponse updatePoint(Long functionId, Double xValue, PointRequest request);

    void deletePoint(Long functionId, Double xValue);

    void deletePointsByFunction(Long functionId);

    List<PointResponse> getPointsByFunction(Long functionId);

    Long countPointsByFunction(Long functionId);

    List<PointResponse> getAllPoints();
}
