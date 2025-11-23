package ru.ssau.tk._AMEBA_._PESEZ_.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.*;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionOwnershipEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionOwnershipId;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.exceptions.CustomException;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionOwnershipRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.FunctionOwnershipService;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.FunctionService;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FunctionOwnershipServiceImpl implements FunctionOwnershipService {
    private final FunctionOwnershipRepository ownershipRepo;
    private final UserRepository userRepo;
    private final FunctionRepository functionRepo;
    private final FunctionService functionService;

    @Override
    public void create(FunctionOwnershipRequest request) {
        // Валидация входных данных
        if (request.getUserId() == null) {
            throw new CustomException("User ID is required", HttpStatus.BAD_REQUEST);
        }
        if (request.getFunctionId() == null) {
            throw new CustomException("Function ID is required", HttpStatus.BAD_REQUEST);
        }

        // Находим пользователя и функцию
        UserEntity user = userRepo.findById(request.getUserId());

        FunctionEntity function = functionRepo.findById(request.getFunctionId());

        // Проверяем, не существует ли уже такая связь
        Optional<FunctionOwnershipEntity> existingOwnership = ownershipRepo.findById(request.getUserId(), request.getFunctionId());
        if (existingOwnership.isPresent()) {
            throw new CustomException("Function ownership already exists for user: " + request.getUserId() + " and function: " + request.getFunctionId(), HttpStatus.CONFLICT);
        }

        // Создаем entity вручную
        FunctionOwnershipEntity ownership = new FunctionOwnershipEntity(user, function, new Date(), request.getFuncName());
        ownershipRepo.save(ownership);
        log.info("Function ownership created for user: {}, function: {}",
                request.getUserId(), request.getFunctionId());
    }

    @Override
    public OwnedFunctionResponse getOwnership(Long userId, Long functionId) {
        return convertToResponse(getOwnershipDb(userId, functionId));
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
        ownershipRepo.deleteById(userId, functionId);
        log.info("Function ownership deleted for user: {}, function: {}", userId, functionId);
    }

    @Override
    public List<FunctionOwnershipEntity> getAllOwnerships() {
        return ownershipRepo.findAll();
    }

    @Override
    public List<OwnedFunctionResponse> getOwnershipsByUserId(Long userId) {
        List<FunctionOwnershipEntity> ownerships = ownershipRepo.findByUserId(userId);
        log.info("Found {} function ownerships for user: {}", ownerships.size(), userId);
        return ownerships.stream().map(this::convertToResponse).toList();
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
    public void updateOwnership(Long userId, Long functionId, FunctionOwnershipRequest request) {
        FunctionOwnershipEntity ownership = getOwnershipDb(userId, functionId);

        // Обновляем только funcName, если он предоставлен
        if (request.getFuncName() != null) {
            ownership.setFuncName(request.getFuncName());
            ownershipRepo.updateById(userId, functionId, request.getFuncName());
            log.info("Function ownership updated for user: {}, function: {}", userId, functionId);
        }
    }

    // Ручное преобразование Entity в Response
    private OwnedFunctionResponse convertToResponse(FunctionOwnershipEntity ownership) {
        FunctionEntity owned = ownership.getFunction();
        return new OwnedFunctionResponse(
                new FunctionResponse(owned.getFuncId(),
                        owned.getTypeId(),
                        owned.getExpression()),
                new FunctionOwnershipResponse(ownership.getUser().getUserId(),
                        owned.getFuncId(),
                        Timestamp.from(ownership.getCreatedDate().toInstant()),
                        ownership.getFuncName())
        );
    }

    @Override
    public Long createOwnedMath(OwnedFunctionCreateRequest<MathFunctionRequest> request, Long userId) {
        Long funcId = functionService.createMathFunction(request.funcParams);
        addExistingFunctionToUser(userId, funcId, request.name);
        return funcId;
    }
    @Override
    public Long createOwnedTabulated(OwnedFunctionCreateRequest<TabulatedFunctionRequest> request, Long userId) {
        Long funcId = functionService.createTabulatedFunction(request.funcParams);
        addExistingFunctionToUser(userId, funcId, request.name);
        return funcId;
    }
    @Override
    public Long createOwnedPure(OwnedFunctionCreateRequest<PureTabulatedRequest> request, Long userId) {
        Long funcId = functionService.createPureTabulatedFunction(request.funcParams);
        addExistingFunctionToUser(userId, funcId, request.name);
        return funcId;
    }
    @Override
    public Long createOwnedComposite(OwnedFunctionCreateRequest<CompositeFunctionRequest> request, Long userId) {
        Long funcId = functionService.createCompositeFunction(request.funcParams);
        addExistingFunctionToUser(userId, funcId, request.name);
        return funcId;
    }

    @Override
    public void addExistingFunctionToUser(Long userId, Long functionId, String funcName) {
        log.info("Adding existing function {} to user: {}", functionId, userId);

        // Проверяем существование функции через FunctionService
        functionService.getFunction(functionId);

        // Создаем связь с пользователем
        FunctionOwnershipRequest request = new FunctionOwnershipRequest();
        request.setUserId(userId);
        request.setFunctionId(functionId);
        request.setFuncName(funcName);
        create(request);

    }
}