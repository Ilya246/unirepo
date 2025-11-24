package ru.ssau.tk._AMEBA_._PESEZ_.controller;

import ru.ssau.tk._AMEBA_._PESEZ_.dto.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.*;
import ru.ssau.tk._AMEBA_._PESEZ_.service.FunctionService;

import static ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository.*;
import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/functions/*")
public class FunctionController extends Controller {
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
            if (!checkRequiredRole(user, resp, UserType.Normal))
                return;
            String param = paramOrNull(req, "id");
            Integer id = param == null ? null : Integer.parseInt(param);
            if (id == null) {
                if (!checkRequiredRole(user, resp, UserType.Admin))
                    return;
            } else if (!hasRequiredRole(user, UserType.Admin)) {
                if (userService.getUserFunction(user.userId, id) == null)
                    throw new RuntimeException("No such function for this user");
            }
            // GET /functions(?id={id})
            if (path == null || path.isEmpty()) {
                if (id == null) {
                    FunctionDTO[] functions = functionService.getAllFunctions().join();
                    resp.getWriter().write(objectMapper.writeValueAsString(functions));
                    return;
                }
                FunctionDTO function = functionService.getFunction(id).join();
                resp.getWriter().write(objectMapper.writeValueAsString(function));
                return;
            }
            if (id == null)
                throw new RuntimeException("Missing parameter id");

            // GET /functions/calculate?id={id}&x={x}
            if (path.equals("/calculate")) {
                double x = Double.parseDouble(req.getParameter("x"));
                double result = functionService.calculateFunction(id, x).join();
                resp.getWriter().write(objectMapper.writeValueAsString(new ResultResponse(result)));
            // GET /functions/calculaterange?id={id}&from={from}&to={to}&pts={pts}
            } else if (path.equals("/calculaterange")) {
                double from = Double.parseDouble(req.getParameter("from"));
                double to = Double.parseDouble(req.getParameter("to"));
                int pts = Integer.parseInt(req.getParameter("pts"));
                PointsDTO results = functionService.calculateFunctionRange(id, from, to, pts).join();
                resp.getWriter().write(objectMapper.writeValueAsString(results));
            } else if (path.equals("/composite")) {
                CompositeFunctionDTO data = functionService.getCompositeData(id).join();
                resp.getWriter().write(objectMapper.writeValueAsString(new CompositeFunctionResponse(data.outerFuncId, data.innerFuncId)));
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
            if (!checkRequiredRole(req, resp, UserType.Admin)) {
                return;
            }
            switch (path) {
                // POST /functions/math
                case "/math" -> {
                    MathFunctionCreateRequest request = parseBody(req, MathFunctionCreateRequest.class);
                    int response = functionService.createMathFunction(request.expression).join();
                    resp.getWriter().write(objectMapper.writeValueAsString(new IdResponse(response)));
                }
                // POST /functions/tabulated
                case "/tabulated" -> {
                    TabulatedFunctionCreateRequest request = parseBody(req, TabulatedFunctionCreateRequest.class);
                    int response = functionService.createTabulated(request.expression, request.xFrom, request.xTo, request.pointCount).join();
                    resp.getWriter().write(objectMapper.writeValueAsString(new IdResponse(response)));
                }
                // POST /functions/pure-tabulated
                case "/pure-tabulated" -> {
                    PureTabulatedCreateRequest request = parseBody(req, PureTabulatedCreateRequest.class);
                    int response = functionService.createPureTabulated(request.xValues, request.yValues).join();
                    resp.getWriter().write(objectMapper.writeValueAsString(new IdResponse(response)));
                }
                // POST /functions/composite
                case "/composite" -> {
                    CompositeFunctionCreateRequest request = parseBody(req, CompositeFunctionCreateRequest.class);
                    int response = functionService.createComposite(request.innerId, request.outerId).join();
                    resp.getWriter().write(objectMapper.writeValueAsString(new IdResponse(response)));
                }
                default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(getErrorInitMessage(e));
        }
    }
}