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

    @Test
    void testAndThen() {
        MathFunction inner = new SqrFunction();
        MathFunction f1 = inner.andThen(new SqrFunction());

        assertEquals(16, f1.apply(2));
        assertEquals(81, f1.apply(-3));

        MathFunction f2 = f1.andThen(new RungeKuttaFunction(2));

        assertTrue(Math.abs(f2.apply(2) - 512) < 0.1);
        assertTrue(Math.abs(f2.apply(-3) - 13122) < 0.1);

        assertEquals(25, new ConstantFunction(5).andThen(new SqrFunction()).andThen(new IdentityFunction()).apply(0));
    }
}
