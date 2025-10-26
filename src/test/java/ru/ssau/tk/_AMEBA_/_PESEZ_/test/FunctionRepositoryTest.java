package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.utility.TestHibernateSessionFactoryUtil;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FunctionRepositoryTest extends BaseRepositoryTest{
    private final FunctionRepository repository =
            new FunctionRepository(TestHibernateSessionFactoryUtil.getSessionFactory());


    @Test
    void testSaveAndFindById() {
        FunctionEntity function = new FunctionEntity(1, 1, "x^2 + 2*x + 1");
        repository.save(function);

        FunctionEntity found = repository.findById(1);

        assertNotNull(found, "Функция должна быть найдена после сохранения");
        assertEquals("x^2 + 2*x + 1", found.getExpression());
        assertEquals(1, found.getTypeId());
    }

    @Test
    void testFindAll() {
        FunctionEntity f1 = new FunctionEntity(1, 1, "sin(x)");
        FunctionEntity f2 = new FunctionEntity(2, 2, "cos(x)");
        FunctionEntity f3 = new FunctionEntity(3, 3, "x^3");

        repository.save(f1);
        repository.save(f2);
        repository.save(f3);

        List<FunctionEntity> functions = repository.findAll();

        assertEquals(3, functions.size(), "Должно быть три функции в базе");
        assertTrue(functions.stream().anyMatch(f -> f.getExpression().equals("sin(x)")));
        assertTrue(functions.stream().anyMatch(f -> f.getExpression().equals("cos(x)")));
        assertTrue(functions.stream().anyMatch(f -> f.getExpression().equals("x^3")));
    }

    @Test
    void testFindByType() {
        FunctionEntity f1 = new FunctionEntity(1, 1, "sin(x)");
        FunctionEntity f2 = new FunctionEntity(2, 1, "cos(x)");
        FunctionEntity f3 = new FunctionEntity(3, 2, "x^2");
        FunctionEntity f4 = new FunctionEntity(4, 3, "log(x)");

        repository.save(f1);
        repository.save(f2);
        repository.save(f3);
        repository.save(f4);

        List<FunctionEntity> type1Functions = repository.findByType(1);
        assertEquals(2, type1Functions.size(), "Должно быть найдено 2 функции с typeId = 1");
        assertTrue(type1Functions.stream().anyMatch(f -> f.getExpression().equals("sin(x)")));
        assertTrue(type1Functions.stream().anyMatch(f -> f.getExpression().equals("cos(x)")));
        assertFalse(type1Functions.stream().anyMatch(f -> f.getExpression().equals("x^2")));

        List<FunctionEntity> type2Functions = repository.findByType(2);
        assertEquals(1, type2Functions.size(), "Должна быть найдена 1 функция с typeId = 2");
        assertEquals("x^2", type2Functions.get(0).getExpression());

        List<FunctionEntity> type300Functions = repository.findByType(300);
        assertTrue(type300Functions.isEmpty(), "Не должно быть функций с typeId = 300");
    }

    @Test
    void testDeleteById() {
        FunctionEntity function = new FunctionEntity(1, 1, "x + 5");
        repository.save(function);

        // Проверяем, что функция сохранена
        FunctionEntity foundBeforeDelete = repository.findById(1);
        assertNotNull(foundBeforeDelete, "Функция должна существовать до удаления");

        repository.deleteById(1);

        FunctionEntity foundAfterDelete = repository.findById(1);
        assertNull(foundAfterDelete, "Функция должна быть удалена");
    }
}