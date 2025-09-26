package ru.ssau.tk._AMEBA_._PESEZ_.functions;

public class RungeKuttaFunction implements MathFunction {
    public double y_0;
    public double t_0 = 1;
    public double maxStepSize = 0.1;
    public int minSteps = 50;

    public RungeKuttaFunction(double y_0) {
        this.y_0 = y_0;
    }

    // y = y_0 * x^2
    double innerDerivative(double t, double y) {
        return Math.abs(t) < 1e-10 ? 0 : 2 * t * y / (t * t); // no /0
    }

    @Override
    public double apply(double x) {
        double dt = x - t_0;
        long step_n = Math.round(Math.max(minSteps, Math.abs(dt) / maxStepSize));
        double step = dt / step_n;

        double y = y_0;
        double t = t_0;
        for (long i = 0; i < step_n; i++) {
            double k1 = innerDerivative(t, y);
            double k2 = innerDerivative(t + 0.5 * step, y + 0.5 * step * k1);
            double k3 = innerDerivative(t + 0.5 * step, y + 0.5 * step * k2);
            double k4 = innerDerivative(t + step, y + step * k3);
            y += step / 6 * (k1 + 2 * k2 + 2 * k3 + k4);
            t += step;
        }

        return y;
    }
}
