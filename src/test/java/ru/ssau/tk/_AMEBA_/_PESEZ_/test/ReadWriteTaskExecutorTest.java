package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.Test;
import ru.ssau.tk._AMEBA_._PESEZ_.concurrent.ReadTask;
import ru.ssau.tk._AMEBA_._PESEZ_.concurrent.ReadWriteTaskExecutor;
import ru.ssau.tk._AMEBA_._PESEZ_.concurrent.WriteTask;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.ConstantFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.LinkedListTabulatedFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.TabulatedFunction;
import static org.junit.jupiter.api.Assertions.*;

class ReadWriteTaskExecutorTest {

    @Test
    void testMainMethodRunsWithoutExceptions() {
        // Проверяем что main метод выполняется без исключений
        assertDoesNotThrow(() -> ReadWriteTaskExecutor.main(new String[]{}));
    }

    @Test
    void testThreadStart() throws InterruptedException {

        var constantFunction = new ConstantFunction(-1);
        var function = new LinkedListTabulatedFunction(constantFunction, 1, 10, 10);

        var readThread = new Thread(new ReadTask(function));
        var writeThread = new Thread(new WriteTask(function, 0.5));

        readThread.start();
        writeThread.start();


        readThread.join(1000);
        writeThread.join(1000);


        assertEquals(Thread.State.TERMINATED, readThread.getState());
        assertEquals(Thread.State.TERMINATED, writeThread.getState());


        for (int i = 0; i < function.getCount(); i++) {
            assertEquals(0.5, function.getY(i), 1e-10);
        }
    }

    @Test
    void testMainMethodCompletes() {

        Thread mainThread = new Thread(() -> ReadWriteTaskExecutor.main(new String[]{}));
        mainThread.start();


        assertDoesNotThrow(() -> {
            mainThread.join(3000); // Ждем 3 секунды
        });


        assertEquals(Thread.State.TERMINATED, mainThread.getState());
    }

    @Test
    void testFunctionCreation() {
        var constantFunction = new ConstantFunction(-1.0);
        TabulatedFunction tabulatedFunction = new LinkedListTabulatedFunction(constantFunction, 1, 10, 10);

        assertEquals(10, tabulatedFunction.getCount());
        assertEquals(1.0, tabulatedFunction.getX(0), 1e-10);
        assertEquals(10.0, tabulatedFunction.getX(9), 1e-10);

        for (int i = 0; i < tabulatedFunction.getCount(); i++) {
            assertEquals(-1.0, tabulatedFunction.getY(i), 1e-10);
        }
    }

    @Test
    void testConcurrentExecution() throws InterruptedException {
        var function = new ArrayTabulatedFunction(new double[]{1.0, 2.0}, new double[]{-1.0, -1.0});

        var readThread = new Thread(new ReadTask(function));
        var writeThread = new Thread(new WriteTask(function, 5.0));

        readThread.start();
        writeThread.start();

        readThread.join(1000);
        writeThread.join(1000);

        assertEquals(5.0, function.getY(0), 1e-10);
        assertEquals(5.0, function.getY(1), 1e-10);
    }

    @Test
    void testReadTaskConstructor() {
        var function = new ArrayTabulatedFunction(new double[]{1.0, 2.0}, new double[]{10.0, 20.0});
        var readTask = new ReadTask(function);

        assertNotNull(readTask);
    }

    @Test
    void testWriteTaskConstructor() {
        var function = new ArrayTabulatedFunction(new double[]{1.0, 2.0}, new double[]{10.0, 20.0});
        var writeTask = new WriteTask(function, 5.0);

        assertNotNull(writeTask);
    }

    @Test
    void testReadTaskRun() {
        var function = new ArrayTabulatedFunction(new double[]{1.0, 2.0}, new double[]{10.0, 20.0});
        var readTask = new ReadTask(function);

        assertDoesNotThrow(readTask::run);
    }

    @Test
    void testWriteTaskRun() {
        var function = new ArrayTabulatedFunction(new double[]{1.0, 2.0}, new double[]{10.0, 20.0});
        var writeTask = new WriteTask(function, 5.0);

        assertDoesNotThrow(writeTask::run);

        assertEquals(5.0, function.getY(0), 1e-10);
        assertEquals(5.0, function.getY(1), 1e-10);
    }

    @Test
    void testWriteTaskChangesValues() {
        var function = new ArrayTabulatedFunction(new double[]{1.0, 2.0}, new double[]{10.0, 20.0});
        var writeTask = new WriteTask(function, 7.5);

        writeTask.run();

        assertEquals(7.5, function.getY(0), 1e-10);
        assertEquals(7.5, function.getY(1), 1e-10);
    }


}