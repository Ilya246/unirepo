package ru.ssau.tk._AMEBA_._PESEZ_.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.FunctionDTO;
import ru.ssau.tk._AMEBA_._PESEZ_.dto.request.*;
import ru.ssau.tk._AMEBA_._PESEZ_.service.FunctionService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.regex.*;

@WebServlet("/functions/*")
public class FunctionController extends HttpServlet {
    private FunctionService functionService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        // Initialize service, ObjectMapper, and Validator (using CDI or manual initialization)
        this.functionService = new FunctionService("main.properties");
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json");
        try {
            // GET /functions/{id}
            if (path.matches("/\\d+")) {
                int id = extractId(path);
                FunctionDTO function = functionService.getFunction(id).join();
                resp.getWriter().write(objectMapper.writeValueAsString(function));
            // GET /functions/{id}/calculate?x=...
            } else if (path.matches("/\\d+/calculate")) {
                int id = extractId(path);
                double x = Double.parseDouble(req.getParameter("x"));
                double result = functionService.calculateFunction(id, x).join();
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
            switch (path) {
                // POST /functions/math
                case "/math" -> {
                    MathFunctionCreateRequest request = parseBody(req, MathFunctionCreateRequest.class);
                    int response = functionService.createMathFunction(request.expression).join();
                    resp.getWriter().write(response);
                }
                // POST /functions/tabulated
                case "/tabulated" -> {
                    TabulatedFunctionCreateRequest request = parseBody(req, TabulatedFunctionCreateRequest.class);
                    int response = functionService.createTabulated(request.expression, request.xFrom, request.xTo, request.pointCount).join();
                    resp.getWriter().write(response);
                }
                // POST /functions/pure-tabulated
                case "/pure-tabulated" -> {
                    PureTabulatedCreateRequest request = parseBody(req, PureTabulatedCreateRequest.class);
                    int response = functionService.createPureTabulated(request.xValues, request.yValues).join();
                    resp.getWriter().write(response);
                }
                // POST /functions/composite
                case "/composite" -> {
                    CompositeFunctionCreateRequest request = parseBody(req, CompositeFunctionCreateRequest.class);
                    int response = functionService.createComposite(request.innerId, request.outerId).join();
                    resp.getWriter().write(response);
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
        // PUT /functions/{id}/points?x=...&y=...
        if (path.matches("/\\d+/points")) {
            int id = extractId(path);
            double x = Double.parseDouble(req.getParameter("x"));
            double y = Double.parseDouble(req.getParameter("y"));
            functionService.updatePoint(id, x, y);
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (path.matches("/\\d+/points")) {
            // DELETE /functions/{id}/points?x=...
            int id = extractId(path);
            double x = Double.parseDouble(req.getParameter("x"));
            functionService.deletePoint(id, x);
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private <T> T parseBody(HttpServletRequest req, Class<T> classT) throws IOException {
        return objectMapper.readValue(req.getInputStream(), classT);
    }

    private final Pattern idPattern = Pattern.compile("/(\\d+)");

    private int extractId(String path) {
        Matcher matcher = idPattern.matcher(path);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        throw new IllegalArgumentException("Invalid ID in path");
    }
}