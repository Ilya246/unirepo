package ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces;


import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.CompositeFunctionRequest;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.CompositeFunctionResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.CompositeFunctionEntity;

import java.util.List;

public interface CompositeFunctionService {
    CompositeFunctionResponse create(CompositeFunctionRequest request);

    List<CompositeFunctionEntity> getAllFunctions();

    CompositeFunctionResponse getFunction(Long id);

    CompositeFunctionEntity getFunctionDb(Long id);

    CompositeFunctionResponse update(Long id, CompositeFunctionRequest request);
}
