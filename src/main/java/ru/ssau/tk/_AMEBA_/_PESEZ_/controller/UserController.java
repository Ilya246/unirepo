package ru.ssau.tk._AMEBA_._PESEZ_.controller;

import ru.ssau.tk._AMEBA_._PESEZ_.dto.UserDTO;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.*;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository;
import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.*;
import static ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

@WebServlet("/users/*")
public class UserController extends Controller {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json");
        try {
            if (path.equals("/self")) {
                UserDTO user = authenticate(req);
                if (!checkRequiredRole(user, resp, UserType.Normal))
                    return;

                resp.getWriter().write(objectMapper.writeValueAsString(user.userId));
                return;
            }
            if (!checkRequiredRole(req, resp, UserType.Admin)) {
                return;
            }
            // GET /users
            if (path == null || path.isEmpty()) {
                UserDTO[] result = userService.getUsers().join();
                resp.getWriter().write(objectMapper.writeValueAsString(result));
            // GET /users/user?id={id}
            } else if (path.matches("/user")) {
                int id = Integer.parseInt(req.getParameter("id"));
                UserDTO user = userService.getUser(id).join();
                resp.getWriter().write(objectMapper.writeValueAsString(user));
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            Log.info("Got bad user GET request: {}, error:", req.getRequestURI(), e);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json");
        try {
            // POST /users
            if (path == null || path.isEmpty()) {
                if (!checkRequiredRole(req, resp, UserRepository.UserType.Admin)) {
                    return;
                }
                UserCreateRequest request = parseBody(req, UserCreateRequest.class);
                int response = userService.createUser(request.userType, request.username, request.password).join();
                resp.getWriter().write(objectMapper.writeValueAsString(response));
                Log.info("Registered new user {} of role {} with ID {}", request.username, request.userType, response);
            // POST /users/register?username={username}&password={password}
            } else if (path.equals("/register")) {
                String username = req.getParameter("username");
                String password = req.getParameter("password");
                int response = userService.createUser(UserRepository.UserType.Normal, username, password).join();
                resp.getWriter().write(objectMapper.writeValueAsString(response));
                Log.info("Registered new user {} with ID {}", username, response);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            Log.info("Got bad user POST request: {}, error:", req.getRequestURI(), e);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json");
        try {
            if (!checkRequiredRole(req, resp, UserType.Admin)) {
                return;
            }
            // DELETE /users?id={id}
            if (path == null || path.isEmpty()) {
                int id = Integer.parseInt(req.getParameter("id"));
                userService.deleteUser(id).join();
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            Log.info("Got bad user DELETE request: {}, error:", req.getRequestURI(), e);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json");
        try {
            if (!checkRequiredRole(req, resp, UserType.Admin)) {
                return;
            }
            // PUT /users?id={id}
            if (path == null || path.isEmpty()) {
                int id = Integer.parseInt(req.getParameter("id"));
                UserChangeRequest request = parseBody(req, UserChangeRequest.class);
                userService.updateUser(id, request.username, request.password, request.userType).join();
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            Log.info("Got bad user PUT request: {}, error:", req.getRequestURI(), e);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
