import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Mountains extends ArrayList<Mountain> {

    private final String pathFile;

    public Mountains() {
        this.pathFile = "MountainList.csv";
        readFromFile();
    }

    public Mountains(String pathFile) {
        this.pathFile = pathFile;
        readFromFile();
    }

    public Mountain get(String mountainCode) {
        if (mountainCode == null) {
            return null;
        }
        for (Mountain m : this) {
            if (m.getMountainCode().equalsIgnoreCase(mountainCode.trim())) {
                return m;
            }
        }
        return null;
    }

    public boolean isValidMountainCode(String mountainCode) {
        return get(mountainCode) != null;
    }

    public Mountain dataToObject(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        String[] parts = text.split(",", -1);
        if (parts.length < 3) {
            return null;
        }
        String code = parts[0].trim();
        String name = parts[1].trim();
        String province = parts[2].trim();
        String description = parts.length >= 4 ? parts[3].trim() : "";
        if (code.isEmpty() || name.isEmpty()) {
            return null;
        }
        return new Mountain(code, name, province, description);
    }

    public void readFromFile() {
        File file = new File(pathFile);
        if (!file.exists()) {
            System.out.println("[Warning] Mountain list file not found: " + pathFile);
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                if (firstLine) {
                    firstLine = false;
                    if (line.toLowerCase().startsWith("code")) {
                        continue;
                    }
                }
                Mountain m = dataToObject(line);
                if (m != null) {
                    this.add(m);
                }
            }
        } catch (IOException e) {
            System.out.println("[Error] Could not read mountain list: " + e.getMessage());
        }
    }

    public void showAll() {
        if (this.isEmpty()) {
            System.out.println("No mountain in the list.");
            return;
        }
        String line = "------------------------------------------------------------------------------------";
        System.out.println(line);
        System.out.printf("%-5s | %-25s | %-15s | %s%n", "Code", "Mountain", "Province", "Description");
        System.out.println(line);
        for (Mountain m : this) {
            System.out.println(m);
        }
        System.out.println(line);
    }
}
