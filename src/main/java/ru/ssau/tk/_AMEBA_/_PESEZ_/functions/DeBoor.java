package ru.ssau.tk._AMEBA_._PESEZ_.functions;

public class DeBoor implements MathFunction {


    static int segmentIndex;
    static double[] knots;
    static double[] controlPoints;
    static int degree;
    public DeBoor(int segmentIndex, double[] knots, double[] controlPoints, int degree) {
        DeBoor.segmentIndex=segmentIndex;
        DeBoor.knots=knots;
        DeBoor.controlPoints=controlPoints;
        DeBoor.degree=degree;
    }

    public static double deBoor(double x) {
        // Массив для хранения текущих вычислений, размер degree+1
        double[] tempPoints = new double[degree + 1];

        // Инициализация массива начальными контрольными точками для данного сегмента
        for (int i = 0; i <= degree; i++) {
            tempPoints[i] = controlPoints[i + segmentIndex - degree];
        }

        // Основной алгоритм де Бура
        for (int r = 1; r <= degree; r++) {
            for (int j = degree; j >= r; j--) {
                // Вычисление коэффициента интерполяции
                double alpha = (x - knots[j + segmentIndex - degree])
                        / (knots[j + 1 + segmentIndex - r] - knots[j + segmentIndex - degree]);

                // Линейная интерполяция между двумя точками
                tempPoints[j] = (1.0 - alpha) * tempPoints[j - 1] + alpha * tempPoints[j];
            }
        }

        // Возвращаем значение сплайна в точке x
        return tempPoints[degree];
    }

    @Override
    public double apply(double x) {
        return deBoor(x);
    }
}
