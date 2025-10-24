package ru.ssau.tk._AMEBA_._PESEZ_.concurrent;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.TabulatedFunction;
import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.*;

public class WriteTask implements Runnable{
    private TabulatedFunction function;
    private double value;

    public WriteTask(TabulatedFunction function, double value) {
        this.function = function;
        this.value = value;
    }

    @Override
    public void run() {
        for (int i = 0; i < function.getCount(); i++) {
            synchronized (function) {
                function.setY(i, value);
                Log.debug("Writing for index {} complete", i);
            }
        }
        Log.debug("Thread {} finished WriteTask", Thread.currentThread().getName());
    }
}
