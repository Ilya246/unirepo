package ru.ssau.tk._AMEBA_._PESEZ_.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.FunctionOwnershipRequest;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.FunctionOwnershipResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionOwnershipEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionOwnershipId;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.exceptions.CustomException;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionOwnershipRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.FunctionOwnershipService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FunctionOwnershipServiceImpl implements FunctionOwnershipService {
    private final FunctionOwnershipRepository ownershipRepo;
    private final ObjectMapper mapper;

    @Override
    public FunctionOwnershipResponse create(FunctionOwnershipRequest request) {
        FunctionOwnershipEntity ownership = mapper.convertValue(request, FunctionOwnershipEntity.class);

        FunctionOwnershipId id = new FunctionOwnershipId(request.getUserId(), request.getFunctionId());
        ownership.setId(id);
        ownershipRepo.save(ownership);
        log.info("Function ownership created for user: {}, function: {}",
                request.getUserId(), request.getFunctionId());

        return mapper.convertValue(ownership, FunctionOwnershipResponse.class);
    }

    @Override
    public FunctionOwnershipResponse getOwnership(Long userId, Long functionId) {
        return mapper.convertValue(getOwnershipDb(userId, functionId), FunctionOwnershipResponse.class);
    }

    @Override
    public FunctionOwnershipEntity getOwnershipDb(Long userId, Long functionId) {
        return ownershipRepo.findById(userId, functionId)
                .orElseThrow(() -> new CustomException(
                        "Function ownership not found for user: " + userId + " and function: " + functionId,
                        HttpStatus.NOT_FOUND
                ));
    }
    @Override
    public void deleteOwnership(Long userId, Long functionId) {
        FunctionOwnershipEntity ownership = getOwnershipDb(userId, functionId);
        if (ownership.getId() != null) {
            ownershipRepo.deleteById(userId, functionId);
            log.info("Function ownership deleted for user: {}, function: {}", userId, functionId);
        } else {
            log.error("Function ownership not found for deletion");
            throw new CustomException("Function ownership not found", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public List<FunctionOwnershipEntity> getAllOwnerships() {
        return ownershipRepo.findAll();
    }

    @Override
    public List<FunctionOwnershipEntity> getOwnershipsByUserId(Long userId) {
        List<FunctionOwnershipEntity> ownerships = ownershipRepo.findByUserId(userId);
        log.info("Found {} function ownerships for user: {}", ownerships.size(), userId);
        return ownerships;
    }

    @Override
    public Optional<UserEntity> getOwnerByFunctionId(Long functionId) {
        Optional<UserEntity> owner = ownershipRepo.findOwnerByFunctionId(functionId);
        log.info("Found owner for function {}: {}", functionId, owner.isPresent() ? "yes" : "no");
        return owner;
    }

    @Override
    public List<FunctionEntity> getUserFunctionsOrderedByDate(Long userId, Boolean descending) {
        boolean sortDescending = descending != null ? descending : true;
        List<FunctionEntity> functions = ownershipRepo.findUserFunctionsOrderByDate(userId, sortDescending);
        log.info("Found {} functions for user: {} sorted by date {}",
                functions.size(), userId, sortDescending ? "DESC" : "ASC");
        return functions;
    }

    @Override
    public FunctionOwnershipResponse updateOwnership(Long userId, Long functionId, FunctionOwnershipRequest request) {
        FunctionOwnershipEntity ownership = getOwnershipDb(userId, functionId);

        if (ownership.getId() != null) {
            ownership.setFuncName(request.getFuncName() == null ? ownership.getFuncName() : request.getFuncName());

            ownershipRepo.updateById(userId, functionId, ownership.getFuncName());
            log.info("Function ownership updated for user: {}, function: {}", userId, functionId);
        } else {
            log.error("Function ownership not found for update");
            throw new CustomException("Function ownership not found", HttpStatus.NOT_FOUND);
        }

        return mapper.convertValue(ownership, FunctionOwnershipResponse.class);
    }

}
