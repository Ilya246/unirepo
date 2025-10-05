package ru.ssau.tk._AMEBA_._PESEZ_.io;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.ArrayTabulatedFunction;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.LinkedListTabulatedFunction;

import java.io.*;

public class TabulatedFunctionFileOutputStream {
    public static void main(String[] args) {
        try(var _arrayStream= new FileOutputStream("output/array function.bin");
            var _listStream = new FileOutputStream("output/linked list function.bin")
        ){
            var arrayTableStream = new BufferedOutputStream(_arrayStream);
            var listTableStream = new BufferedOutputStream(_listStream);

            var xValues = new double[] {1, 2, 3};
            var yValues = new double[] {10, 20, 30};
            var arrayFunc = new ArrayTabulatedFunction(xValues, yValues);
            var listFunc = new LinkedListTabulatedFunction(xValues, yValues);

            FunctionsIO.writeTabulatedFunction(arrayTableStream, arrayFunc);
            FunctionsIO.writeTabulatedFunction(listTableStream, listFunc);

        }
        catch (IOException error) {
            error.printStackTrace(System.err);
        }
    }
}
