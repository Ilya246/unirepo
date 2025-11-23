package ru.ssau.tk._AMEBA_._PESEZ_.controller;

import ru.ssau.tk._AMEBA_._PESEZ_.dto.PointsDTO;
import ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository;
import ru.ssau.tk._AMEBA_._PESEZ_.service.FunctionService;

import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

@WebServlet("/points/*")
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
            if (!checkRequiredRole(req, resp, UserRepository.UserType.Admin)) {
                return;
            }
            // GET /points?id={id}
            if (path == null || path.isEmpty()) {
                int id = Integer.parseInt(req.getParameter("id"));
                PointsDTO points = functionService.getPoints(id).join();
                resp.getWriter().write(objectMapper.writeValueAsString(points));
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
            if (!checkRequiredRole(req, resp, UserRepository.UserType.Admin)) {
                return;
            }
            // POST /points?id={id}&x={x}&y={y}
            if (path == null || path.isEmpty()) {
                int id = Integer.parseInt(req.getParameter("id"));
                double x = Double.parseDouble(req.getParameter("x"));
                double y = Double.parseDouble(req.getParameter("y"));
                functionService.createPoint(id, x, y).join();
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
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        try {
            if (!checkRequiredRole(req, resp, UserRepository.UserType.Admin)) {
                return;
            }
            // PUT /points?id={id}&x={x}&y={y}
            if (path == null || path.isEmpty()) {
                int id = Integer.parseInt(req.getParameter("id"));
                double x = Double.parseDouble(req.getParameter("x"));
                double y = Double.parseDouble(req.getParameter("y"));
                functionService.updatePoint(id, x, y).join();
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
            if (!checkRequiredRole(req, resp, UserRepository.UserType.Admin)) {
                return;
            }
            // DELETE /points?id={id}&x={x}
            if (path == null || path.isEmpty()) {
                int id = Integer.parseInt(req.getParameter("id"));
                double x = Double.parseDouble(req.getParameter("x"));
                functionService.deletePoint(id, x).join();
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
