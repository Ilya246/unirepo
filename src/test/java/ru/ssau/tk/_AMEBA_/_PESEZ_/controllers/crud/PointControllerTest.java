package ru.ssau.tk._AMEBA_._PESEZ_.controllers.crud;


import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.FunctionRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.PointsRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.BaseRepositoryTest;
import ru.ssau.tk._AMEBA_._PESEZ_.utility.TestHibernateSessionFactoryUtil;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PointControllerTest extends BaseRepositoryTest {

    @Autowired
    private MockMvc mockMvc;

    private FunctionRepository functionRepo;
    private PointsRepository pointsRepo;
    private FunctionEntity testFunction;

    @BeforeEach
    void setUp() {
        SessionFactory sessionFactory = TestHibernateSessionFactoryUtil.getSessionFactory();
        functionRepo = new FunctionRepository(sessionFactory);
        pointsRepo = new PointsRepository(sessionFactory);

        // Создаем тестовую функцию
        testFunction = new FunctionEntity(1, "x^2");
        functionRepo.save(testFunction);
    }

    @Test
    void createPoint_ShouldCreateAndReturnPoint() throws Exception {
        String pointJson = """
            {
                "functionId": %d,
                "xValue": 0.75,
                "yValue": 4.0
            }
            """.formatted(testFunction.getFuncId());

        mockMvc.perform(post("/points")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pointJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.functionId").value(testFunction.getFuncId()))
                .andExpect(jsonPath("$.xValue").value(0.75))
                .andExpect(jsonPath("$.yValue").value(4.0));
    }
}