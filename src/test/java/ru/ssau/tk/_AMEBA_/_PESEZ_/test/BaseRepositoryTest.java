package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
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
        cleanDatabase();
        createTables();
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

            session.createNativeQuery("DROP TABLE IF EXISTS Points CASCADE").executeUpdate();
            session.createNativeQuery("DROP TABLE IF EXISTS Composite_Function CASCADE").executeUpdate();
            session.createNativeQuery("DROP TABLE IF EXISTS Function_Ownership CASCADE").executeUpdate();
            session.createNativeQuery("DROP TABLE IF EXISTS Function CASCADE").executeUpdate();
            session.createNativeQuery("DROP TABLE IF EXISTS Users CASCADE").executeUpdate();

            cleanupTransaction.commit();
        } catch (Exception e) {
            if (cleanupTransaction != null && cleanupTransaction.isActive()) {
                cleanupTransaction.rollback();
            }
            System.err.println("Warning during database cleanup: " + e.getMessage());
        }
    }
    protected void createTables() {
        Transaction createTransaction = null;
        try {
            createTransaction = session.beginTransaction();

            session.createNativeQuery("""
            CREATE TABLE IF NOT EXISTS users (
                user_Id INTEGER PRIMARY KEY,
                type_Id INTEGER CHECK (type_Id >= 1 AND type_Id <= 2),
                user_Name VARCHAR(100),
                password VARCHAR(20),
                created_Date TIMESTAMP
            )
        """).executeUpdate();

            // 2. Function - независимая таблица
            session.createNativeQuery("""
            CREATE TABLE IF NOT EXISTS Function (
                func_Id INTEGER PRIMARY KEY,
                type_Id INTEGER CHECK (type_Id >= 1 AND type_Id <= 3),
                expression VARCHAR(200)
            )
        """).executeUpdate();

            session.createNativeQuery("""
            CREATE TABLE IF NOT EXISTS Function_Ownership (
                user_Id INTEGER,
                func_Id INTEGER,
                created_Date TIMESTAMP,
                func_Name VARCHAR(100),
                PRIMARY KEY (user_Id, func_Id),
                FOREIGN KEY (user_Id) REFERENCES users(user_Id) ON DELETE CASCADE,
                FOREIGN KEY (func_Id) REFERENCES Function(func_Id) ON DELETE CASCADE
            )
        """).executeUpdate();

            session.createNativeQuery("""
            CREATE TABLE IF NOT EXISTS Composite_Function (
                func_Id INTEGER,
                inner_Func_Id INTEGER,
                outer_Func_Id INTEGER,
                PRIMARY KEY (func_Id),
                FOREIGN KEY (func_Id) REFERENCES Function(func_Id) ON DELETE CASCADE,
                FOREIGN KEY (inner_Func_Id) REFERENCES Function(func_Id) ON DELETE CASCADE,
                FOREIGN KEY (outer_Func_Id) REFERENCES Function(func_Id) ON DELETE CASCADE
            )
        """).executeUpdate();

            session.createNativeQuery("""
            CREATE TABLE IF NOT EXISTS Points (
                func_Id INTEGER,
                x_Value DOUBLE PRECISION,
                y_Value DOUBLE PRECISION,
                PRIMARY KEY (func_Id, x_Value),
                FOREIGN KEY (func_Id) REFERENCES Function(func_Id) ON DELETE CASCADE
            )
        """).executeUpdate();

            createTransaction.commit();
        } catch (Exception e) {
            if (createTransaction != null && createTransaction.isActive()) {
                createTransaction.rollback();
            }
            System.err.println("Error during table creation: " + e.getMessage());
            throw new RuntimeException("Failed to create tables", e);
        }
    }
}