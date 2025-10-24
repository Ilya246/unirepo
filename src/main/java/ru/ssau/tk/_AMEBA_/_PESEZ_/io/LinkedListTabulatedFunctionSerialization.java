package ru.ssau.tk._AMEBA_._PESEZ_.io;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.factory.LinkedListTabulatedFunctionFactory;
import ru.ssau.tk._AMEBA_._PESEZ_.operations.TabulatedDifferentialOperator;
import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.*;

import java.io.*;

public class LinkedListTabulatedFunctionSerialization {
    public static void main(String[] args) {

        try (var _output = new FileOutputStream("output/serialized linked list functions.bin")) {
            var output = new BufferedOutputStream(_output);

            var factory = new LinkedListTabulatedFunctionFactory();
            var derivativeOperator = new TabulatedDifferentialOperator(factory);
            var func = new LinkedListTabulatedFunction(new double[]{1, 2, 3}, new double[]{10, 40, 90});
            TabulatedFunction derivative1 = derivativeOperator.derive(func);
            TabulatedFunction derivative2 = derivativeOperator.derive(derivative1);

            FunctionsIO.serialize(output, func);
            FunctionsIO.serialize(output, derivative1);
            FunctionsIO.serialize(output, derivative2);
        } catch (IOException error) {
            Log.error("Ошибка:", error);
        }

        try (var _input = new FileInputStream("output/serialized linked list functions.bin")) {
            var input = new BufferedInputStream(_input);

            TabulatedFunction func = FunctionsIO.deserialize(input);
            TabulatedFunction derivative1 = FunctionsIO.deserialize(input);
            TabulatedFunction derivative2 = FunctionsIO.deserialize(input);

            Log.info("func: {}", func);
            Log.info("derivative1: {}", derivative1);
            Log.info("derivative2: {}", derivative2);
        } catch (IOException | ClassNotFoundException error) {
            Log.error("Ошибка:", error);
        }
    }
}