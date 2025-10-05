package ru.ssau.tk._AMEBA_._PESEZ_.io;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.Point;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.TabulatedFunction;

import java.io.*;

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
    public static void writeTabulatedFunction(BufferedOutputStream outputStream, TabulatedFunction function) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        dataOutputStream.writeInt(function.getCount());

        for (Point point : function) {
            dataOutputStream.writeDouble(point.getX());
            dataOutputStream.writeDouble(point.getY());
        }

        dataOutputStream.flush();
    }

}
