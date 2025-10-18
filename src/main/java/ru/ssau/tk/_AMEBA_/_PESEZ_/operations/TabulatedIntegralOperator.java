package ru.ssau.tk._AMEBA_._PESEZ_.operations;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.factory.*;

import java.util.concurrent.*;

// Оператор, интегрирующий табулированную функцию по области задания
public class TabulatedIntegralOperator implements IntegralOperator<TabulatedFunction, Double> {
    int partSize;

    public TabulatedIntegralOperator(int partSize) {
        this.partSize = partSize;
    }

    private static class IntegrateTask extends RecursiveTask<Double> {
        private final int left, right, partSize;
        private final Point[] points;

        public IntegrateTask(int left, int right, int partSize, Point[] points) {
            this.left = left;
            this.right = right;
            this.partSize = partSize;
            this.points = points;
        }

        @Override
        protected Double compute() {
            if (right - left > partSize) {
                int mid = (left + right) / 2;
                IntegrateTask leftTask = new IntegrateTask(left, mid, partSize, points);
                IntegrateTask rightTask = new IntegrateTask(mid, right, partSize, points);
                rightTask.fork();
                double ourResult = leftTask.compute();
                return ourResult + rightTask.join();
            } else {
                System.out.println(Thread.currentThread().getName() + " executing " + left + " to " + right);
                return getIntegral();
            }
        }

        private double getIntegral() {
            double integral = 0;
            for (int i = left; i < right; i++) {
                Point pt = points[i], prevPt = points[i - 1];
                double x0 = prevPt.getX(), y0 = prevPt.getY(), x1 = pt.getX(), y1 = pt.getY();
                // y = y0 + (y1 - y0) * (x - x0) / (x1 - x0)
                // y = z'
                // z = z0 + y0 * (x - x0) + (y1 - y0) * (x - x0)^2 / 2 / (x1 - x0)
                // z(x1) = z0 + y0*(x1 - x0) + (y1 - y0)*(x1 - x0)/2
                // z(x1) = z0 + (y1 + y0)*(x1 - x0)/2
                integral += (y1 + y0)*(x1 - x0)/2;
            }
            System.out.println(Thread.currentThread().getName() + " done");
            return integral;
        }
    }

    @Override
    public Double integrate(TabulatedFunction function) {
        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        ForkJoinPool pool = ForkJoinPool.commonPool();
        // Интеграл в первой точке - 0, поэтому считаем с индекса 1
        ForkJoinTask<Double> res = pool.submit(new IntegrateTask(1, points.length, partSize, points));
        try {
            return res.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
