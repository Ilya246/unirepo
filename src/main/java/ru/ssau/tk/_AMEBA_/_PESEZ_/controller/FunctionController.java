package ru.ssau.tk._AMEBA_._PESEZ_.controller;

import ru.ssau.tk._AMEBA_._PESEZ_.dto.FunctionDTO;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.*;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.response.FunctionResponse;
import ru.ssau.tk._AMEBA_._PESEZ_.service.FunctionService;
import static ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Enumeration;

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
            if (!checkRequiredRole(req, resp, UserType.Admin)) {
                return;
            }
            // GET /functions?id={id}
            if (path == null || path.isEmpty()) {
                if (!req.getParameterMap().containsKey("id")) {
                    FunctionDTO[] funcs = functionService.getAllFunctions().join();
                    resp.getWriter().write(objectMapper.writeValueAsString(funcs));
                    return;
                }
                int id = Integer.parseInt(req.getParameter("id"));
                FunctionDTO function = functionService.getFunction(id).join();
                resp.getWriter().write(objectMapper.writeValueAsString(function));
            // GET /functions/calculate?id={id}&x={x}
            } else if (path.equals("/calculate")) {
                int id = Integer.parseInt(req.getParameter("id"));
                double x = Double.parseDouble(req.getParameter("x"));
                double result = functionService.calculateFunction(id, x).join();
                resp.getWriter().write(objectMapper.writeValueAsString(result));
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
            if (!checkRequiredRole(req, resp, UserType.Admin)) {
                return;
            }
            switch (path) {
                // POST /functions/math
                case "/math" -> {
                    MathFunctionCreateRequest request = parseBody(req, MathFunctionCreateRequest.class);
                    int response = functionService.createMathFunction(request.expression).join();
                    resp.getWriter().write(objectMapper.writeValueAsString(new FunctionResponse(response)));
                }
                // POST /functions/tabulated
                case "/tabulated" -> {
                    TabulatedFunctionCreateRequest request = parseBody(req, TabulatedFunctionCreateRequest.class);
                    int response = functionService.createTabulated(request.expression, request.xFrom, request.xTo, request.pointCount).join();
                    resp.getWriter().write(objectMapper.writeValueAsString(new FunctionResponse(response)));
                }
                // POST /functions/pure-tabulated
                case "/pure-tabulated" -> {
                    PureTabulatedCreateRequest request = parseBody(req, PureTabulatedCreateRequest.class);
                    int response = functionService.createPureTabulated(request.xValues, request.yValues).join();
                    resp.getWriter().write(objectMapper.writeValueAsString(new FunctionResponse(response)));
                }
                // POST /functions/composite
                case "/composite" -> {
                    CompositeFunctionCreateRequest request = parseBody(req, CompositeFunctionCreateRequest.class);
                    int response = functionService.createComposite(request.innerId, request.outerId).join();
                    resp.getWriter().write(objectMapper.writeValueAsString(new FunctionResponse(response)));
                }
                default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}