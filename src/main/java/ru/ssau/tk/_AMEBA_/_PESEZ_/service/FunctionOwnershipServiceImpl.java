package ru.ssau.tk._AMEBA_._PESEZ_.service;

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
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.FunctionOwnershipService;

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

    @Override
    public FunctionOwnershipResponse create(FunctionOwnershipRequest request) {
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
        FunctionOwnershipEntity ownership = new FunctionOwnershipEntity();
        ownership.setUser(user);
        ownership.setFunction(function);
        ownership.setFuncName(request.getFuncName());
        ownership.setCreatedDate(new Date()); // устанавливаем текущую дату

        // Создаем и устанавливаем composite ID
        FunctionOwnershipId id = new FunctionOwnershipId(request.getUserId(), request.getFunctionId());
        ownership.setId(id);

        ownershipRepo.save(ownership);
        log.info("Function ownership created for user: {}, function: {}",
                request.getUserId(), request.getFunctionId());

        return convertToResponse(ownership);
    }

    @Override
    public FunctionOwnershipResponse getOwnership(Long userId, Long functionId) {
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
        FunctionOwnershipEntity ownership = getOwnershipDb(userId, functionId);
        ownershipRepo.deleteById(userId, functionId);
        log.info("Function ownership deleted for user: {}, function: {}", userId, functionId);
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

        // Обновляем только funcName, если он предоставлен
        if (request.getFuncName() != null) {
            ownership.setFuncName(request.getFuncName());
            ownershipRepo.updateById(userId, functionId, request.getFuncName());
            log.info("Function ownership updated for user: {}, function: {}", userId, functionId);
        }

        return convertToResponse(ownership);
    }

    // Ручное преобразование Entity в Response
    private FunctionOwnershipResponse convertToResponse(FunctionOwnershipEntity ownership) {
        FunctionOwnershipResponse response = new FunctionOwnershipResponse();
        response.setUserId(ownership.getUser().getUserId());
        response.setFunctionId(ownership.getFunction().getFuncId());
        response.setFuncName(ownership.getFuncName());
        return response;
    }
}