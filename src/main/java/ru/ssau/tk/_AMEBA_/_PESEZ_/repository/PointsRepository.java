package ru.ssau.tk._AMEBA_._PESEZ_.repository;

import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.PointsEntity;

import java.util.List;
import java.util.Optional;
@Repository
public class PointsRepository {

    private final SessionFactory sessionFactory;

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
            // Используем правильный синтаксис для составного ключа
            Query<PointsEntity> query = session.createQuery(
                    "FROM PointsEntity p WHERE p.function = :function AND p.id.xValue = :xValue",
                    PointsEntity.class);
            query.setParameter("function", function);
            query.setParameter("xValue", xValue);

            List<PointsEntity> results = query.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        }
    }

    public List<PointsEntity> findByFunction(FunctionEntity function) {
        try (Session session = sessionFactory.openSession()) {
            Query<PointsEntity> query = session.createQuery(
                    "FROM PointsEntity p WHERE p.function = :function ORDER BY p.id.xValue",
                    PointsEntity.class);
            query.setParameter("function", function);
            return query.getResultList();
        }
    }

    public void updateById(Long functionId, double xValue, Double newYValue) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            Query<?> query = session.createQuery(
                    "UPDATE PointsEntity p SET p.yValue = :yValue " +
                            "WHERE p.function.funcId = :funcId AND p.id.xValue = :xValue");
            query.setParameter("yValue", newYValue);
            query.setParameter("funcId", functionId);
            query.setParameter("xValue", xValue);

            query.executeUpdate();
            transaction.commit();
        }
    }

    public void deleteById(Long functionId, double xValue) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            Query<?> query = session.createQuery(
                    "DELETE FROM PointsEntity p WHERE p.function.funcId = :funcId AND p.id.xValue = :xValue");
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
                    "DELETE FROM PointsEntity p WHERE p.function = :function");
            query.setParameter("function", function);
            query.executeUpdate();
            transaction.commit();
        }
    }

    public long countByFunction(FunctionEntity function) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(p) FROM PointsEntity p WHERE p.function = :function", Long.class);
            query.setParameter("function", function);
            return query.uniqueResult();
        }
    }

    public List<PointsEntity> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<PointsEntity> query = session.createQuery("FROM PointsEntity", PointsEntity.class);
            return query.getResultList();
        }
    }

    public void saveAll(List<PointsEntity> points) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            for (int i = 0; i < points.size(); i++) {
                session.persist(points.get(i));
                if (i % 50 == 0) {
                    session.flush();
                    session.clear();
                }
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error saving points batch", e);
        } finally {
            session.close();
        }
    }
}