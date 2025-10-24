package ru.ssau.tk._AMEBA_._PESEZ_.io;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.factory.*;
import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.*;

import java.io.*;

public class TabulatedFunctionFileReader {
    public static void main(String[] args) {
        try(var _arrayReader = new FileReader("input/function.txt");
            var _listReader = new FileReader("input/function.txt")
        ) {
            var arrayTableReader = new BufferedReader(_arrayReader);
            var listTableReader = new BufferedReader(_listReader);

            TabulatedFunction arrayFunc = FunctionsIO.readTabulatedFunction(arrayTableReader, new ArrayTabulatedFunctionFactory());
            TabulatedFunction listFunc = FunctionsIO.readTabulatedFunction(listTableReader, new LinkedListTabulatedFunctionFactory());

            Log.info("{}", arrayFunc);
            Log.info("{}", listFunc);
        } catch (IOException error) {
            error.printStackTrace(System.err);
        }
    }
}
