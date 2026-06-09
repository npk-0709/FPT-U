import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Volunteers extends ArrayList<Volunteer> {

    private final String pathFile;
    private boolean isSaved;

    public Volunteers() {
        this.pathFile = "volunteers.dat";
        this.isSaved = true;
        readFromFile();
    }

    public Volunteers(String pathFile) {
        this.pathFile = pathFile;
        this.isSaved = true;
        readFromFile();
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void markUnsaved() {
        this.isSaved = false;
    }

    @Override
    public boolean add(Volunteer v) {
        if (v == null || searchById(v.getId()) != null) {
            return false;
        }
        boolean ok = super.add(v);
        if (ok) {
            markUnsaved();
        }
        return ok;
    }

    public Volunteer searchById(String id) {
        if (id == null) {
            return null;
        }
        for (Volunteer v : this) {
            if (v.getId().equalsIgnoreCase(id)) {
                return v;
            }
        }
        return null;
    }

    public boolean delete(String id) {
        if (id == null) {
            return false;
        }
        for (int i = 0; i < this.size(); i++) {
            if (this.get(i).getId().equalsIgnoreCase(id)) {
                this.remove(i);
                markUnsaved();
                return true;
            }
        }
        return false;
    }

    public void showAll() {
        showAll(this);
    }

    public void showAll(List<Volunteer> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("No volunteers registered yet.");
            return;
        }
        List<Volunteer> sortedList = new ArrayList<>(list);
        Collections.sort(sortedList);
        String line = "---------------------------------------------------------------------------------";
        System.out.println(line);
        System.out.printf("%-8s | %-25s | %-14s | %-5s | %-8s%n",
                "ID", "Name", "Skill", "Max", "Today");
        System.out.println(line);
        for (Volunteer v : sortedList) {
            System.out.println(v);
        }
        System.out.println(line);
    }

    @SuppressWarnings("unchecked")
    public final void readFromFile() {
        File file = new File(pathFile);
        if (!file.exists() || file.length() == 0) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                List<Volunteer> list = (List<Volunteer>) obj;
                this.clear();
                super.addAll(list);
                this.isSaved = true;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[Warning] Could not load volunteer data: " + e.getMessage());
        }
    }

    public boolean saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(pathFile))) {
            oos.writeObject(new ArrayList<>(this));
            exportToCsv();
            this.isSaved = true;
            return true;
        } catch (IOException e) {
            System.out.println("Could not save volunteer data: " + e.getMessage());
            return false;
        }
    }

    public String getPathFile() {
        return pathFile;
    }

    public String getCsvPathFile() {
        return "volunteers.csv";
    }

    private void exportToCsv() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(getCsvPathFile()))) {
            bw.write("VolunteerID,Name,Skill,MaxShiftsPerDay,ShiftsToday");
            bw.newLine();
            List<Volunteer> sortedList = new ArrayList<>(this);
            Collections.sort(sortedList);
            for (Volunteer v : sortedList) {
                bw.write(v.toCsv());
                bw.newLine();
            }
        }
    }
}
