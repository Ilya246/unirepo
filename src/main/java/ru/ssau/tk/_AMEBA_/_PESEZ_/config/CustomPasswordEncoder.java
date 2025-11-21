package ru.ssau.tk._AMEBA_._PESEZ_.config;


import org.springframework.security.crypto.password.PasswordEncoder;
import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.getBase64Hash;

public class CustomPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return getBase64Hash(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String hashedRawPassword = getBase64Hash(rawPassword.toString());
        return hashedRawPassword.equals(encodedPassword);
    }
}
