package ru.ssau.tk._AMEBA_._PESEZ_.concurrent;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;
import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.*;

import java.util.*;

public class MultiplyingTaskExecutor {
    public static void main(String[] args) throws InterruptedException {
        var func = new LinkedListTabulatedFunction(new UnitFunction(), 1, 1000, 1000);

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            var task = new MultiplyingTask(func);
            var thread = new Thread(task);
            threads.add(thread);
        }
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }

        Log.info("Function is now: {}", func);
    }
}
