package ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces;

import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.*;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionOwnershipEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface FunctionOwnershipService {
    void create(FunctionOwnershipRequest request);

    OwnedFunctionResponse getOwnership(Long userId, Long functionId);

    FunctionOwnershipEntity getOwnershipDb(Long userId, Long functionId);

    void deleteOwnership(Long userId, Long functionId);

    List<FunctionOwnershipEntity> getAllOwnerships();

    List<OwnedFunctionResponse> getOwnershipsByUserId(Long userId);

    Optional<UserEntity> getOwnerByFunctionId(Long functionId);

    List<FunctionEntity> getUserFunctionsOrderedByDate(Long userId, Boolean descending);

    void updateOwnership(Long userId, Long functionId, FunctionOwnershipRequest request);

    Long createOwnedMath(OwnedFunctionCreateRequest<MathFunctionRequest> request, Long userId);

    Long createOwnedTabulated(OwnedFunctionCreateRequest<TabulatedFunctionRequest> request, Long userId);

    Long createOwnedPure(OwnedFunctionCreateRequest<PureTabulatedRequest> request, Long userId);

    Long createOwnedComposite(OwnedFunctionCreateRequest<CompositeFunctionRequest> request, Long userId);

    void addExistingFunctionToUser(Long userId, Long functionId, String funcName);
}
