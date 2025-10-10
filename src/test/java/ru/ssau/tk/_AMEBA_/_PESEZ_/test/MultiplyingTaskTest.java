package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.concurrent.MultiplyingTask;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;
import ru.ssau.tk._AMEBA_._PESEZ_.operations.TabulatedFunctionOperationService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MultiplyingTaskTest {

    @Test
    void run() { // test no race condition
        var func = new ArrayTabulatedFunction(new UnitFunction(), 1, 10000, 10000);

        List<Thread> threads = new ArrayList<>();

        int amount = 20;
        for (int i = 0; i < amount; i++) {
            var task = new MultiplyingTask(func);
            var thread = new Thread(task);
            threads.add(thread);
        }
        for (Thread t : threads) {
            t.start();
        }
        try {
            for (Thread t : threads) {
                t.join();
            }
        } catch (InterruptedException e) {
            fail();
        }

        int expected = 1 << amount; // 2^amount
        Point[] pts = TabulatedFunctionOperationService.asPoints(func);
        for (Point p : pts) {
            assertEquals(p.getY(), expected);
        }
    }
}