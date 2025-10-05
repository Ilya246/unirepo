package ru.ssau.tk._AMEBA_._PESEZ_.io;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk._AMEBA_._PESEZ_.operations.TabulatedDifferentialOperator;

import java.io.*;

public class ArrayTabulatedFunctionSerialization {
    public static void main(String[] args) {
        try(var _output = new FileOutputStream("output/serialized array functions.bin")) {
            var output = new BufferedOutputStream(_output);

            var factory = new ArrayTabulatedFunctionFactory();
            var derivativeOperator = new TabulatedDifferentialOperator(factory);
            var func = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{10, 40, 90});
            TabulatedFunction derivative1 = derivativeOperator.derive(func);
            TabulatedFunction derivative2 = derivativeOperator.derive(derivative1);

            FunctionsIO.serialize(output, func);
            FunctionsIO.serialize(output, derivative1);
            FunctionsIO.serialize(output, derivative2);
        } catch (IOException error) {
            error.printStackTrace(System.err);
        }

        try(var _input = new FileInputStream("output/serialized array functions.bin")) {
            var input = new BufferedInputStream(_input);

            TabulatedFunction func = FunctionsIO.deserialize(input);
            TabulatedFunction derivative1 = FunctionsIO.deserialize(input);
            TabulatedFunction derivative2 = FunctionsIO.deserialize(input);

            System.out.println("func: " + func.toString());
            System.out.println("derivative1: " + derivative1.toString());
            System.out.println("derivative2: " + derivative2.toString());
        } catch (IOException | ClassNotFoundException error) {
            error.printStackTrace(System.err);
        }
    }
}
