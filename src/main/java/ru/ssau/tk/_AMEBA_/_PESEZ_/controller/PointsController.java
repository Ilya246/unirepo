package ru.ssau.tk._AMEBA_._PESEZ_.controller;

import ru.ssau.tk._AMEBA_._PESEZ_.dto.PointsDTO;
import ru.ssau.tk._AMEBA_._PESEZ_.service.FunctionService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

@WebServlet("/points")
public class PointsController extends Controller {
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
            // GET /points?id={id}
            if (path.isEmpty()) {
                int id = Integer.parseInt(req.getParameter("id"));
                PointsDTO points = functionService.getPoints(id).join();
                resp.getWriter().write(objectMapper.writeValueAsString(points));
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        // PUT /points?id={id}&x={x}&y={y}
        if (path.isEmpty()) {
            int id = Integer.parseInt(req.getParameter("id"));
            double x = Double.parseDouble(req.getParameter("x"));
            double y = Double.parseDouble(req.getParameter("y"));
            functionService.updatePoint(id, x, y).join();
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        // DELETE /points?id={id}&x={x}
        if (path.isEmpty()) {
            int id = Integer.parseInt(req.getParameter("id"));
            double x = Double.parseDouble(req.getParameter("x"));
            functionService.deletePoint(id, x).join();
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
