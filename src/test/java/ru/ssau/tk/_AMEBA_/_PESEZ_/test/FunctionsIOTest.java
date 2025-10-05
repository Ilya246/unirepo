package ru.ssau.tk._AMEBA_._PESEZ_.test;

import org.junit.jupiter.api.*;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.*;
import ru.ssau.tk._AMEBA_._PESEZ_.functions.factory.*;
import ru.ssau.tk._AMEBA_._PESEZ_.io.FunctionsIO;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class FunctionsIOTest {
    private static final String TEMP_DIR = "temp";
    @AfterAll
    static void cleanUp() {
        clearTempDirectory();
    }

    @BeforeEach
    void setUp() {
        ensureTempDirExists();
    }

    private static void ensureTempDirExists() {
        File tempDir = new File(TEMP_DIR);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
    }

    private static void clearTempDirectory() {
        File tempDir = new File(TEMP_DIR);
        if (tempDir.exists() && tempDir.isDirectory()) {
            File[] files = tempDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        clearDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
    }

    private static void clearDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    clearDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
    }

    @Test
    void writeTabulatedFunctionWithBufferedOutputStream() throws IOException {
        // Подготовка тестовых данных
        double[] xValues = {1.5, 2.5, 3.5};
        double[] yValues = {4.5, 5.5, 6.5};
        TabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        var testFile = new File(TEMP_DIR, "test_write_buffered_stream.bin");

        // Вызов тестируемого метода
        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(testFile))) {
            FunctionsIO.writeTabulatedFunction(outputStream, function);
        }

        // Проверка результата
        assertTrue(testFile.exists());

        try (var dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(testFile)))) {
            assertEquals(3, dataInputStream.readInt());
            assertEquals(1.5, dataInputStream.readDouble(), 0.0001);
            assertEquals(4.5, dataInputStream.readDouble(), 0.0001);
            assertEquals(2.5, dataInputStream.readDouble(), 0.0001);
            assertEquals(5.5, dataInputStream.readDouble(), 0.0001);
            assertEquals(3.5, dataInputStream.readDouble(), 0.0001);
            assertEquals(6.5, dataInputStream.readDouble(), 0.0001);
        }
    }

    @Test
    void readTabulatedFunctionWithBufferedInputStream() throws IOException {
        // Подготовка тестовых данных
        var testFile = new File(TEMP_DIR, "test_read_buffered_stream.bin");

        try (var dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(testFile)))) {
            dataOutputStream.writeInt(2);
            dataOutputStream.writeDouble(1.5);
            dataOutputStream.writeDouble(4.5);
            dataOutputStream.writeDouble(2.5);
            dataOutputStream.writeDouble(5.5);
        }

        // Вызов тестируемого метода
        try (var inputStream = new BufferedInputStream(new FileInputStream(testFile))) {
            var factory = new ArrayTabulatedFunctionFactory();
            TabulatedFunction function = FunctionsIO.readTabulatedFunction(inputStream, factory);

            // Проверка результата
            assertNotNull(function);
            assertEquals(2, function.getCount());
            assertEquals(1.5, function.getX(0), 0.0001);
            assertEquals(4.5, function.getY(0), 0.0001);
            assertEquals(2.5, function.getX(1), 0.0001);
            assertEquals(5.5, function.getY(1), 0.0001);
        }
    }

    @Test
    void deserializeArrayTabulatedFunction() throws IOException, ClassNotFoundException {
        // Подготовка тестовых данных
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        TabulatedFunction originalFunction = new ArrayTabulatedFunction(xValues, yValues);

        var testFile = new File(TEMP_DIR, "test_deserialize_array.bin");

        try (var outputStream = new BufferedOutputStream(new FileOutputStream(testFile))) {
            FunctionsIO.serialize(outputStream, originalFunction);
        }

        try (var inputStream = new BufferedInputStream(new FileInputStream(testFile))) {
            TabulatedFunction deserializedFunction = FunctionsIO.deserialize(inputStream);

            assertEquals(originalFunction, deserializedFunction);
        }
    }

    @Test
    void deserializeLinkedListTabulatedFunction() throws IOException, ClassNotFoundException {
        // Подготовка тестовых данных
        double[] xValues = {0.5, 1.5, 2.5};
        double[] yValues = {1.5, 2.5, 3.5};
        TabulatedFunction originalFunction = new LinkedListTabulatedFunction(xValues, yValues);

        var testFile = new File(TEMP_DIR, "test_deserialize_linkedlist.bin");

        try (var outputStream = new BufferedOutputStream(new FileOutputStream(testFile))) {
            FunctionsIO.serialize(outputStream, originalFunction);
        }

        try (var inputStream = new BufferedInputStream(new FileInputStream(testFile))) {
            TabulatedFunction deserializedFunction = FunctionsIO.deserialize(inputStream);

            assertEquals(originalFunction, deserializedFunction);
        }
    }

    @Test
    void readWriteArrayTabulatedFunction() throws IOException, ClassNotFoundException {
        // Подготовка тестовых данных
        double[] xValues = {0.5, 1.5, 2.5};
        double[] yValues = {1.5, 2.5, 3.5};
        TabulatedFunction originalFunction = new ArrayTabulatedFunction(xValues, yValues);

        var testFile = new File(TEMP_DIR, "test_readwrite_array.txt");

        try (var outputStream = new BufferedWriter(new FileWriter(testFile))) {
            FunctionsIO.writeTabulatedFunction(outputStream, originalFunction);
        }

        try (var inputStream = new BufferedReader(new FileReader(testFile))) {
            TabulatedFunction deserializedFunction = FunctionsIO.readTabulatedFunction(inputStream, new ArrayTabulatedFunctionFactory());

            assertEquals(originalFunction, deserializedFunction);
        }
    }

    @Test
    void readWriteLinkedListTabulatedFunction() throws IOException, ClassNotFoundException {
        // Подготовка тестовых данных
        double[] xValues = {0.5, 1.5, 2.5};
        double[] yValues = {1.5, 2.5, 3.5};
        TabulatedFunction originalFunction = new LinkedListTabulatedFunction(xValues, yValues);

        var testFile = new File(TEMP_DIR, "test_readwrite_linkedlist.txt");

        try (var outputStream = new BufferedWriter(new FileWriter(testFile))) {
            FunctionsIO.writeTabulatedFunction(outputStream, originalFunction);
        }

        try (var inputStream = new BufferedReader(new FileReader(testFile))) {
            TabulatedFunction deserializedFunction = FunctionsIO.readTabulatedFunction(inputStream, new ArrayTabulatedFunctionFactory());

            assertEquals(originalFunction, deserializedFunction);
        }
    }

    @Test
    void deserializeWithInvalidFile() {
        var nonExistentFile = new File(TEMP_DIR, "non_existent.bin");

        assertThrows(IOException.class, () -> {
            try (var inputStream = new BufferedInputStream(new FileInputStream(nonExistentFile))) {
                FunctionsIO.deserialize(inputStream);
            }
        });
    }

    @Test
    void deserializeWithInvalidData() throws IOException {
        var invalidFile = new File(TEMP_DIR, "invalid_data.bin");


        try (var dos = new DataOutputStream(new FileOutputStream(invalidFile))) {
            dos.writeUTF("This is not a serialized object");
        }

        assertThrows(IOException.class, () -> {
            try (var inputStream = new BufferedInputStream(new FileInputStream(invalidFile))) {
                FunctionsIO.deserialize(inputStream);
            }
        });
    }
}