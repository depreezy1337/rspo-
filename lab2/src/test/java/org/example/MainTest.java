package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    private Path inputFile;
    private Path outputDir;

    @BeforeEach
    void setUp() throws IOException {
        outputDir = Files.createTempDirectory("test_output");
        inputFile = Files.createTempFile("test_input", ".txt");
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(inputFile);
        Files.walk(outputDir)
                .map(Path::toFile)
                .forEach(File::delete);
    }

    private void writeToFile(Path file, List<String> lines) throws IOException {
        Files.write(file, lines);
    }

    private String readFileContent(Path file) throws IOException {
        return Files.readString(file).trim();
    }

    @Test
    void testStatisticsComputation() throws IOException {
        writeToFile(inputFile, List.of("150", "131", "666", "6", "3.14", "2.66", "mers", "AMG"));

        String[] args = {inputFile.toString(), "-o", outputDir.toString(), "-s"};
        Main.main(args);

        Path intFile = Paths.get(outputDir.toString(), "integers.txt");
        Path floatFile = Paths.get(outputDir.toString(), "floats.txt");
        Path stringFile = Paths.get(outputDir.toString(), "strings.txt");

        assertTrue(Files.exists(intFile));
        assertTrue(Files.exists(floatFile));
        assertTrue(Files.exists(stringFile));

        List<String> expectedLinesInt = List.of("150", "131", "666", "6");
        List<String> actualLinesInt = Files.readAllLines(intFile);
        assertEquals(expectedLinesInt, actualLinesInt);

        List<String> expectedLinesFloat = List.of("3.14", "2.66");
        List<String> actualLinesFloat = Files.readAllLines(floatFile);
        assertEquals(expectedLinesFloat, actualLinesFloat);

        List<String> expectedLinesStr = List.of( "mers", "AMG");
        List<String> actualLinesStr = Files.readAllLines(stringFile);
        assertEquals(expectedLinesStr, actualLinesStr);
    }

    @Test
    void testFullStatisticsComputation() throws IOException {
        writeToFile(inputFile, List.of("150", "131", "666", "6", "3.14", "2.66", "mers", "AMG"));

        String[] args = {inputFile.toString(), "-o", outputDir.toString(), "-f"};
        Main.main(args);

        // Capture console output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Main.main(args);

        String output = outContent.toString();

        assertTrue(output.contains("Integers: 4"));
        assertTrue(output.contains("Min value: 6"));
        assertTrue(output.contains("Max value: 666"));
        assertTrue(output.contains("Sum: 953"));
        assertTrue(output.contains("Average: 238.25"));

        assertTrue(output.contains("Floating point numbers: 2"));
        assertTrue(output.contains("Min value: 2.66"));
        assertTrue(output.contains("Max value: 3.14"));
        assertTrue(output.contains("Sum: 5.800000000000001"));
        assertTrue(output.contains("Average: 2.9000000000000004"));

        assertTrue(output.contains("Strings: 2"));
        assertTrue(output.contains("Min string length: 3"));
        assertTrue(output.contains("Max string length: 4"));
    }
}
