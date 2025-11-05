package ru.ssau.tk._AMEBA_._PESEZ_.utility;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.*;

public class TestHibernateSessionFactoryUtil {
    private static SessionFactory sessionFactory;

    private TestHibernateSessionFactoryUtil() {}

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                        .configure("hibernate.test.cfg.xml")
                        .build();

                MetadataSources sources = new MetadataSources(registry);

                // Регистрация всех сущностей
                sources.addAnnotatedClass(UserEntity.class);
                sources.addAnnotatedClass(FunctionEntity.class);
                sources.addAnnotatedClass(FunctionOwnershipEntity.class);
                sources.addAnnotatedClass(FunctionOwnershipId.class);
                sources.addAnnotatedClass(CompositeFunctionEntity.class);
                sources.addAnnotatedClass(PointsEntity.class);
                sources.addAnnotatedClass(PointId.class);

                Metadata metadata = sources.getMetadataBuilder().build();
                sessionFactory = metadata.getSessionFactoryBuilder().build();


            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to create Hibernate SessionFactory", e);
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
