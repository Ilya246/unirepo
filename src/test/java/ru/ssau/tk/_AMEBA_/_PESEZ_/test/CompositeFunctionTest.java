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

    @Test
    void testTabulatedFunctions1() {
        ArrayTabulatedFunction arrayFunc = new ArrayTabulatedFunction(new double[] {0, 1, 2, 3}, new double[] {0, 2, 4, 6});
        LinkedListTabulatedFunction listFunc = new LinkedListTabulatedFunction(new double[] {0, 2, 4, 6}, new double[] {1, 3, 5, 7});
        MathFunction composite = arrayFunc.andThen(listFunc);

        assertEquals(1.0, composite.apply(0.0)); // arrayFunc(0) = 0, listFunc(0) = 1
        assertEquals(3.0, composite.apply(1.0)); // arrayFunc(1) = 2, listFunc(2) = 3
        assertEquals(5.0, composite.apply(2.0)); // arrayFunc(2) = 4, listFunc(4) = 5
        assertEquals(2.0, composite.apply(0.5)); // arrayFunc(0.5) = 1, listFunc(1) = 2
        assertEquals(4.0, composite.apply(1.5)); // arrayFunc(1.5) = 3, listFunc(3) = 4
    }

    @Test
    void testTabulatedFunctions2() {
        ArrayTabulatedFunction tabulatedFunc = new ArrayTabulatedFunction(new double[] {-2, -1, 0, 1, 2}, new double[] {-4, -2, 0, 2, 4});
        MathFunction composite = tabulatedFunc.andThen(new SqrFunction());

        assertEquals(16.0, composite.apply(-2.0)); // tabulatedFunc(-2) = -4, (-4)^2 = 16
        assertEquals(4.0, composite.apply(-1.0));  // tabulatedFunc(-1) = -2, (-2)^2 = 4
        assertEquals(0.0, composite.apply(0.0));   // tabulatedFunc(0) = 0, 0^2 = 0
        assertEquals(4.0, composite.apply(1.0));   // tabulatedFunc(1) = 2, 2^2 = 4
        assertEquals(16.0, composite.apply(2.0));  // tabulatedFunc(2) = 4, 4^2 = 16
        assertEquals(9.0, composite.apply(-1.5));  // tabulatedFunc(-1.5) = -3, (-3)^2 = 9
        assertEquals(1.0, composite.apply(-0.5));  // tabulatedFunc(-0.5) = -1, (-1)^2 = 1
    }

    @Test
    void testMultipleCompositionsWithTabulatedFunctions() {
        ArrayTabulatedFunction func1 = new ArrayTabulatedFunction(new double[] {0, 1, 2}, new double[] {0, 3, 6});
        LinkedListTabulatedFunction func2 = new LinkedListTabulatedFunction(new double[] {0, 2, 4, 6}, new double[] {0, 4, 8, 12});
        MathFunction composite = func1.andThen(func2).andThen(new SqrFunction());

        assertEquals(0.0, composite.apply(0.0));   // func1(0) = 0, func2(0) = 0, 0^2 = 0
        assertEquals(36.0, composite.apply(1.0));  // func1(1) = 3, func2(3) = 6, 6^2 = 36
        assertEquals(144.0, composite.apply(2.0)); // func1(2) = 6, func2(6) = 12, 12^2 = 144
        assertEquals(81.0, composite.apply(1.5));  // func1(1.5) = 4.5, func2(4.5) = 9, 9^2 = 81
    }
}
