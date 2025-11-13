package ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces;

import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.FunctionOwnershipRequest;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.FunctionOwnershipResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionOwnershipEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface FunctionOwnershipService {
    FunctionOwnershipResponse create(FunctionOwnershipRequest request);

    FunctionOwnershipResponse getOwnership(Long userId, Long functionId);

    FunctionOwnershipEntity getOwnershipDb(Long userId, Long functionId);

    void deleteOwnership(Long userId, Long functionId);

    List<FunctionOwnershipEntity> getAllOwnerships();

    List<FunctionOwnershipEntity> getOwnershipsByUserId(Long userId);

    Optional<UserEntity> getOwnerByFunctionId(Long functionId);

    List<FunctionEntity> getUserFunctionsOrderedByDate(Long userId, Boolean descending);

    FunctionOwnershipResponse updateOwnership(Long userId, Long functionId, FunctionOwnershipRequest request);
}
