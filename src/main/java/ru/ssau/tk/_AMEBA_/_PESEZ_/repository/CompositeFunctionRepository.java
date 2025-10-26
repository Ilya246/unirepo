package ru.ssau.tk._AMEBA_._PESEZ_.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.CompositeFunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.utility.HibernateSessionFactoryUtil;

import java.util.List;
import java.util.Optional;

public class CompositeFunctionRepository {

    private final SessionFactory sessionFactory;

    // Передаём SessionFactory при создании репозитория
    public CompositeFunctionRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(CompositeFunctionEntity function){
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(function);
            transaction.commit();
        }
    }
    public Optional<CompositeFunctionEntity> findById(int compositeFunctionId) {
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
    public void deleteById(int compositeFunctionId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            CompositeFunctionEntity composite = session.find(CompositeFunctionEntity.class, compositeFunctionId);
            if (composite != null) {
                session.remove(composite);
            }
            transaction.commit();
        }
    }





}
