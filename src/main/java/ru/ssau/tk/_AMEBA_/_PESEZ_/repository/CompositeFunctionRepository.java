package ru.ssau.tk._AMEBA_._PESEZ_.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.CompositeFunctionEntity;

import java.util.List;
import java.util.Optional;

@Repository
public class CompositeFunctionRepository {

    private final SessionFactory sessionFactory;

    public CompositeFunctionRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public CompositeFunctionEntity save(CompositeFunctionEntity function) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(function);
            transaction.commit();
            return function;
        }
    }

    public Optional<CompositeFunctionEntity> findById(Long compositeFunctionId) {
        try (Session session = sessionFactory.openSession()) {
            CompositeFunctionEntity composite = session.find(CompositeFunctionEntity.class, compositeFunctionId);
            return Optional.ofNullable(composite);
        }
    }

    public List<CompositeFunctionEntity> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM CompositeFunctionEntity", CompositeFunctionEntity.class).list();
        }
    }

    public CompositeFunctionEntity update(CompositeFunctionEntity compositeFunction) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            CompositeFunctionEntity merged = session.merge(compositeFunction);
            transaction.commit();
            return merged;
        }
    }

    public void delete(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            CompositeFunctionEntity entity = session.find(CompositeFunctionEntity.class, id);
            if (entity != null) {
                session.remove(entity);
            }
            transaction.commit();
        }
    }
}