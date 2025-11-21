package ru.ssau.tk._AMEBA_._PESEZ_.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.UserDTO;
import ru.ssau.tk._AMEBA_._PESEZ_.service.UserService;
import static ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository.*;
import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;

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
            byte[] credDecoded = Base64Decode.decode(base64Credentials);
            String credentials = new String(credDecoded);

            final String[] values = credentials.split(":");
            if (values.length != 2)
                return null;

            String username = values[0];
            String password = values[1];
            if (isLocalhost(req.getRemoteAddr()) && username.equals("localhost") && password.equals("localhost")) {
                return new UserDTO(0, UserType.Admin, "localhost", "localhost", new Timestamp(0));
            }
            return userService.getUserByCredentials(username, password).join();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean hasRequiredRole(UserDTO user, UserType requiredRole) {
        return user != null && (requiredRole == UserType.Normal || user.userType == requiredRole);
    }

    public boolean hasRequiredRole(HttpServletRequest req, UserType requiredRole) {
        return hasRequiredRole(authenticate(req), requiredRole);
    }

    public boolean checkRequiredRole(UserDTO user, HttpServletResponse resp, UserType requiredRole) throws IOException {
        if (user == null) {
            resp.setHeader("WWW-Authenticate", "Basic realm=\"Restricted Area\"");
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        if (!hasRequiredRole(user, requiredRole)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }

    public boolean checkRequiredRole(HttpServletRequest req, HttpServletResponse resp, UserType requiredRole) throws IOException {
        return checkRequiredRole(authenticate(req), resp, requiredRole);
    }

    protected static String getPage(String filename) {
        try (FileInputStream instream = new FileInputStream("src/main/webapp/" + filename)) {
            return new String(instream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException | NullPointerException e) {
            Log.error("Failed to read HTML file:", e);
            return null;
        }
    }

    public static boolean isLocalhost(String addr) {
        return addr.equals("127.0.0.1") || addr.equals("0:0:0:0:0:0:0:1");
    }
}
