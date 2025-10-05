package ru.ssau.tk._AMEBA_._PESEZ_.io;

import java.io.*;
import java.text.*;
import java.util.Locale;

import com.thoughtworks.xstream.XStream;
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

    public static void writeTabulatedFunction(BufferedOutputStream outputStream, TabulatedFunction function) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        dataOutputStream.writeInt(function.getCount());

        for (Point point : function) {
            dataOutputStream.writeDouble(point.getX());
            dataOutputStream.writeDouble(point.getY());
        }

        dataOutputStream.flush();
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

    public static TabulatedFunction readTabulatedFunction(BufferedInputStream inputStream, TabulatedFunctionFactory factory) throws IOException{
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        int count = dataInputStream.readInt();

        double[] xValues = new double[count];
        double[] yValues = new double[count];

        for (int i = 0; i < count; i++) {
            xValues[i] = dataInputStream.readDouble();
            yValues[i] = dataInputStream.readDouble();
        }

        return factory.create(xValues, yValues);
    }


    public static void serialize(BufferedOutputStream stream, TabulatedFunction function) throws IOException {
        var output = new ObjectOutputStream(stream);
        output.writeObject(function);
        output.flush();
    }

    public static TabulatedFunction deserialize(BufferedInputStream stream) throws IOException, ClassNotFoundException {
        var input = new ObjectInputStream(stream);
        return (TabulatedFunction) input.readObject();
    }

    public static void serializeXml(BufferedWriter writer, ArrayTabulatedFunction function) throws IOException {
        var stream = new XStream();
        String xml = stream.toXML(function);
        writer.write(xml);
        writer.flush();
    }

    public static ArrayTabulatedFunction deserializeXml(BufferedReader reader) {
        var stream = new XStream();
        stream.allowTypes(new Class[] {ArrayTabulatedFunction.class});
        Object function = stream.fromXML(reader);
        return (ArrayTabulatedFunction) function;
    }
}
