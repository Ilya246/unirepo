package ru.ssau.tk._AMEBA_._PESEZ_.service;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.PointRequest;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.PointResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.exceptions.CustomException;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.PointsRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.BaseRepositoryTest;
import ru.ssau.tk._AMEBA_._PESEZ_.utility.TestHibernateSessionFactoryUtil;

import static org.junit.jupiter.api.Assertions.*;

class PointServiceImplTest extends BaseRepositoryTest {


    private SessionFactory sessionFactory;
    private PointServiceImpl pointService;
    private PointsRepository pointsRepo;
    private FunctionRepository functionRepo;

    @BeforeEach
    void setUp() {
        sessionFactory = TestHibernateSessionFactoryUtil.getSessionFactory();
        pointsRepo = new PointsRepository(sessionFactory);
        functionRepo = new FunctionRepository(sessionFactory);
        pointService = new PointServiceImpl(pointsRepo, functionRepo);
    }

    @Test
    void testCreatePoint() {
        // Given - создаем функцию
        FunctionEntity function = new FunctionEntity(1,"x^2");
        functionRepo.save(function);
        PointRequest request = new PointRequest(function.getFuncId(), 0.75, 1.0);

        // When
        PointResponse response = pointService.createPoint(request);

        // Then
        assertNotNull(response);
        assertEquals(function.getFuncId(), response.getFunctionId());
        assertEquals(0.75, response.getXValue());
        assertEquals(1.0, response.getYValue());
    }

    @Test
    void testCreatePoint_FunctionNotFound() {
        // Given
        PointRequest request = new PointRequest(999L, 0.75, 1.0);

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            pointService.createPoint(request);
        });

        assertEquals("Function not found with id: 999", exception.getMessage());
    }

    @Test
    void testCreatePoint_PointAlreadyExists() {
        // Given - создаем функцию и точку
        FunctionEntity function = new FunctionEntity(1,"x^2");
        functionRepo.save(function);
        PointRequest r = new PointRequest(function.getFuncId(), 0.75, 1.0);
        pointService.createPoint(r);

        PointRequest request = new PointRequest(function.getFuncId(), 0.75, 1.0);

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            pointService.createPoint(request);
        });

        assertEquals("Point already exists for function " + function.getFuncId() + " with x=0.75", exception.getMessage());
    }

    @Test
    void testGetPoint() {
        // Given - создаем функцию и точку
        FunctionEntity function = new FunctionEntity(1,"x^2");
        functionRepo.save(function);
        PointRequest r = new PointRequest(function.getFuncId(), 3.0, 9.0);
        pointService.createPoint(r);
        // When
        PointResponse response = pointService.getPoint(function.getFuncId(), 3.0);

        // Then
        assertNotNull(response);
        assertEquals(function.getFuncId(), response.getFunctionId());
        assertEquals(3.0, response.getXValue());
        assertEquals(9.0, response.getYValue());
    }

    @Test
    void testUpdatePoint() {
        // Given - создаем функцию и точку
        FunctionEntity function = new FunctionEntity(1,"x^2");
        functionRepo.save(function);
        PointRequest r = new PointRequest(function.getFuncId(), 4.0, 16.0);
        pointService.createPoint(r);

        PointRequest updateRequest = new PointRequest(function.getFuncId(), 4.0, 20.0);

        // When
        PointResponse response = pointService.updatePoint(function.getFuncId(), 4.0, updateRequest);

        // Then
        assertNotNull(response);
        assertEquals(function.getFuncId(), response.getFunctionId());
        assertEquals(4.0, response.getXValue());
        assertEquals(20.0, response.getYValue());
    }

    @Test
    void testDeletePoint() {
        // Given - создаем функцию и точку
        FunctionEntity function = new FunctionEntity(1,"x^2");
        functionRepo.save(function);
        PointRequest r = new PointRequest(function.getFuncId(), 5.0, 25.0);
        pointService.createPoint(r);
        // When
        pointService.deletePoint(function.getFuncId(), 5.0);

        // Then - проверяем, что точка удалена
        CustomException exception = assertThrows(CustomException.class, () -> {
            pointService.getPoint(function.getFuncId(), 5.0);
        });

        assertEquals("Point not found for function " + function.getFuncId() + " with x=5.0", exception.getMessage());
    }




}