package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.DeBoor;

import static org.junit.jupiter.api.Assertions.*;

class DeBoorTest {
    @Test
    void apply() {
        double[] knots = {0, 0, 0, 1, 2, 3, 4, 4, 4}; // пример узлов
        double[] controlPoints = {0, 1, 2, 3, 4, 5}; // пример контрольных точек
        int degree = 2;                               // степень сплайна
        int segmentIndex = 3;
        double x=2.5;
        double expect=3;
        DeBoor boor = new DeBoor(segmentIndex, knots, controlPoints, degree);
        double res=boor.apply(x);
        assertEquals(res, expect);
    }
}