package ru.ssau.tk._AMEBA_._PESEZ_.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Controller extends HttpServlet {
    protected ObjectMapper objectMapper;

    @Override
    public void init() {
        this.objectMapper = new ObjectMapper();
    }

    protected  <T> T parseBody(HttpServletRequest req, Class<T> classT) throws IOException {
        return objectMapper.readValue(req.getInputStream(), classT);
    }
}
