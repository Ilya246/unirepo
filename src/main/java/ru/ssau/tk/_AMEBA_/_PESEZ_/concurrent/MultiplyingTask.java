package ru.ssau.tk._AMEBA_._PESEZ_.concurrent;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.TabulatedFunction;

public class MultiplyingTask implements Runnable {
    private TabulatedFunction function;

    public MultiplyingTask(TabulatedFunction function) {
        this.function = function;
    }

    @Override
    public void run() {
        int count = function.getCount();
        for (int i = 0; i < count; i++) {
            function.setY(i, function.getY(i) * 2);
        }
        System.out.println("Thread " + Thread.currentThread().getName() + " finished MultiplyingTask");
    }
}
