package ru.ssau.tk._AMEBA_._PESEZ_.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.CompositeFunctionRequest;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.CompositeFunctionResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.CompositeFunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.exceptions.CustomException;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.CompositeFunctionRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.service.interfaces.CompositeFunctionService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompositeFunctionServiceImpl implements CompositeFunctionService {
    private final CompositeFunctionRepository compositeFunctionRepo;
    private final ObjectMapper mapper;

    @Override
    public CompositeFunctionResponse create(CompositeFunctionRequest request) {
        CompositeFunctionEntity function = mapper.convertValue(request, CompositeFunctionEntity.class);
        compositeFunctionRepo.save(function);
        log.info("Composite function created with id: {}", function.getCompositeFunction().getFuncId());
        return mapper.convertValue(function, CompositeFunctionResponse.class);
    }

    @Override
    public List<CompositeFunctionEntity> getAllFunctions() {
        return compositeFunctionRepo.findAll();
    }

    @Override
    public CompositeFunctionResponse getFunction(Long id) {
        return mapper.convertValue(getFunctionDb(id), CompositeFunctionResponse.class);
    }

    @Override
    public CompositeFunctionEntity getFunctionDb(Long id) {
        return compositeFunctionRepo.findById(id)
                .orElseThrow(() -> new CustomException("Composite function is not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public CompositeFunctionResponse update(Long id, CompositeFunctionRequest request) {
        CompositeFunctionEntity function = getFunctionDb(id);
        if (function.getCompositeFunction().getFuncId() != null) {
            function.setInnerFunction(request.getInnerFunction() == null ? function.getInnerFunction() : request.getInnerFunction());
            function.setOuterFunction(request.getOuterFunction() == null ? function.getOuterFunction() : request.getOuterFunction());

            function = compositeFunctionRepo.update(function);
        } else {
            log.error("Composite function not found");
        }
        return mapper.convertValue(function, CompositeFunctionResponse.class);
    }


}
