package ru.ssau.tk._AMEBA_._PESEZ_.io;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.LinkedListTabulatedFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.TabulatedFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.factory.LinkedListTabulatedFunctionFactory;
import ru.ssau.tk._AMEBA_._PESEZ_.operations.TabulatedDifferentialOperator;

import java.io.*;
import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.*;

public class TabulatedFunctionFileInputStream {
    public static void main(String[] args) {
        try(var _binaryStream= new FileInputStream("input/binary function.bin");
        ){
            var bufferedInputStream = new BufferedInputStream(_binaryStream);
            var arrayFactory = new ArrayTabulatedFunctionFactory();
            TabulatedFunction fileFunction = FunctionsIO.readTabulatedFunction(bufferedInputStream, arrayFactory);
            Log.info("Функция из файла: {}", fileFunction);

        }
        catch (IOException error) {
            Log.error("Ошибка:", error);
        }
        Log.info("Введите размер и значения функции");
        try {
            var linkedListFactory = new LinkedListTabulatedFunctionFactory();
            TabulatedFunction consoleFunction = FunctionsIO.readTabulatedFunction(new BufferedInputStream(System.in), linkedListFactory);

            var differentialOperator = new TabulatedDifferentialOperator();
            TabulatedFunction derivative = differentialOperator.derive(consoleFunction);

            Log.info("Производная введенной функции: {}", derivative);
        } catch (IOException e) {
            Log.error("Ошибка:", e);
        }
    }
}
