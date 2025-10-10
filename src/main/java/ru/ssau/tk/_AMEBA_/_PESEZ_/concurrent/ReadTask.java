package ru.ssau.tk._AMEBA_._PESEZ_.concurrent;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.TabulatedFunction;

public class ReadTask implements Runnable{
    TabulatedFunction function;

    public ReadTask(TabulatedFunction tabulatedFunction) {
        this.function = tabulatedFunction;
    }

    @Override
    public void run() {
        for (int i = 0; i< function.getCount(); i++){
            System.out.printf("After read: i = %d, x = %f, y = %f\n", i, function.getX(i), function.getY(i));
        }
    }
}
