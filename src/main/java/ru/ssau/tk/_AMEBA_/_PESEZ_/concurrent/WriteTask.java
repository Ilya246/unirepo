package ru.ssau.tk._AMEBA_._PESEZ_.concurrent;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.TabulatedFunction;

public class WriteTask implements Runnable{
    private TabulatedFunction function;
    private double value;

    public WriteTask(TabulatedFunction function, double value) {
        this.function = function;
        this.value=value;
    }

    @Override
    public void run() {
        synchronized (function) {
        for (int i=0; i<function.getCount(); i++){
            function.setY(i,value);
            System.out.printf("Writing for index %d complete\n", i);
        }}
    }
}
