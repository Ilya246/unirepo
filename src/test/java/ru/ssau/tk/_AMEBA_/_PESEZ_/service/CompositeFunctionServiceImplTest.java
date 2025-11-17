package ru.ssau.tk._AMEBA_._PESEZ_.service;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.CompositeFunctionRequest;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.CompositeFunctionResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.CompositeFunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.exceptions.CustomException;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.CompositeFunctionRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.BaseRepositoryTest;
import ru.ssau.tk._AMEBA_._PESEZ_.utility.TestHibernateSessionFactoryUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class CompositeFunctionServiceImplTest extends BaseRepositoryTest {

    private SessionFactory sessionFactory;
    private CompositeFunctionServiceImpl compositeFunctionService ;
    private CompositeFunctionRepository repo;
    private FunctionRepository functionRepo;

    @BeforeEach
    void setUp() {
        sessionFactory  = TestHibernateSessionFactoryUtil.getSessionFactory();
        repo = new CompositeFunctionRepository(sessionFactory);
        functionRepo = new FunctionRepository(sessionFactory);
        compositeFunctionService  = new CompositeFunctionServiceImpl(repo,functionRepo);
    }

    @Test
    void testCreateCompositeFunction() {
        // Given - создаем базовые функции
        FunctionEntity innerFunction = createTestFunction("x^2", 1);
        FunctionEntity outerFunction = createTestFunction("sin(x)", 1);
        FunctionEntity compositeFunction = createTestFunction("sin(x^2)", 3);

        CompositeFunctionRequest request = new CompositeFunctionRequest();
        request.setInnerFunctionId(innerFunction.getFuncId());
        request.setOuterFunctionId(outerFunction.getFuncId());
        request.setCompositeFunctionId(compositeFunction.getFuncId());

        // When
        CompositeFunctionResponse response = compositeFunctionService.create(request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getCompositeFunctionId());
        assertEquals("sin(x^2)", functionRepo.findById(response.getCompositeFunctionId()).getExpression());
        assertEquals("x^2", functionRepo.findById(response.getInnerFunctionId()).getExpression());
        assertEquals("sin(x)", functionRepo.findById(response.getOuterFunctionId()).getExpression());
    }

    @Test
    void testGetAllFunctions() {
        // Given - создаем несколько композитных функций
        createTestCompositeFunction("x^2", "sin(x)", "sin(x^2)");
        createTestCompositeFunction("x", "cos(x)", "cos(x)");

        // When
        List<CompositeFunctionEntity> functions = compositeFunctionService.getAllFunctions();

        // Then
        assertNotNull(functions);
        assertTrue(functions.size() >= 2);
    }

    @Test
    void testGetFunction() {
        // Given - создаем композитную функцию
        CompositeFunctionEntity compositeEntity = createTestCompositeFunction("x^2", "sin(x)", "sin(x^2)");

        // When
        CompositeFunctionResponse response = compositeFunctionService.getFunction(compositeEntity.getCompositeFunction().getFuncId());

        // Then
        assertNotNull(response);
        assertEquals(compositeEntity.getCompositeFunction().getFuncId(), response.getCompositeFunctionId());
        assertEquals("sin(x^2)", functionRepo.findById(response.getCompositeFunctionId()).getExpression());
    }

    @Test
    void testGetFunction_NotFound() {
        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            compositeFunctionService.getFunction(999L);
        });

        assertEquals("Composite function is not found", exception.getMessage());
    }

    @Test
    void testGetFunctionDb() {
        // Given - создаем композитную функцию
        CompositeFunctionEntity compositeEntity = createTestCompositeFunction("x^2", "sin(x)", "sin(x^2)");

        // When
        CompositeFunctionEntity result = compositeFunctionService.getFunctionDb(compositeEntity.getCompositeFunction().getFuncId());

        // Then
        assertNotNull(result);
        assertEquals(compositeEntity.getCompositeFunction().getFuncId(), result.getCompositeFunction().getFuncId());
        assertEquals(compositeEntity.getInnerFunction().getFuncId(), result.getInnerFunction().getFuncId());
        assertEquals(compositeEntity.getOuterFunction().getFuncId(), result.getOuterFunction().getFuncId());
    }


    @Test
    void testUpdateCompositeFunction() {
        // Given - создаем композитную функцию
        CompositeFunctionEntity originalEntity = createTestCompositeFunction("x^2", "sin(x)", "sin(x^2)");

        // Создаем новые функции для обновления
        FunctionEntity newInnerFunction = createTestFunction("x^3", 1);
        FunctionEntity newOuterFunction = createTestFunction("cos(x)", 1);

        CompositeFunctionRequest updateRequest = new CompositeFunctionRequest();
        updateRequest.setInnerFunctionId(newInnerFunction.getFuncId());
        updateRequest.setOuterFunctionId(newOuterFunction.getFuncId());

        // When
        CompositeFunctionResponse response = compositeFunctionService.update(
                originalEntity.getCompositeFunction().getFuncId(),
                updateRequest
        );

        // Then
        assertNotNull(response);
        assertEquals(newInnerFunction.getFuncId(), response.getInnerFunctionId());
        assertEquals(newOuterFunction.getFuncId(), response.getOuterFunctionId());
        assertEquals("x^3",  functionRepo.findById(response.getInnerFunctionId()).getExpression());
        assertEquals("cos(x)", functionRepo.findById(response.getOuterFunctionId()).getExpression());
    }

    @Test
    void testUpdateCompositeFunction_PartialUpdate() {
        // Given - создаем композитную функцию
        CompositeFunctionEntity originalEntity = createTestCompositeFunction("x^2", "sin(x)", "sin(x^2)");

        // Обновляем только inner function
        FunctionEntity newInnerFunction = createTestFunction("x^3", 1);

        CompositeFunctionRequest updateRequest = new CompositeFunctionRequest();
        updateRequest.setInnerFunctionId(newInnerFunction.getFuncId());
        // outerFunction = null - не должен обновиться

        // When
        CompositeFunctionResponse response = compositeFunctionService.update(
                originalEntity.getCompositeFunction().getFuncId(),
                updateRequest
        );

        // Then
        assertNotNull(response);
        assertEquals(newInnerFunction.getFuncId(), response.getInnerFunctionId()); // обновился
        assertEquals("x^3", functionRepo.findById(response.getInnerFunctionId()).getExpression());
        // outer function остался прежним
        assertEquals(originalEntity.getOuterFunction().getFuncId(), response.getOuterFunctionId());
        assertEquals("sin(x)", functionRepo.findById(response.getOuterFunctionId()).getExpression());
    }

    @Test
    void testUpdateCompositeFunction_NotFound() {
        // Given
        CompositeFunctionRequest request = new CompositeFunctionRequest();

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            compositeFunctionService.update(999L, request);
        });

        assertEquals("Composite function is not found", exception.getMessage());
    }

    // Вспомогательные методы

    private FunctionEntity createTestFunction(String expression, int typeId) {
        FunctionEntity function = new FunctionEntity();
        function.setExpression(expression);
        function.setTypeId(typeId);
        functionRepo.save(function);
        return function;
    }

    private CompositeFunctionEntity createTestCompositeFunction(String innerExpr, String outerExpr, String compositeExpr) {
        FunctionEntity innerFunction = createTestFunction(innerExpr, 1);
        FunctionEntity outerFunction = createTestFunction(outerExpr, 1);
        FunctionEntity compositeFunction = createTestFunction(compositeExpr, 3);

        CompositeFunctionEntity compositeEntity = new CompositeFunctionEntity();
        compositeEntity.setCompositeFunction(compositeFunction);
        compositeEntity.setInnerFunction(innerFunction);
        compositeEntity.setOuterFunction(outerFunction);

        repo.save(compositeEntity);
        return compositeEntity;
    }

}