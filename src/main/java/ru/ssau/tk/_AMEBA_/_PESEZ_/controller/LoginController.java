package ru.ssau.tk._AMEBA_._PESEZ_.controller;

import static ru.ssau.tk._AMEBA_._PESEZ_.repository.UserRepository.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@WebServlet("/login")
public class LoginController extends Controller {
    HashSet<String> gotLoginFor = new HashSet<>();
    Timer cleanupTimer = new Timer();
    long cleanupTimeout = 120;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String date = req.getParameter("date");
        if (gotLoginFor.contains(date) && hasRequiredRole(req, UserType.Normal)) {
            gotLoginFor.remove(date);
            resp.sendRedirect("/");
            return;
        }
        gotLoginFor.add(date);
        cleanupTimer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        gotLoginFor.remove(date);
                    }
                },
                cleanupTimeout * 1000
        );
        resp.setHeader("WWW-Authenticate", "Basic realm=\"Restricted Area\"");
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
