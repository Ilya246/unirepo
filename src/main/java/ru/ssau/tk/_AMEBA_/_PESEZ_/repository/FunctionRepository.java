package ru.ssau.tk._AMEBA_._PESEZ_.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.utility.HibernateSessionFactoryUtil;

import java.util.List;

public class FunctionRepository {

    private final SessionFactory sessionFactory;

    // Передаём SessionFactory при создании репозитория
    public FunctionRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public FunctionEntity findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(FunctionEntity.class, id);
        }
    }

    public void save(FunctionEntity function) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(function);
            transaction.commit();
        }
    }

    public List<FunctionEntity> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM FunctionEntity", FunctionEntity.class).list();
        }
    }

    public List<FunctionEntity> findByType(int typeId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "FROM FunctionEntity WHERE typeId = :typeId", FunctionEntity.class)
                    .setParameter("typeId", typeId)
                    .list();
        }
    }
    public void deleteById(int functionId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            FunctionEntity function = session.find(FunctionEntity.class, functionId);
            if (function != null) {
                session.remove(function);
            }
            transaction.commit();
        }
    }



}
