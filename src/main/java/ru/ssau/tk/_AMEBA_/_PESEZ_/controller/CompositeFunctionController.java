package ru.ssau.tk._AMEBA_._PESEZ_.controller;

import ru.ssau.tk._AMEBA_._PESEZ_.dto.CompositeFunctionDTO;
import ru.ssau.tk._AMEBA_._PESEZ_.service.FunctionService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

@WebServlet("/composite-functions")
public class CompositeFunctionController extends Controller {
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
            // GET /composite-functions?id={id}
            if (path.isEmpty()) {
                int id = Integer.parseInt(req.getParameter("id"));
                CompositeFunctionDTO function = functionService.getCompositeData(id).join();
                resp.getWriter().write(objectMapper.writeValueAsString(function));
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
