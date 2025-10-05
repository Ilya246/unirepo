package ru.ssau.tk._AMEBA_._PESEZ_.io;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;

import java.io.*;

public class TabulatedFunctionFileWriter {
    public static void main(String[] args) {
        try(var _arrayWriter = new FileWriter("output/array function.txt");
            var _listWriter = new FileWriter("output/linked list function.txt")
        ) {
            var arrayTableWriter = new BufferedWriter(_arrayWriter);
            var listTableWriter = new BufferedWriter(_listWriter);

            var xValues = new double[] {1, 2, 3};
            var yValues = new double[] {10, 20, 30};
            var arrayFunc = new ArrayTabulatedFunction(xValues, yValues);
            var listFunc = new LinkedListTabulatedFunction(xValues, yValues);

            FunctionsIO.writeTabulatedFunction(arrayTableWriter, arrayFunc);
            FunctionsIO.writeTabulatedFunction(listTableWriter, listFunc);
        } catch (IOException error) {
            error.printStackTrace(System.err);
        }
    }
}
