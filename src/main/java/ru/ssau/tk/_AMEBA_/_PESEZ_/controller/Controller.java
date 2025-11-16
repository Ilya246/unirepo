package ru.ssau.tk._AMEBA_._PESEZ_.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.UserDTO;
import ru.ssau.tk._AMEBA_._PESEZ_.service.UserService;
import static ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Base64;

public abstract class Controller extends HttpServlet {
    protected ObjectMapper objectMapper;
    protected UserService userService;

    @Override
    public void init() {
        this.objectMapper = new ObjectMapper();
        this.userService = new UserService("main.properties");
    }

    protected <T> T parseBody(HttpServletRequest req, Class<T> classT) throws IOException {
        return objectMapper.readValue(req.getInputStream(), classT);
    }

    private static final String AUTH_HEADER = "Authorization";
    private static final String BASIC_PREFIX = "Basic ";

    public UserDTO authenticate(HttpServletRequest req) {
        String authHeader = req.getHeader(AUTH_HEADER);
        if (authHeader == null || !authHeader.startsWith(BASIC_PREFIX))
            return null;

        try {
            String base64Credentials = authHeader.substring(BASIC_PREFIX.length());
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded);

            final String[] values = credentials.split(":");
            if (values.length != 2)
                return null;

            String username = values[0];
            String password = values[1];
            // Get all users and find matching credentials
            return userService.getUserByCredentials(username, password).join();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean hasRequiredRole(UserDTO user, UserType requiredRole) {
        return user != null && user.userType == requiredRole;
    }

    public boolean hasRequiredRole(HttpServletRequest req, UserType requiredRole) {
        return hasRequiredRole(authenticate(req), requiredRole);
    }
}
