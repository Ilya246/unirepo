package ru.ssau.tk._AMEBA_._PESEZ_.utility;

import org.apache.logging.log4j.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Utility {
    public static final Logger Log = LogManager.getLogger();
    public static final MessageDigest Hasher;
    public static final Base64.Encoder Base64Encode = Base64.getEncoder();
    public static final Base64.Decoder Base64Decode = Base64.getDecoder();

    static {
        try {
            Hasher = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getBase64Hash(String from) {
        return Base64Encode.encodeToString(Hasher.digest(from.getBytes()));
    }

    public static String getErrorInitMessage(Throwable error) {
        while (true) {
            Throwable cause = error.getCause();
            if (cause == null)
                return error.getMessage();
            error = cause;
        }
    }
}
