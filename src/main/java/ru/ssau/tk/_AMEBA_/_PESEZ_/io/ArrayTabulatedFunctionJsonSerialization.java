package ru.ssau.tk._AMEBA_._PESEZ_.io;

import ru.ssau.tk._AMEBA_._PESEZ_.functions.ArrayTabulatedFunction;
import static ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility.*;

import java.io.*;

public class ArrayTabulatedFunctionJsonSerialization {
    public static void main(String[] args) {

        double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0, 25.0};
        ArrayTabulatedFunction originalFunction = new ArrayTabulatedFunction(xValues, yValues);

        String filename = "output/array_function.json";

        // Сериализация в JSON
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            FunctionsIO.serializeJson(writer, originalFunction);
            Log.info("Функция успешно сериализована в JSON файл: {}", filename);
            Log.info("Исходная функция: {}", originalFunction);
        } catch (IOException e) {
            Log.error("Ошибка при десериализации:", e);
        }

        // Десериализация из JSON
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            ArrayTabulatedFunction deserializedFunction = FunctionsIO.deserializeJson(reader);
            Log.info("Функция успешно десериализована из JSON файла: {}", filename);
            Log.info("Десериализованная функция: {}", deserializedFunction);

            // Проверка корректности десериализации
            Log.debug("Проверка корректности десериализации:\n" +
                      "Количество точек совпадает: {}",
                      (originalFunction.getCount() == deserializedFunction.getCount()));

            boolean allPointsMatch = true;
            for (int i = 0; i < originalFunction.getCount(); i++) {
                boolean xMatch = Math.abs(originalFunction.getX(i) - deserializedFunction.getX(i)) < 0.0001;
                boolean yMatch = Math.abs(originalFunction.getY(i) - deserializedFunction.getY(i)) < 0.0001;
                if (!xMatch || !yMatch) {
                    allPointsMatch = false;
                    break;
                }
            }
            Log.debug("Все точки совпадают: {}", allPointsMatch);

        } catch (IOException e) {
            Log.error("Ошибка при десериализации:", e);
        }
    }
}
