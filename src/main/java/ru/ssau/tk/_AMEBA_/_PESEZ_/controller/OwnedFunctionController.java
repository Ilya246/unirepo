package ru.ssau.tk._AMEBA_._PESEZ_.controller;

import ru.ssau.tk._AMEBA_._PESEZ_.dto.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.*;
import ru.ssau.tk._AMEBA_._PESEZ_.service.UserService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

@WebServlet("/owned-functions")
public class OwnedFunctionController extends Controller {
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
            // GET /owned-functions?id={id}&user={userId}
            if (path == null || path.isEmpty()) {
                int id = Integer.parseInt(req.getParameter("id"));
                int userId = Integer.parseInt(req.getParameter("user"));
                OwnedFunctionDTO function = userService.getUserFunction(userId, id).join();
                resp.getWriter().write(objectMapper.writeValueAsString(function));
            // GET /owned-functions/user?user={userId}
            } else if (path.equals("/user")) {
                int userId = Integer.parseInt(req.getParameter("user"));
                OwnedFunctionDTO[] functions = userService.getUserFunctions(userId).join();
                resp.getWriter().write(objectMapper.writeValueAsString(functions));
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
            switch (path) {
                // POST /owned-functions/math?user={userId}
                case "/math" -> {
                    int userId = Integer.parseInt(req.getParameter("user"));
                    OwnedFunctionCreateRequest request = parseBody(req, OwnedFunctionCreateRequest.class);
                    int response = userService.createUserFunction(userId, request.name, ((MathFunctionCreateRequest)request.funcParams).expression).join();
                    resp.getWriter().write(response);
                }
                // POST /owned-functions/tabulated?user={userId}
                case "/tabulated" -> {
                    int userId = Integer.parseInt(req.getParameter("user"));
                    OwnedFunctionCreateRequest request = parseBody(req, OwnedFunctionCreateRequest.class);
                    var params = (TabulatedFunctionCreateRequest)request.funcParams;
                    int response = userService.createUserTabulatedFunction(userId, request.name, params.expression, params.xFrom, params.xTo, params.pointCount).join();
                    resp.getWriter().write(response);
                }
                // POST /owned-functions/pure-tabulated?user={userId}
                case "/pure-tabulated" -> {
                    int userId = Integer.parseInt(req.getParameter("user"));
                    OwnedFunctionCreateRequest request = parseBody(req, OwnedFunctionCreateRequest.class);
                    var params = (PureTabulatedCreateRequest)request.funcParams;
                    int response = userService.createUserPureTabulated(userId, request.name, params.xValues, params.yValues).join();
                    resp.getWriter().write(response);
                }
                // POST /owned-functions/composite?user={userId}
                case "/composite" -> {
                    int userId = Integer.parseInt(req.getParameter("user"));
                    OwnedFunctionCreateRequest request = parseBody(req, OwnedFunctionCreateRequest.class);
                    var params = (CompositeFunctionCreateRequest)request.funcParams;
                    int response = userService.createUserComposite(userId, request.name, params.innerId, params.outerId).join();
                    resp.getWriter().write(response);
                }
                // POST /owned-functions/own?id={id}&user={userId}&name={name}
                case "/own" -> {
                    int id = Integer.parseInt(req.getParameter("id"));
                    int userId = Integer.parseInt(req.getParameter("user"));
                    String name = req.getParameter("name");
                    userService.addOwnership(userId, id, name).join();
                    resp.setStatus(HttpServletResponse.SC_OK);
                }
                default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        // PUT /owned_functions?id={id}&user={userId}&name={name}
        if (path == null || path.isEmpty()) {
            int id = Integer.parseInt(req.getParameter("id"));
            int userId = Integer.parseInt(req.getParameter("user"));
            String name = req.getParameter("name");
            userService.updateOwnership(userId, id, name).join();
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
