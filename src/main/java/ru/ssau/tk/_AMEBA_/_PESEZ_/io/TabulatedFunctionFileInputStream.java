package ru.ssau.tk._AMEBA_._PESEZ_.io;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.LinkedListTabulatedFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.TabulatedFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.factory.LinkedListTabulatedFunctionFactory;
import ru.ssau.tk._AMEBA_._PESEZ_.operations.TabulatedDifferentialOperator;

import java.io.*;

public class TabulatedFunctionFileInputStream {
    public static void main(String[] args) {
        try(var _binaryStream= new FileInputStream("input/binary function.bin");
        ){
            var bufferedInputStream = new BufferedInputStream(_binaryStream);
            var arrayFactory = new ArrayTabulatedFunctionFactory();
            TabulatedFunction fileFunction = FunctionsIO.readTabulatedFunction(bufferedInputStream, arrayFactory);
            System.out.println("Функция из файла:");
            System.out.println(fileFunction.toString());

        }
        catch (IOException error) {
            error.printStackTrace(System.err);
        }
        System.out.println("Введите размер и значения функции");
        try {
            var inputStreamReader = new InputStreamReader(System.in);
            var bufferedReader = new BufferedReader(inputStreamReader);

            var linkedListFactory = new LinkedListTabulatedFunctionFactory();
            TabulatedFunction consoleFunction = FunctionsIO.readTabulatedFunction(new BufferedInputStream(System.in), linkedListFactory);

            var differentialOperator = new TabulatedDifferentialOperator();
            TabulatedFunction derivative = differentialOperator.derive(consoleFunction);

            System.out.println("Производная введенной функции:");
            System.out.println(derivative.toString());

        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }
}
