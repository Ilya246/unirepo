package ru.ssau.tk._AMEBA_._PESEZ_.utility;

import org.hibernate.SessionFactory;
import ru.ssau.tk._AMEBA_._PESEZ_.entity.UserEntity;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository;

import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.getBase64Hash;

public class Data {

    public static void main(String[] args){
        SessionFactory sessionFactory= HibernateSessionFactoryUtil.getSessionFactory();
        UserRepository userRepository = new UserRepository(sessionFactory);
        String password2=getBase64Hash("localhost");
        UserEntity admin = new UserEntity(2, "localhost", password2);
        userRepository.save(admin);
    }
}
