package ru.ssau.tk._AMEBA_._PESEZ_.io;

import java.io.*;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;

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
}
