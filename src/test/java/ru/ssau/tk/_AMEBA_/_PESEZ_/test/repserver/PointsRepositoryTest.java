package ru.ssau.tk._AMEBA_._PESEZ_.test.repserver;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.PointsEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.PointsRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.utility.TestHibernateSessionFactoryUtil;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PointsRepositoryTest extends BaseRepositoryTest {

    private SessionFactory factory = TestHibernateSessionFactoryUtil.getSessionFactory();;
    private PointsRepository pointsRepository= new PointsRepository(factory);
    private FunctionRepository functionRepository= new FunctionRepository(factory);


    @Test
    void testSaveAndFindById() {

        FunctionEntity function = new FunctionEntity(1, "x^2");
        functionRepository.save(function);

        PointsEntity point = new PointsEntity(function, 2.0, 4.0);
        pointsRepository.save(point);

        Optional<PointsEntity> found = pointsRepository.findById(function, 2.0);

        assertTrue(found.isPresent(), "Точка должна быть найдена");
        assertEquals(2.0, found.get().get_xValue(), 0.001);
        assertEquals(4.0, found.get().get_yValue(), 0.001);
        assertEquals(function.getFuncId(), found.get().getFunction().getFuncId());
    }

    @Test
    void testFindByFunction() {

        FunctionEntity function = new FunctionEntity(1, "x^2");
        functionRepository.save(function);

        PointsEntity point1 = new PointsEntity(function, -2.0, 4.0);
        PointsEntity point2 = new PointsEntity(function, -1.0, 1.0);
        PointsEntity point3 = new PointsEntity(function, 0.0, 0.0);
        PointsEntity point4 = new PointsEntity(function, 1.0, 1.0);
        PointsEntity point5 = new PointsEntity(function, 2.0, 4.0);

        pointsRepository.save(point1);
        pointsRepository.save(point2);
        pointsRepository.save(point3);
        pointsRepository.save(point4);
        pointsRepository.save(point5);

        List<PointsEntity> points = pointsRepository.findByFunction(function);

        assertEquals(5, points.size(), "Должно быть 5 точек для функции");
    }

    @Test
    void testUpdateById() {


        FunctionEntity function = new FunctionEntity(1, "x^2");
        functionRepository.save(function);

        PointsEntity point = new PointsEntity(function, 3.0, 9.0);
        pointsRepository.save(point);

        pointsRepository.updateById(function.getFuncId(), 3.0, 10.0);

        Optional<PointsEntity> updated = pointsRepository.findById(function, 3.0);
        assertTrue(updated.isPresent(), "Точка должна существовать после обновления");
        assertEquals(10.0, updated.get().get_yValue(), 0.001, "yValue должно быть обновлено");
        assertEquals(3.0, updated.get().get_xValue(), 0.001, "xValue не должно измениться");
    }

    @Test
    void testDeleteById() {

        FunctionEntity function = new FunctionEntity(1, "x^2");
        functionRepository.save(function);

        PointsEntity point = new PointsEntity(function, 5.0, 25.0);
        pointsRepository.save(point);

        Optional<PointsEntity> foundBefore = pointsRepository.findById(function, 5.0);
        assertTrue(foundBefore.isPresent(), "Точка должна существовать до удаления");

        pointsRepository.deleteById(function.getFuncId(), 5.0);

        Optional<PointsEntity> foundAfter = pointsRepository.findById(function, 5.0);
        assertFalse(foundAfter.isPresent(), "Точка должна быть удалена");
    }

    @Test
    void testDeleteByFunction() {

        FunctionEntity function = new FunctionEntity(1, "x^2");
        functionRepository.save(function);

        PointsEntity point1 = new PointsEntity(function, 1.0, 1.0);
        PointsEntity point2 = new PointsEntity(function, 2.0, 4.0);
        PointsEntity point3 = new PointsEntity(function, 3.0, 9.0);

        pointsRepository.save(point1);
        pointsRepository.save(point2);
        pointsRepository.save(point3);

        List<PointsEntity> pointsBefore = pointsRepository.findByFunction(function);
        assertEquals(3, pointsBefore.size(), "Должно быть 3 точки до удаления");

        pointsRepository.deleteByFunction(function);

        List<PointsEntity> pointsAfter = pointsRepository.findByFunction(function);
        assertTrue(pointsAfter.isEmpty(), "Все точки функции должны быть удалены");
    }

    @Test
    void testCountByFunction() {

        // Создаем две функции
        FunctionEntity function1 = new FunctionEntity(1, "x^2");
        FunctionEntity function2 = new FunctionEntity(2, "sin(x)");
        functionRepository.save(function1);
        functionRepository.save(function2);

        // Создаем точки для первой функции
        PointsEntity point1 = new PointsEntity(function1, -1.0, 1.0);
        PointsEntity point2 = new PointsEntity(function1, 0.0, 0.0);
        PointsEntity point3 = new PointsEntity(function1, 1.0, 1.0);

        // Создаем точки для второй функции
        PointsEntity point4 = new PointsEntity(function2, 0.0, 0.0);
        PointsEntity point5 = new PointsEntity(function2, Math.PI / 2, 1.0);

        pointsRepository.save(point1);
        pointsRepository.save(point2);
        pointsRepository.save(point3);
        pointsRepository.save(point4);
        pointsRepository.save(point5);

        long count1 = pointsRepository.countByFunction(function1);
        long count2 = pointsRepository.countByFunction(function2);

        assertEquals(3, count1, "Должно быть 3 точки для первой функции");
        assertEquals(2, count2, "Должно быть 2 точки для второй функции");
    }

    @Test
    void testFindByIdNotFound() {
        FunctionEntity function = new FunctionEntity(1, "x^2");
        functionRepository.save(function);

        // Пытаемся найти несуществующую точку
        Optional<PointsEntity> found = pointsRepository.findById(function, 999.0);

        assertFalse(found.isPresent(), "Не должна быть найдена несуществующая точка");
    }




}