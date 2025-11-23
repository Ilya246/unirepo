package ru.ssau.tk._AMEBA_._PESEZ_.controller;

import ru.ssau.tk._AMEBA_._PESEZ_.dto.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.CompositeFunctionResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.IdResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.service.FunctionService;

import static ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository.*;
import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

@WebServlet("/owned-functions/*")
public class OwnedFunctionController extends Controller {
    private FunctionService functionService;

    @Override
    public void init() {
        super.init();
        this.functionService = new FunctionService("main.properties");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json");
        try {
            UserDTO user = authenticate(req);
            if (!checkRequiredRole(user, resp, UserType.Normal)) {
                return;
            }
            int userId = user.userId;
            if (hasRequiredRole(req, UserType.Admin) && req.getParameterMap().containsKey("user")) {
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
            } else if (path.equals("/composite")) {
                int id = Integer.parseInt(req.getParameter("id"));
                if (userService.getUserFunction(userId, id) == null) {
                    resp.getWriter().write(objectMapper.writeValueAsString(null));
                }
                CompositeFunctionDTO composite = functionService.getCompositeData(id).join();
                resp.getWriter().write(objectMapper.writeValueAsString(new CompositeFunctionResponse(composite.outerFuncId, composite.innerFuncId)));
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(getErrorInitMessage(e));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json");
        try {
            UserDTO user = authenticate(req);
            if (!checkRequiredRole(user, resp, UserType.Normal)) {
                return;
            }
            int userId = user.userId;
            if (hasRequiredRole(req, UserType.Admin) && req.getParameterMap().containsKey("userId")) {
                userId = Integer.parseInt(req.getParameter("userId"));
            }

            switch (path) {
                // POST /owned-functions/math
                case "/math" -> {
                    OwnedFunctionCreateRequest request = parseBody(req, OwnedFunctionCreateRequest.class);
                    int response = userService.createUserFunction(userId, request.name, ((MathFunctionCreateRequest)request.funcParams).expression).join();
                    resp.getWriter().write(objectMapper.writeValueAsString(new IdResponse(response)));
                }
                // POST /owned-functions/tabulated
                case "/tabulated" -> {
                    OwnedFunctionCreateRequest request = parseBody(req, OwnedFunctionCreateRequest.class);
                    var params = (TabulatedFunctionCreateRequest)request.funcParams;
                    int response = userService.createUserTabulatedFunction(userId, request.name, params.expression, params.xFrom, params.xTo, params.pointCount).join();
                    resp.getWriter().write(objectMapper.writeValueAsString(new IdResponse(response)));
                }
                // POST /owned-functions/pure-tabulated
                case "/pure-tabulated" -> {
                    OwnedFunctionCreateRequest request = parseBody(req, OwnedFunctionCreateRequest.class);
                    var params = (PureTabulatedCreateRequest)request.funcParams;
                    int response = userService.createUserPureTabulated(userId, request.name, params.xValues, params.yValues).join();
                    resp.getWriter().write(objectMapper.writeValueAsString(new IdResponse(response)));
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
                    resp.getWriter().write(objectMapper.writeValueAsString(new IdResponse(response)));
                }
                // POST /owned-functions/own?id={id}&name={name}
                case "/own" -> {
                    if (!checkRequiredRole(user, resp, UserType.Admin)) {
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
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(getErrorInitMessage(e));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        try {
            UserDTO user = authenticate(req);
            if (!checkRequiredRole(user, resp, UserType.Normal)) {
                return;
            }
            int userId = user.userId;
            if (hasRequiredRole(req, UserType.Admin) && req.getParameterMap().containsKey("user")) {
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
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(getErrorInitMessage(e));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        try {
            UserDTO user = authenticate(req);
            if (!checkRequiredRole(user, resp, UserType.Normal)) {
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
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(getErrorInitMessage(e));
        }
    }
}
