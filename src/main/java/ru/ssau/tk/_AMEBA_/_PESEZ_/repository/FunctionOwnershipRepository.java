package ru.ssau.tk._AMEBA_._PESEZ_.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.*;
import ru.ssau.tk._AMEBA_._PESEZ_.utility.HibernateSessionFactoryUtil;

import java.util.List;
import java.util.Optional;

public class FunctionOwnershipRepository {

    private final SessionFactory sessionFactory;

    // Передаём SessionFactory при создании репозитория
    public FunctionOwnershipRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(FunctionOwnershipEntity ownership){
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(ownership);
            transaction.commit();
        }
    }

    public Optional<FunctionOwnershipEntity> findById(Long userId, Long functionId) {
        try (Session session = sessionFactory.openSession()) {
            FunctionOwnershipId id = new FunctionOwnershipId(userId, functionId);
            FunctionOwnershipEntity ownership = session.find(FunctionOwnershipEntity.class, id);
            return Optional.ofNullable(ownership);
        }
    }

    public List<FunctionOwnershipEntity> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM FunctionOwnershipEntity", FunctionOwnershipEntity.class).list();
        }
    }

    public void deleteById(Long userId, Long functionId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            // Для составного ключа нужно найти сущность по компонентам ключа
            Query<FunctionOwnershipEntity> query = session.createQuery(
                    "FROM FunctionOwnershipEntity WHERE id.userId = :userId AND id.funcId = :funcId",
                    FunctionOwnershipEntity.class);
            query.setParameter("userId", userId);
            query.setParameter("funcId", functionId);

            FunctionOwnershipEntity ownership = query.uniqueResult();
            if (ownership != null) {
                session.remove(ownership);
            }

            transaction.commit();
        }
    }

    public void deleteByFuncId(Long functionId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            // Для составного ключа нужно найти сущность по компонентам ключа
            Query<FunctionOwnershipEntity> query = session.createQuery(
                    "FROM FunctionOwnershipEntity WHERE id.funcId = :funcId",
                    FunctionOwnershipEntity.class);
            query.setParameter("funcId", functionId);

            FunctionOwnershipEntity ownership = query.uniqueResult();
            if (ownership != null) {
                session.remove(ownership);
            }

            transaction.commit();
        }
    }

    public void updateById(Long userId, Long functionId, String newFuncName) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            Query<?> query = session.createQuery(
                    "UPDATE FunctionOwnershipEntity SET funcName = :funcName " +
                            "WHERE id.userId = :userId AND id.funcId = :funcId");
            query.setParameter("funcName", newFuncName);
            query.setParameter("userId", userId);
            query.setParameter("funcId", functionId);

            query.executeUpdate();
            transaction.commit();
        }
    }
    public List<FunctionOwnershipEntity> findByUserId(Long userId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "FROM FunctionOwnershipEntity WHERE id.userId = :userId",
                            FunctionOwnershipEntity.class)
                    .setParameter("userId", userId)
                    .list();
        }
    }
    public Optional<UserEntity> findOwnerByFunctionId(Long functionId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "SELECT u FROM FunctionOwnershipEntity fo " +
                                    "JOIN UserEntity u ON u.userId = fo.id.userId " +
                                    "WHERE fo.id.funcId = :functionId",
                            UserEntity.class)
                    .setParameter("functionId", functionId)
                    .uniqueResultOptional();
        }
    }
    public List<FunctionEntity> findUserFunctionsOrderByDate(Long userId, boolean descending) {
        try (Session session = sessionFactory.openSession()) {
            String order = descending ? "DESC" : "ASC";
            return session.createQuery(
                            "SELECT fo.function FROM FunctionOwnershipEntity fo " +
                                    "WHERE fo.id.userId = :userId " +
                                    "ORDER BY fo.createdDate " + order,
                            FunctionEntity.class)
                    .setParameter("userId", userId)
                    .list();
        }
    }
}
