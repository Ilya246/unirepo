package ru.ssau.tk._AMEBA_._PESEZ_.io;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;
import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.*;

import java.io.*;

public class ArrayTabulatedFunctionXmlSerialization {
    public static void main(String[] args) {
        try(var _output = new FileWriter("output/func.xml")) {
            var output = new BufferedWriter(_output);

            var func = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{10, 40, 90});
            FunctionsIO.serializeXml(output, func);
        } catch (IOException error) {
            Log.error("Ошибка:", error);
        }

        try(var _input = new FileReader("output/func.xml")) {
            var input = new BufferedReader(_input);

            TabulatedFunction func = FunctionsIO.deserializeXml(input);
            Log.info("func: {}", func);
        } catch (IOException error) {
            Log.error("Ошибка:", error);
        }
    }
}
