package ru.ssau.tk._AMEBA_._PESEZ_.repository;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.PointsEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.utility.HibernateSessionFactoryUtil;

import java.util.List;
import java.util.Optional;

public class PointsRepository {

    private final SessionFactory sessionFactory;

    // Передаём SessionFactory при создании репозитория
    public PointsRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(PointsEntity points) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(points);
            transaction.commit();
        }
    }
    public Optional<PointsEntity> findById(FunctionEntity function, double xValue) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "FROM PointsEntity WHERE function = :function AND xValue = :xValue", PointsEntity.class)
                    .setParameter("function", function)
                    .setParameter("xValue", xValue)
                    .uniqueResultOptional();
        }
    }
    public List<PointsEntity> findByFunction(FunctionEntity function) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "FROM PointsEntity WHERE function = :function ORDER BY xValue", PointsEntity.class)
            .setParameter("function", function)
            .list();
        }
    }
    public void updateById(int functionId, double xValue, Double newYValue) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            Query<?> query = session.createQuery(
                    "UPDATE PointsEntity SET yValue = :yValue " +
                            "WHERE function.funcId = :funcId AND xValue = :xValue");
            query.setParameter("yValue", newYValue);
            query.setParameter("funcId", functionId);
            query.setParameter("xValue", xValue);

            query.executeUpdate();
            transaction.commit();
        }
    }

    public void deleteById(int functionId, double xValue) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            Query<?> query = session.createQuery(
                    "DELETE FROM PointsEntity WHERE function.funcId = :funcId AND xValue = :xValue");
            query.setParameter("funcId", functionId);
            query.setParameter("xValue", xValue);
            query.executeUpdate();

            transaction.commit();
        }
    }


        public void deleteByFunction(FunctionEntity function) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Query<?> query = session.createQuery(
                    "DELETE FROM PointsEntity WHERE function = :function");
            query.setParameter("function", function);
            query.executeUpdate();
            transaction.commit();
        }
    }
    public long countByFunction(FunctionEntity function) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "SELECT COUNT(p) FROM PointsEntity p WHERE p.function = :function", Long.class)
            .setParameter("function", function)
            .uniqueResult();
        }
    }



}
