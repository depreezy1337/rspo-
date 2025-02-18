package org.example;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main {

    static class IntStats {
        int count = 0;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        long sum = 0;
    }

    static class FloatStats {
        int count = 0;
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        double sum = 0;
    }

    static class StringStats {
        int count = 0;
        int minLength = Integer.MAX_VALUE;
        int maxLength = 0;
    }

    public static void main(String[] args) {
        List<String> inputFiles = new ArrayList<>();
        String outputPath = "";
        String prefix = "";
        boolean append = false;
        boolean shortStats = false;
        boolean fullStats = false;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-o":
                    if (i + 1 < args.length) outputPath = args[++i];
                    break;
                case "-p":
                    if (i + 1 < args.length) prefix = args[++i];
                    break;
                case "-a":
                    append = true;
                    break;
                case "-s":
                    shortStats = true;
                    break;
                case "-f":
                    fullStats = true;
                    break;
                default:
                    inputFiles.add(args[i]);
                    break;
            }
        }

        if (inputFiles.isEmpty()) {
            System.err.println("No input files specified.");
            return;
        }

        Path intFile = Paths.get(outputPath, prefix + "integers.txt");
        Path floatFile = Paths.get(outputPath, prefix + "floats.txt");
        Path stringFile = Paths.get(outputPath, prefix + "strings.txt");

        boolean[] hasData = {false, false, false};
        IntStats intStats = new IntStats();
        FloatStats floatStats = new FloatStats();
        StringStats stringStats = new StringStats();

        try (
                BufferedWriter intWriter = Files.newBufferedWriter(intFile, append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                BufferedWriter floatWriter = Files.newBufferedWriter(floatFile, append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                BufferedWriter stringWriter = Files.newBufferedWriter(stringFile, append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)
        ) {
            for (String inputFile : inputFiles) {
                processFile(inputFile, intWriter, floatWriter, stringWriter, hasData,
                        intStats, floatStats, stringStats);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        removeEmptyFile(intFile, hasData[0]);
        removeEmptyFile(floatFile, hasData[1]);
        removeEmptyFile(stringFile, hasData[2]);

        if (shortStats || fullStats) {
            printStats(intStats, floatStats, stringStats, shortStats, fullStats, hasData);
        }
    }

    private static void processFile(String inputFile, BufferedWriter intWriter, BufferedWriter floatWriter,
                                    BufferedWriter stringWriter, boolean[] hasData,
                                    IntStats intStats, FloatStats floatStats, StringStats stringStats) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.matches("^-?\\d+$")) {
                    intWriter.write(line);
                    intWriter.newLine();
                    hasData[0] = true;
                    intStats.count++;
                    int intValue = Integer.parseInt(line);
                    intStats.min = Math.min(intStats.min, intValue);
                    intStats.max = Math.max(intStats.max, intValue);
                    intStats.sum += intValue;
                } else if (line.matches("^-?\\d*\\.\\d+([eE][+-]?\\d+)?$") || line.matches("^-?\\d+([eE][+-]?\\d+)$")) {
                    floatWriter.write(line);
                    floatWriter.newLine();
                    hasData[1] = true;
                    floatStats.count++;
                    double floatValue = Double.parseDouble(line);
                    floatStats.min = Math.min(floatStats.min, floatValue);
                    floatStats.max = Math.max(floatStats.max, floatValue);
                    floatStats.sum += floatValue;
                } else {
                    stringWriter.write(line);
                    stringWriter.newLine();
                    hasData[2] = true;
                    stringStats.count++;
                    int length = line.length();
                    stringStats.minLength = Math.min(stringStats.minLength, length);
                    stringStats.maxLength = Math.max(stringStats.maxLength, length);
                }
            }
        } catch (IOException e) {
            System.err.println("Error processing file: " + inputFile);
            e.printStackTrace();
        }
    }

    private static void printStats(IntStats intStats, FloatStats floatStats, StringStats stringStats,
                                   boolean shortStats, boolean fullStats, boolean[] hasData) {
        System.out.println("Statistics:");

        if (hasData[0]) {
            System.out.println("Integers: " + intStats.count);
            if (fullStats) {
                System.out.println("Min value: " + (intStats.count > 0 ? intStats.min : "N/A"));
                System.out.println("Max value: " + (intStats.count > 0 ? intStats.max : "N/A"));
                System.out.println("Sum: " + intStats.sum);
                System.out.println("Average: " + (intStats.count > 0 ? (double) intStats.sum / intStats.count : "N/A"));
            }
        }

        if (hasData[1]) {
            System.out.println("Floating point numbers: " + floatStats.count);
            if (fullStats) {
                System.out.println("Min value: " + (floatStats.count > 0 ? floatStats.min : "N/A"));
                System.out.println("Max value: " + (floatStats.count > 0 ? floatStats.max : "N/A"));
                System.out.println("Sum: " + floatStats.sum);
                System.out.println("Average: " + (floatStats.count > 0 ? floatStats.sum / floatStats.count : "N/A"));
            }
        }

        if (hasData[2]) {
            System.out.println("Strings: " + stringStats.count);
            if (fullStats) {
                System.out.println("Min string length: " + (stringStats.count > 0 ? stringStats.minLength : "N/A"));
                System.out.println("Max string length: " + (stringStats.count > 0 ? stringStats.maxLength : "N/A"));
            }
        }
    }

    private static void removeEmptyFile(Path file, boolean hasData) {
        if (!hasData) {
            try {
                Files.deleteIfExists(file);
            } catch (IOException e) {
                System.err.println("Error deleting empty file: " + file);
                e.printStackTrace();
            }
        }
    }
}