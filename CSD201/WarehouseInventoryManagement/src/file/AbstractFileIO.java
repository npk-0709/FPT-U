package file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFileIO<T> implements IFileIO<T> {

    protected abstract String getFileName();

    protected abstract T dataToObject(String line);

    protected abstract String toFileString(T obj);

    protected File getFile() {
        File f = new File(getFileName());
        if (!f.exists()) {
            File sub = new File("WarehouseInventoryManagement", getFileName());
            if (sub.exists()) {
                return sub;
            }
        }
        return f;
    }

    @Override
    public List<T> readFromFile() {
        List<T> list = new ArrayList<>();
        File f = getFile();

        if (!f.exists()) {
            System.out.println("[Notice] " + getFileName() + " not found. Starting with empty list.");
            return list;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {

                if (isFirstLine) {
                    if (line.startsWith("\uFEFF")) {
                        line = line.substring(1);
                    }
                    isFirstLine = false;
                }

                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                T obj = dataToObject(line);
                if (obj != null) {
                    list.add(obj);
                }
            }
        } catch (IOException e) {
            System.out.println("[Error] Cannot read " + getFileName() + ": " + e.getMessage());
            return null;
        }

        System.out.println("[Success] " + getFileName() + " loaded successfully (" + list.size() + " records).");
        return list;
    }

    @Override
    public boolean saveToFile(List<T> list) {
        File f = getFile();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8))) {
            for (T obj : list) {
                bw.write(toFileString(obj));
                bw.newLine();
            }
            System.out.println("[Success] " + getFileName() + " saved successfully.");
            return true;
        } catch (IOException e) {
            System.out.println("[Error] Cannot save " + getFileName() + ": " + e.getMessage());
            return false;
        }
    }
}
