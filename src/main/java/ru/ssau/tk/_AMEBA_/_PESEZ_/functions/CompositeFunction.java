package ru.ssau.tk._AMEBA_._PESEZ_.functions;

public class CompositeFunction implements MathFunction{

    private MathFunction firstFunction;
    private MathFunction secondFunction;
    public CompositeFunction(MathFunction firstFunction, MathFunction secondFunction) {
        this.firstFunction=firstFunction;
        this.secondFunction=secondFunction;
    }

    @Override
    public double apply(double x) {
        double resultFirst = firstFunction.apply(x);
        return secondFunction.apply(resultFirst);
    }
}
