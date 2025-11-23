package ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces;

import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.PointRequest;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.PointsResponse;

public interface PointService {
    void createPoint(PointRequest request);

    void updatePoint(Long functionId, Double xValue, PointRequest request);

    void deletePoint(Long functionId, Double xValue);

    PointsResponse getPointsByFunction(Long functionId);
}
