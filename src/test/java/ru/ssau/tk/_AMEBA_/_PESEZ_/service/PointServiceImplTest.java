package ru.ssau.tk._AMEBA_._PESEZ_.service;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.PointRequest;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.PointsResponse;
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
    void testDeletePoint() {
        // Given - создаем функцию и точку
        FunctionEntity function = new FunctionEntity(1,"x^2");
        functionRepo.save(function);
        PointRequest r = new PointRequest(function.getFuncId(), 5.0, 25.0);
        pointService.createPoint(r);
        // When
        pointService.deletePoint(function.getFuncId(), 5.0);
    }




}