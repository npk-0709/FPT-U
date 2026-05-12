package com.hrms.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileService {

    public static void saveToFile(String fileName, List<String> data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String line : data) {
                writer.write(line);
                writer.newLine();
            }
            System.out.println("Data saved to " + fileName + " successfully.");
        } catch (IOException e) {
            System.err.println("Error saving file " + fileName + ": " + e.getMessage());
        }
    }

    public static List<String> loadFromFile(String fileName) {
        List<String> data = new ArrayList<>();
        File file = new File(fileName);

        if (!file.exists()) {
            return data;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    data.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file " + fileName + ": " + e.getMessage());
        }
        return data;
    }
}
