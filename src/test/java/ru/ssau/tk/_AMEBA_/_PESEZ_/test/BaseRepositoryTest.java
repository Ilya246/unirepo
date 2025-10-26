package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.ssau.tk._AMEBA_._PESEZ_.utility.TestHibernateSessionFactoryUtil;

import java.util.List;

public abstract class BaseRepositoryTest {

    protected Session session;
    protected Transaction transaction;

    @BeforeEach
    void baseSetUp() {
        session = TestHibernateSessionFactoryUtil.getSessionFactory().openSession();
        cleanDatabase(); // очищаем перед тестом
        transaction = session.beginTransaction();
    }

    @AfterEach
    void baseTearDown() {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }
        if (session != null && session.isOpen()) {
            session.close();
        }
    }

    protected void cleanDatabase() {
        Transaction cleanupTransaction = null;
        try {
            cleanupTransaction = session.beginTransaction();

            // очищаем в правильном порядке
            session.createNativeQuery("TRUNCATE TABLE Points CASCADE").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE Composite_Function CASCADE").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE Function_Ownership CASCADE").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE Function CASCADE").executeUpdate();
            session.createNativeQuery("TRUNCATE TABLE Users CASCADE").executeUpdate();

            cleanupTransaction.commit();
        } catch (Exception e) {
            if (cleanupTransaction != null && cleanupTransaction.isActive()) {
                cleanupTransaction.rollback();
            }
            System.err.println("Warning during database cleanup: " + e.getMessage());
        }
    }
}
