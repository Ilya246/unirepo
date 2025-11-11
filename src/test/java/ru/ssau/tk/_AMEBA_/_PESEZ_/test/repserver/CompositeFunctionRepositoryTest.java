package ru.ssau.tk._AMEBA_._PESEZ_.test.repserver;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.CompositeFunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.PointsEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.CompositeFunctionRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.PointsRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.utility.TestHibernateSessionFactoryUtil;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CompositeFunctionRepositoryTest extends BaseRepositoryTest {
    private SessionFactory factory  = TestHibernateSessionFactoryUtil.getSessionFactory();
    private CompositeFunctionRepository compositeFunctionRepository= new CompositeFunctionRepository(factory);
    private FunctionRepository functionRepository= new FunctionRepository(factory);


    @Test
    void testSaveAndFindById() {

        FunctionEntity innerFunc = new FunctionEntity(1, "x");
        FunctionEntity outerFunc = new FunctionEntity(1, "x^2");
        FunctionEntity compositeFunc = new FunctionEntity(3, "(x)^2"); // композитная функция

        functionRepository.save(innerFunc);
        functionRepository.save(outerFunc);
        functionRepository.save(compositeFunc);

        CompositeFunctionEntity composite = new CompositeFunctionEntity(
                compositeFunc, innerFunc, outerFunc
        );
        compositeFunctionRepository.save(composite);

        Optional<CompositeFunctionEntity> found = compositeFunctionRepository.findById(compositeFunc.getFuncId());

        assertTrue(found.isPresent(), "Композитная функция должна быть найдена");
        assertEquals(3, found.get().getCompositeFunction().getFuncId());
        assertEquals(1, found.get().getInnerFunction().getFuncId());
        assertEquals(2, found.get().getOuterFunction().getFuncId());
        assertEquals("x", found.get().getInnerFunction().getExpression());
        assertEquals("x^2", found.get().getOuterFunction().getExpression());
    }

    @Test
    void testFindAll() {

        FunctionEntity inner1 = new FunctionEntity(1, "x");
        FunctionEntity outer1 = new FunctionEntity(1, "x^2");
        FunctionEntity composite1 = new FunctionEntity(3, "(x)^2");

        FunctionEntity inner2 = new FunctionEntity(1, "2*x");
        FunctionEntity outer2 = new FunctionEntity(2, "sin(x)");
        FunctionEntity composite2 = new FunctionEntity(3, "sin(2*x)");

        functionRepository.save(inner1);
        functionRepository.save(outer1);
        functionRepository.save(composite1);
        functionRepository.save(inner2);
        functionRepository.save(outer2);
        functionRepository.save(composite2);

        // Создаем композитные функции
        CompositeFunctionEntity comp1 = new CompositeFunctionEntity(composite1, inner1, outer1);
        CompositeFunctionEntity comp2 = new CompositeFunctionEntity(composite2, inner2, outer2);

        compositeFunctionRepository.save(comp1);
        compositeFunctionRepository.save(comp2);

        List<CompositeFunctionEntity> allComposites = compositeFunctionRepository.findAll();

        assertEquals(2, allComposites.size(), "Должно быть две композитные функции");
        assertTrue(allComposites.stream().anyMatch(c ->
                c.getCompositeFunction().getExpression().equals("(x)^2")));
        assertTrue(allComposites.stream().anyMatch(c ->
                c.getCompositeFunction().getExpression().equals("sin(2*x)")));
    }

    @Test
    void testUpdate() {

        FunctionEntity inner = new FunctionEntity(1, "x");
        FunctionEntity outer = new FunctionEntity(1, "x^2");
        FunctionEntity composite = new FunctionEntity(3, "(x)^2");
        FunctionEntity newOuter = new FunctionEntity(2, "cos(x)");

        functionRepository.save(inner);
        functionRepository.save(outer);
        functionRepository.save(composite);
        functionRepository.save(newOuter);

        CompositeFunctionEntity comp = new CompositeFunctionEntity(composite, inner, outer);
        compositeFunctionRepository.save(comp);

        comp.setOuterFunction(newOuter);
        CompositeFunctionEntity updated = compositeFunctionRepository.update(comp);

        assertNotNull(updated, "Обновленная функция не должна быть null");
        assertEquals(4, updated.getOuterFunction().getFuncId());
        assertEquals(1, updated.getInnerFunction().getFuncId()); // внутренняя не изменилась
        assertEquals(3, updated.getCompositeFunction().getFuncId()); // композитная не изменилась

        Optional<CompositeFunctionEntity> foundAfterUpdate = compositeFunctionRepository.findById(composite.getFuncId());
        assertTrue(foundAfterUpdate.isPresent());
    }


    @Test
    void testCompositeFunctionWithPoints() {

        PointsRepository pointsRepository = new PointsRepository(factory);

        // Создаем базовые функции
        FunctionEntity inner = new FunctionEntity(1, "x");           // f(x) = x
        FunctionEntity outer = new FunctionEntity(1, "x^2");         // g(x) = x²
        FunctionEntity composite = new FunctionEntity(3, "(x)^2");   // композитная: g(f(x)) = (x)² = x²

        functionRepository.save(inner);
        functionRepository.save(outer);
        functionRepository.save(composite);

        // Создаем композитную функцию
        CompositeFunctionEntity comp = new CompositeFunctionEntity(composite, inner, outer);
        compositeFunctionRepository.save(comp);

        // Добавляем точки для КОМПОЗИТНОЙ функции
        // Для g(f(x)) = (x)² = x²
        PointsEntity point1 = new PointsEntity(composite, -2.0, 4.0);   // (-2)² = 4
        PointsEntity point2 = new PointsEntity(composite, -1.0, 1.0);   // (-1)² = 1
        PointsEntity point3 = new PointsEntity(composite, 0.0, 0.0);    // 0² = 0
        PointsEntity point4 = new PointsEntity(composite, 1.0, 1.0);    // 1² = 1
        PointsEntity point5 = new PointsEntity(composite, 2.0, 4.0);    // 2² = 4

        pointsRepository.save(point1);
        pointsRepository.save(point2);
        pointsRepository.save(point3);
        pointsRepository.save(point4);
        pointsRepository.save(point5);

        // Также добавляем точки для ВНУТРЕННЕЙ функции (f(x) = x)
        PointsEntity innerPoint1 = new PointsEntity(inner, -2.0, -2.0);
        PointsEntity innerPoint2 = new PointsEntity(inner, 0.0, 0.0);
        PointsEntity innerPoint3 = new PointsEntity(inner, 2.0, 2.0);

        pointsRepository.save(innerPoint1);
        pointsRepository.save(innerPoint2);
        pointsRepository.save(innerPoint3);

        // Проверяем композитную функцию
        Optional<CompositeFunctionEntity> foundComp = compositeFunctionRepository.findById(composite.getFuncId());
        assertTrue(foundComp.isPresent(), "Композитная функция должна быть найдена");

        CompositeFunctionEntity retrievedComp = foundComp.get();

        // Проверяем структуру композитной функции
        assertEquals("(x)^2", retrievedComp.getCompositeFunction().getExpression());
        assertEquals("x", retrievedComp.getInnerFunction().getExpression());
        assertEquals("x^2", retrievedComp.getOuterFunction().getExpression());

        // Проверяем точки КОМПОЗИТНОЙ функции
        List<PointsEntity> compositePoints = pointsRepository.findByFunction(composite);
        assertEquals(5, compositePoints.size(), "Должно быть 5 точек у композитной функции");

        // Проверяем конкретные значения точек композитной функции
        assertTrue(compositePoints.stream().anyMatch(p ->
                p.getXValue() == -2.0 && p.getYValue() == 4.0));
        assertTrue(compositePoints.stream().anyMatch(p ->
                p.getXValue() == 0.0 && p.getYValue() == 0.0));
        assertTrue(compositePoints.stream().anyMatch(p ->
                p.getXValue() == 2.0 && p.getYValue() == 4.0));

        // Проверяем точки ВНУТРЕННЕЙ функции
        List<PointsEntity> innerPoints = pointsRepository.findByFunction(inner);
        assertEquals(3, innerPoints.size(), "Должно быть 3 точки у внутренней функции");

        // Проверяем конкретные значения точек внутренней функции
        assertTrue(innerPoints.stream().anyMatch(p ->
                p.getXValue() == -2.0 && p.getYValue() == -2.0));
        assertTrue(innerPoints.stream().anyMatch(p ->
                p.getXValue() == 0.0 && p.getYValue() == 0.0));

        // Проверяем количество точек через count
        long compositePointsCount = pointsRepository.countByFunction(composite);
        long innerPointsCount = pointsRepository.countByFunction(inner);
        long outerPointsCount = pointsRepository.countByFunction(outer);

        assertEquals(5, compositePointsCount, "Count должен вернуть 5 точек для композитной функции");
        assertEquals(3, innerPointsCount, "Count должен вернуть 3 точки для внутренней функции");
        assertEquals(0, outerPointsCount, "У внешней функции не должно быть точек");

        // Проверяем, что можем найти конкретную точку композитной функции
        Optional<PointsEntity> specificPoint = pointsRepository.findById(composite, 2.0);
        assertTrue(specificPoint.isPresent(), "Должна быть найдена точка (2.0, 4.0)");
        assertEquals(4.0, specificPoint.get().getYValue(), 0.001);
    }
}