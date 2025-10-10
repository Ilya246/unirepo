package ru.ssau.tk._AMEBA_._PESEZ_.concurrent;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.ConstantFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.LinkedListTabulatedFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.MathFunction;

public class ReadWriteTaskExecutor {
    public static void main(String[] args){
        var constantFunction = new ConstantFunction(-1);
        var function = new LinkedListTabulatedFunction(constantFunction, 1, 1000, 1000);
        Thread threadRead = new Thread(new ReadTask(function));
        Thread threadWrite = new Thread(new WriteTask(function,0.5));
        threadRead.start();
        threadWrite.start();
    }
}
