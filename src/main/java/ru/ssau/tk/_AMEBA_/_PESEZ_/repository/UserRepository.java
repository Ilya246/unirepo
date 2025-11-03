package ru.ssau.tk._AMEBA_._PESEZ_.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.FunctionEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;


import java.util.List;

public class UserRepository {

    private final SessionFactory sessionFactory;

    // Передаём SessionFactory при создании репозитория
    public UserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(UserEntity user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
        }
    }

    public UserEntity findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(UserEntity.class, id);
        }
    }

    public List<UserEntity> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM UserEntity", UserEntity.class).list();
        }
    }

    public void deleteById(int userId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            UserEntity user = session.find(UserEntity.class, userId);
            if (user != null) session.remove(user);
            transaction.commit();
        }
    }

    public UserEntity update(UserEntity user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            UserEntity merged = session.merge(user);
            transaction.commit();
            return merged;
        }
    }
    public List<UserEntity> findByType(int typeId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "FROM UserEntity WHERE typeId = :typeId", UserEntity.class)
                    .setParameter("typeId", typeId)
                    .list();
        }
    }
    public List<UserEntity> findAllOrderByCreatedDate(boolean descending) {
        try (Session session = sessionFactory.openSession()) {
            String order = descending ? "DESC" : "ASC";
            return session.createQuery(
                            "FROM UserEntity ORDER BY createdDate " + order, UserEntity.class)
                    .list();
        }
    }
}
