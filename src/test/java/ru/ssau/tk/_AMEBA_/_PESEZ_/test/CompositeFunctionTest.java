package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;

import static org.junit.jupiter.api.Assertions.*;

class CompositeFunctionTest {

    @Test
    void testCompositeFunctionSimple() {
        MathFunction sqr = new SqrFunction();
        MathFunction id = new IdentityFunction();

        // Создаём сложную функцию
        MathFunction composite = new CompositeFunction(sqr, id);

        assertEquals(9.0, composite.apply(3.0)); // 3^2 = 9, потом id(9) = 9
    }

    @Test
    void testCompositeFunctionOfComposite() {
        MathFunction sqr = new SqrFunction();


        MathFunction composite = new CompositeFunction(sqr, sqr);

        assertEquals(16.0, composite.apply(2.0)); // (2^2)^2 = 16
    }

    @Test
    void testCompositeFunctionDifferentFunctions() {
        MathFunction sqr = new SqrFunction();
        MathFunction id = new IdentityFunction();

        // h(x) = id(sqr(x)) = x^2
        MathFunction composite1 = new CompositeFunction(sqr, id);

        // h(x) = sqr(id(x)) = x^2
        MathFunction composite2 = new CompositeFunction(id, sqr);

        assertEquals(25.0, composite1.apply(5.0));
        assertEquals(25.0, composite2.apply(5.0));
    }

    @Test
    void testCompositeWithDeBoor() {
        // Данные для B-сплайна
        double[] knots = {0, 0, 0, 1, 2, 3, 4, 4, 4};
        double[] controlPoints = {0, 1, 2, 3, 4, 5};
        int degree = 2;
        int segmentIndex = 3;


        MathFunction deBoorFunc = new DeBoor(segmentIndex, knots, controlPoints, degree);

        MathFunction composite = new CompositeFunction(deBoorFunc, new SqrFunction());

        double deBoorValue = deBoorFunc.apply(2.5);
        double expected = deBoorValue * deBoorValue;

        assertEquals(expected, composite.apply(2.5), 1e-9);
    }
}
