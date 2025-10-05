package ru.ssau.tk._AMEBA_._PESEZ_.io;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;

import java.io.*;

public class ArrayTabulatedFunctionXmlSerialization {
    public static void main(String[] args) {
        try(var _output = new FileWriter("output/func.xml")) {
            var output = new BufferedWriter(_output);

            var func = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{10, 40, 90});
            FunctionsIO.serializeXml(output, func);
        } catch (IOException error) {
            error.printStackTrace(System.err);
        }

        try(var _input = new FileReader("output/func.xml")) {
            var input = new BufferedReader(_input);

            TabulatedFunction func = FunctionsIO.deserializeXml(input);
            System.out.println("func: " + func.toString());
        } catch (IOException error) {
            error.printStackTrace(System.err);
        }
    }
}
