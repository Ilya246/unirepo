package ru.ssau.tk._AMEBA_._PESEZ_.controller;

import ru.ssau.tk._AMEBA_._PESEZ_.dto.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.*;

import static ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

@WebServlet("/owned-functions/*")
public class OwnedFunctionController extends Controller {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json");
        try {
            UserDTO user = authenticate(req);
            if (user == null) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            int userId = user.userId;
            if (hasRequiredRole(user, UserType.Admin) && req.getParameterMap().containsKey("user")) {
                userId = Integer.parseInt(req.getParameter("user"));
            }

            // GET /owned-functions?id={id}
            if (path == null || path.isEmpty()) {
                int id = Integer.parseInt(req.getParameter("id"));
                OwnedFunctionDTO function = userService.getUserFunction(userId, id).join();
                resp.getWriter().write(objectMapper.writeValueAsString(function));
            // GET /owned-functions/user
            } else if (path.equals("/user")) {
                OwnedFunctionDTO[] functions = userService.getUserFunctions(userId).join();
                resp.getWriter().write(objectMapper.writeValueAsString(functions));
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json");
        try {
            UserDTO user = authenticate(req);
            if (user == null) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            int userId = user.userId;
            if (hasRequiredRole(user, UserType.Admin) && req.getParameterMap().containsKey("user")) {
                userId = Integer.parseInt(req.getParameter("user"));
            }

            switch (path) {
                // POST /owned-functions/math
                case "/math" -> {
                    OwnedFunctionCreateRequest request = parseBody(req, OwnedFunctionCreateRequest.class);
                    int response = userService.createUserFunction(userId, request.name, ((MathFunctionCreateRequest)request.funcParams).expression).join();
                    resp.getWriter().write(objectMapper.writeValueAsString(response));
                }
                // POST /owned-functions/tabulated
                case "/tabulated" -> {
                    OwnedFunctionCreateRequest request = parseBody(req, OwnedFunctionCreateRequest.class);
                    var params = (TabulatedFunctionCreateRequest)request.funcParams;
                    int response = userService.createUserTabulatedFunction(userId, request.name, params.expression, params.xFrom, params.xTo, params.pointCount).join();
                    resp.getWriter().write(objectMapper.writeValueAsString(response));
                }
                // POST /owned-functions/pure-tabulated
                case "/pure-tabulated" -> {
                    OwnedFunctionCreateRequest request = parseBody(req, OwnedFunctionCreateRequest.class);
                    var params = (PureTabulatedCreateRequest)request.funcParams;
                    int response = userService.createUserPureTabulated(userId, request.name, params.xValues, params.yValues).join();
                    resp.getWriter().write(objectMapper.writeValueAsString(response));
                }
                // POST /owned-functions/composite
                case "/composite" -> {
                    OwnedFunctionCreateRequest request = parseBody(req, OwnedFunctionCreateRequest.class);
                    var params = (CompositeFunctionCreateRequest)request.funcParams;
                    if (!hasRequiredRole(req, UserType.Admin) && (
                        userService.getUserFunction(userId, params.innerId) == null
                        || userService.getUserFunction(userId, params.outerId) == null
                    )) {
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid function IDs");
                    }
                    int response = userService.createUserComposite(userId, request.name, params.innerId, params.outerId).join();
                    resp.getWriter().write(objectMapper.writeValueAsString(response));
                }
                // POST /owned-functions/own?id={id}&name={name}
                case "/own" -> {
                    if (!hasRequiredRole(user, UserType.Admin)) {
                        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                        return;
                    }
                    int id = Integer.parseInt(req.getParameter("id"));
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
        try {
            UserDTO user = authenticate(req);
            if (user == null) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            int userId = user.userId;
            if (hasRequiredRole(user, UserType.Admin) && req.getParameterMap().containsKey("user")) {
                userId = Integer.parseInt(req.getParameter("user"));
            }

            // PUT /owned_functions?id={id}&name={name}
            if (path == null || path.isEmpty()) {
                int id = Integer.parseInt(req.getParameter("id"));
                String name = req.getParameter("name");
                userService.updateOwnership(userId, id, name).join();
                resp.setStatus(HttpServletResponse.SC_OK);
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
        try {
            UserDTO user = authenticate(req);
            if (user == null) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            int userId = user.userId;
            if (hasRequiredRole(user, UserType.Admin) && req.getParameterMap().containsKey("user")) {
                userId = Integer.parseInt(req.getParameter("user"));
            }
            // DELETE /owned-functions?id={id}
            if (path == null || path.isEmpty()) {
                int id = Integer.parseInt(req.getParameter("id"));
                userService.deleteFunctionOwnership(userId, id);
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
