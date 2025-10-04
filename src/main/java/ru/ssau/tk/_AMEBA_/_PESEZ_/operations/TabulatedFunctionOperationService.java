package ru.ssau.tk._AMEBA_._PESEZ_.operations;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;

public class TabulatedFunctionOperationService {
    public static Point[] asPoints(TabulatedFunction tabulatedFunction) {
        Point[] pts = new Point[tabulatedFunction.getCount()];
        int i = 0;
        for (Point p : tabulatedFunction) {
            pts[i++] = p;
        }
        return pts;
    }
}
