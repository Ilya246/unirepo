package ru.ssau.tk._AMEBA_._PESEZ_.operations;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.factory.*;
import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.*;

import java.io.Serial;
import java.util.concurrent.*;

// Оператор, интегрирующий табулированную функцию по области задания
public class TabulatedIntegralOperator implements IntegralOperator<TabulatedFunction, Double> {
    int partSize;

    public TabulatedIntegralOperator(int partSize) {
        this.partSize = partSize;
    }

    private static class IntegrateTask extends RecursiveTask<Double> {
        @Serial
        private static final long serialVersionUID = -1623237770652048111L;
        private final int left, right, partSize;
        private final TabulatedFunction function;

        public IntegrateTask(int left, int right, int partSize, TabulatedFunction function) {
            this.left = left;
            this.right = right;
            this.partSize = partSize;
            this.function = function;
        }

        @Override
        protected Double compute() {
            if (right - left > partSize) {
                int mid = (left + right) / 2;
                Log.trace("Split integration task [{}, {}) -> [{}, {}), [{}, {})", left, right, left, mid, mid, right);
                IntegrateTask leftTask = new IntegrateTask(left, mid, partSize, function);
                IntegrateTask rightTask = new IntegrateTask(mid, right, partSize, function);
                rightTask.fork();
                double ourResult = leftTask.compute();
                return ourResult + rightTask.join();
            } else {
                return getIntegral();
            }
        }

        private double getIntegral() {
            double integral = 0;
            for (int i = left; i < right; i++) {
                double x0 = function.getX(i - 1), y0 = function.getY(i - 1), x1 = function.getX(i), y1 = function.getY(i);
                // y = y0 + (y1 - y0) * (x - x0) / (x1 - x0)
                // y = z'
                // z = z0 + y0 * (x - x0) + (y1 - y0) * (x - x0)^2 / 2 / (x1 - x0)
                // z(x1) = z0 + y0*(x1 - x0) + (y1 - y0)*(x1 - x0)/2
                // z(x1) = z0 + (y1 + y0)*(x1 - x0)/2
                integral += (y1 + y0)*(x1 - x0)/2;
            }
            return integral;
        }
    }

    @Override
    public Double integrate(TabulatedFunction function) {
        Log.debug("Счёт интеграла из {}", function.simpleInfo());
        ForkJoinPool pool = ForkJoinPool.commonPool();
        // Интеграл в первой точке - 0, поэтому считаем с индекса 1
        return pool.invoke(new IntegrateTask(1, function.getCount(), partSize, function));
    }
}
