package ru.ssau.tk._AMEBA_._PESEZ_.operations;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;

abstract public class SteppingDifferentialOperator implements DifferentialOperator<MathFunction> {
    protected double step;

    public SteppingDifferentialOperator(double step) {
        setStep(step);
    }

    public double getStep() {
        return step;
    }

    public void setStep(double step) {
        if (!Double.isFinite(step) || Double.isNaN(step) || step <= 0) throw new IllegalArgumentException();
        this.step = step;
    }
}
