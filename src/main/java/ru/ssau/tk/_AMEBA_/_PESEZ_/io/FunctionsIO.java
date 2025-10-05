package ru.ssau.tk._AMEBA_._PESEZ_.io;

import java.io.*;
import java.text.*;
import java.util.Locale;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.factory.TabulatedFunctionFactory;

public final class FunctionsIO {
    private FunctionsIO() {
        throw new UnsupportedOperationException();
    }

    public static void writeTabulatedFunction(BufferedWriter writer, TabulatedFunction function) {
        var print = new PrintWriter(writer);
        print.println(function.getCount());
        for (Point point : function) {
            print.printf("%f %f\n", point.getX(), point.getY());
        }
        print.flush();
    }

    public static TabulatedFunction readTabulatedFunction(BufferedReader reader, TabulatedFunctionFactory factory) throws IOException {
        int count = Integer.parseInt(reader.readLine());
        var xValues = new double[count];
        var yValues = new double[count];

        try {
            var formatter = NumberFormat.getInstance(Locale.forLanguageTag("ru"));
            for (int i = 0; i < count; i++) {
                String line = reader.readLine();
                String[] numbers = line.split(" ");
                xValues[i] = formatter.parse(numbers[0]).doubleValue();
                yValues[i] = formatter.parse(numbers[1]).doubleValue();
            }
        } catch (ParseException error) {
            throw new IOException(error);
        }

        return factory.create(xValues, yValues);
    }

}
