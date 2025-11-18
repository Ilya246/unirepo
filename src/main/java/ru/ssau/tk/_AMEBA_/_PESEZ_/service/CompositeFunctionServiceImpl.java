package ru.ssau.tk._AMEBA_._PESEZ_.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.CompositeFunctionRequest;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.CompositeFunctionResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.CompositeFunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.exceptions.CustomException;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.CompositeFunctionRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.CompositeFunctionService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompositeFunctionServiceImpl implements CompositeFunctionService {
    private final CompositeFunctionRepository compositeFunctionRepo;
    private final FunctionRepository functionRepository;

    @Override
    public List<CompositeFunctionEntity> getAllFunctions() {
        return compositeFunctionRepo.findAll();
    }

    @Override
    public CompositeFunctionResponse getFunction(Long id) {
        CompositeFunctionEntity function = getFunctionDb(id);
        return convertToResponse(function);
    }

    @Override
    public CompositeFunctionEntity getFunctionDb(Long id) {
        return compositeFunctionRepo.findById(id)
                .orElseThrow(() -> new CustomException("Composite function is not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public CompositeFunctionResponse update(Long id, CompositeFunctionRequest request) {
        CompositeFunctionEntity function = getFunctionDb(id);

        // Обновляем только не-null поля из запроса
        if (request.getInnerFunctionId() != null) {
            function.setInnerFunction(functionRepository.findById(request.getInnerFunctionId()));
        }
        if (request.getOuterFunctionId() != null) {
            function.setOuterFunction(functionRepository.findById(request.getOuterFunctionId()));
        }

        function = compositeFunctionRepo.update(function);
        log.info("Composite function updated with id: {}", id);

        return convertToResponse(function);
    }

    // Ручное преобразование Entity в Response (аналогично FunctionServiceImpl)
    private CompositeFunctionResponse convertToResponse(CompositeFunctionEntity function) {
        CompositeFunctionResponse response = new CompositeFunctionResponse();
        response.setCompositeFunctionId(function.getCompositeFunction().getFuncId());
        response.setInnerFunctionId(function.getInnerFunction().getFuncId());
        response.setOuterFunctionId(function.getOuterFunction().getFuncId());
        return response;
    }
}