package ru.ssau.tk._AMEBA_._PESEZ_.controller;

import ru.ssau.tk._AMEBA_._PESEZ_.dto.UserDTO;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.*;
import ru.ssau.tk._AMEBA_._PESEZ_.service.UserService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

@WebServlet("/users")
public class UserController extends Controller {
    private UserService userService;

    @Override
    public void init() {
        super.init();
        this.userService = new UserService("main.properties");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json");
        try {
            // GET /users/user?id={id}
            if (path.matches("/user")) {
                int id = Integer.parseInt(req.getParameter("id"));
                UserDTO user = userService.getUser(id).join();
                resp.getWriter().write(objectMapper.writeValueAsString(user));
            // GET /users
            } else if (path.matches("")) {
                UserDTO[] result = userService.getUsers().join();
                resp.getWriter().write(objectMapper.writeValueAsString(result));
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json");
        try {
            // POST /users
            if (path.isEmpty()) {
                UserCreateRequest request = parseBody(req, UserCreateRequest.class);
                int response = userService.createUser(request.userType, request.username, request.password).join();
                resp.getWriter().write(response);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json");
        try {
            // DELETE /users?id={id}
            if (path.isEmpty()) {
                int id = Integer.parseInt(req.getParameter("id"));
                userService.deleteUser(id).join();
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json");
        try {
            // PUT /users?id={id}
            if (path.isEmpty()) {
                int id = Integer.parseInt(req.getParameter("id"));
                UserChangeRequest request = parseBody(req, UserChangeRequest.class);
                userService.updateUser(id, request.username, request.password, request.userType).join();
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
