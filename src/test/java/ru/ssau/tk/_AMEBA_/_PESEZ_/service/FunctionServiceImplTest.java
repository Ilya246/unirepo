package ru.ssau.tk._AMEBA_._PESEZ_.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.CompositeFunctionResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.FunctionResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.MathFunctionResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.TabulatedFunctionResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.exceptions.CustomException;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.MathFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.BaseRepositoryTest;
import ru.ssau.tk._AMEBA_._PESEZ_.utility.TestHibernateSessionFactoryUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class FunctionServiceImplTest extends BaseRepositoryTest {
    private FunctionServiceImpl functionService;
    private FunctionRepository functionRepository;
    private SessionFactory sessionFactory;

    @BeforeEach
    void setUp() {
        sessionFactory  = TestHibernateSessionFactoryUtil.getSessionFactory();
        functionRepository = new FunctionRepository(sessionFactory);
        functionService = new FunctionServiceImpl(functionRepository, new ObjectMapper());
    }

    @Test
    void testGetFunction() {
        // Given
        FunctionEntity function = new FunctionEntity();
        function.setExpression("x^2");
        function.setTypeId(1);
        functionRepository.save(function);

        // When
        FunctionResponse response = functionService.getFunction(function.getFuncId());

        // Then
        assertNotNull(response);
        assertEquals(function.getFuncId(), response.getFuncId());
        assertEquals("x^2", response.getExpression());
    }*/

    @Test
    void testGetFunction_NotFound() {
        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            functionService.getFunction(999L);
        });

        assertEquals("Function not found with id: 999", exception.getMessage());
    }

    @Test
    void testGetAllFunctions() {
        // Given
        createTestFunctions();

        // When
        List<FunctionResponse> functions = functionService.getAllFunctions();

        // Then
        assertNotNull(functions);
        assertTrue(functions.size() >= 3);
    }

    @Test
    void testGetFunctionsByType() {
        // Given
        createTestFunctions();

        // When
        List<FunctionResponse> mathFunctions = functionService.getFunctionsByType(1);
        List<FunctionResponse> tabulatedFunctions = functionService.getFunctionsByType(2);

        // Then
        assertNotNull(mathFunctions);
        assertNotNull(tabulatedFunctions);
        assertTrue(mathFunctions.size() >= 2); // math1 и math2
        assertTrue(tabulatedFunctions.size() >= 1); // tabulated1
    }

    @Test
    void testCreateMathFunction() {
        // Given
        MathFunctionRequest request = new MathFunctionRequest();
        request.setExpression("x * 2");

        // When
        MathFunctionResponse response = functionService.createMathFunction(request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getFuncId());
        assertEquals("x * 2", response.getExpression());
    }

/*    @Test
    void testCreateTabulatedFunction() {
        // Given
        TabulatedFunctionRequest request = new TabulatedFunctionRequest();
        request.setExpression("sin(x)");
        request.setFrom(0.0);
        request.setTo(3.14);
        request.setPointCount(10);

        // When
        TabulatedFunctionResponse response = functionService.createTabulatedFunction(request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getFuncId());
        assertEquals("sin(x)", response.getExpression());
        assertEquals(0.0, response.getFrom());
        assertEquals(3.14, response.getTo());
        assertEquals(10, response.getPointCount());
    }*/

/*    @Test
    void testCreatePureTabulatedFunction() {
        // Given
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        PureTabulatedRequest request = new PureTabulatedRequest();
        request.setXValues(xValues);
        request.setYValues(yValues);

        // When
        FunctionResponse response = functionService.createPureTabulatedFunction(request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getFuncId());
        assertEquals(2, response.getTypeId()); // TABULATED type
    }*/

    @Test
    void testCreateCompositeFunction() {
        // Given - создаем две базовые функции
        FunctionEntity innerFunction = new FunctionEntity();
        innerFunction.setExpression("x^2");
        innerFunction.setTypeId(1);
        functionRepository.save(innerFunction);

        FunctionEntity outerFunction = new FunctionEntity();
        outerFunction.setExpression("sin(x)");
        outerFunction.setTypeId(1);
        functionRepository.save(outerFunction);

        CompositeFunctionRequest request = new CompositeFunctionRequest();
        request.setInnerFunctionId(innerFunction.getFuncId());
        request.setOuterFunctionId(outerFunction.getFuncId());

        // When
        CompositeFunctionResponse response = functionService.createCompositeFunction(request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getCompositeFunctionId());
        assertEquals(innerFunction.getFuncId(), response.getInnerFunctionId());
        assertEquals(outerFunction.getFuncId(), response.getOuterFunctionId());}

    @Test
    void testCalculateFunction() {
        // Given - создаем простую математическую функцию
        FunctionEntity function = new FunctionEntity();
        function.setExpression("x + 5");
        function.setTypeId(1);
        functionRepository.save(function);

        // When
        Double result = functionService.calculateFunction(function.getFuncId(), 3.0);

        // Then - x + 5 при x=3 должно быть 8
        assertEquals(8.0, result, 0.001);
    }

    @Test
    void testGetMathFunction() throws ExecutionException, InterruptedException {
        // Given
        FunctionEntity function = new FunctionEntity();
        function.setExpression("x * 2");
        function.setTypeId(1);
        functionRepository.save(function);

        // When
        CompletableFuture<MathFunction> future = functionService.getMathFunction(function.getFuncId());
        MathFunction mathFunction = future.get();

        // Then
        assertNotNull(mathFunction);
        // Проверяем что функция работает
        assertEquals(6.0, mathFunction.apply(3.0), 0.001);
    }

    @Test
    void testUpdatePoint() {
        // Given - создаем табулированную функцию
        FunctionEntity function = new FunctionEntity();
        function.setExpression("x^2");
        function.setTypeId(2);
        functionRepository.save(function);

        // When & Then - проверяем что метод не падает
        assertDoesNotThrow(() -> {
            functionService.updatePoint(function.getFuncId(), 2.0, 4.5);
        });
    }

    @Test
    void testDeletePoint() {
        // Given
        FunctionEntity function = new FunctionEntity();
        function.setExpression("x^2");
        function.setTypeId(2);
        functionRepository.save(function);

        // When & Then - проверяем что метод не падает
        assertDoesNotThrow(() -> {
            functionService.deletePoint(function.getFuncId(), 2.0);
        });
    }

    @Test
    void testGetFunctionDb() {
        // Given
        FunctionEntity function = new FunctionEntity();
        function.setExpression("test");
        function.setTypeId(1);
        functionRepository.save(function);

        // When
        FunctionEntity result = functionService.getFunctionDb(function.getFuncId());

        // Then
        assertNotNull(result);
        assertEquals(function.getFuncId(), result.getFuncId());
        assertEquals("test", result.getExpression());
    }

    // Вспомогательный метод для создания тестовых функций
    private void createTestFunctions() {
        FunctionEntity math1 = new FunctionEntity();
        math1.setExpression("x + 1");
        math1.setTypeId(1);
        functionRepository.save(math1);

        FunctionEntity math2 = new FunctionEntity();
        math2.setExpression("x * 2");
        math2.setTypeId(1);
        functionRepository.save(math2);

        FunctionEntity tabulated1 = new FunctionEntity();
        tabulated1.setExpression("sin(x)");
        tabulated1.setTypeId(2);
        functionRepository.save(tabulated1);
    }
}