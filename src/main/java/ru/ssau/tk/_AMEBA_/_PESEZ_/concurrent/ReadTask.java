package ru.ssau.tk._AMEBA_._PESEZ_.concurrent;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.TabulatedFunction;
import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.*;

public class ReadTask implements Runnable{
    private TabulatedFunction function;

    public ReadTask(TabulatedFunction tabulatedFunction) {
        this.function = tabulatedFunction;
    }

    @Override
    public void run() {
        for (int i = 0; i < function.getCount(); i++) {
            synchronized (function) {
                Log.info("After read: i = {}, x = {}, y = {}", i, function.getX(i), function.getY(i));
            }
        }
        Log.debug("Thread {} finished ReadTask", Thread.currentThread().getName());
    }
}
