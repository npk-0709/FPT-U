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

public class Students extends ArrayList<Student> {

    private final String pathFile;
    private boolean isSaved;

    public Students() {
        this.pathFile = "registrations.dat";
        this.isSaved = true;
        readFromFile();
    }

    public Students(String pathFile) {
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
    public boolean add(Student x) {
        if (x == null || searchById(x.getId()) != null) {
            return false;
        }
        boolean ok = super.add(x);
        if (ok) {
            markUnsaved();
        }
        return ok;
    }

    public boolean update(Student x) {
        if (x == null) {
            return false;
        }
        for (int i = 0; i < this.size(); i++) {
            if (this.get(i).getId().equalsIgnoreCase(x.getId())) {
                this.set(i, x);
                markUnsaved();
                return true;
            }
        }
        return false;
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

    public Student searchById(String id) {
        if (id == null) {
            return null;
        }
        for (Student s : this) {
            if (s.getId().equalsIgnoreCase(id)) {
                return s;
            }
        }
        return null;
    }

    public List<Student> searchByName(String name) {
        List<Student> result = new ArrayList<>();
        if (name == null || name.trim().isEmpty()) {
            return result;
        }
        String key = name.trim().toLowerCase();
        for (Student s : this) {
            if (s.getName() != null && s.getName().toLowerCase().contains(key)) {
                result.add(s);
            }
        }
        return result;

    }

    public List<Student> filterByCampusCode(String campusCode) {
        List<Student> result = new ArrayList<>();
        if (campusCode == null) {
            return result;
        }
        String key = campusCode.trim().toUpperCase();
        for (Student s : this) {
            if (s.getCampusCode().equals(key)) {
                result.add(s);
            }
        }
        return result;
    }

    public void showAll() {
        showAll(this);
    }

    public void showAll(List<Student> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("No students have registered yet.");
            return;
        }
        List<Student> sortedList = new ArrayList<>(list);
        Collections.sort(sortedList);
        String line = "------------------------------------------------------------------------------------------------------";
        System.out.println(line);
        System.out.printf("%-10s | %-20s | %-12s | %-25s | %-9s | %12s%n",
                "StudentID", "Name", "Phone", "Email", "PeakCode", "Fee");
        System.out.println(line);
        for (Student s : sortedList) {
            System.out.println(s);
        }
        System.out.println(line);
    }

    public void statisticalizeByMountainPeak(Mountains mountains) {
        if (this.isEmpty()) {
            System.out.println("No registration data available for statistics.");
            return;
        }
        Statistics stats = new Statistics(this, mountains);
        stats.show();
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
                List<Student> list = (List<Student>) obj;
                this.clear();
                super.addAll(list);
                this.isSaved = true;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[Warning] Could not load registration data: " + e.getMessage());
        }
    }

    public boolean saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(pathFile))) {
            oos.writeObject(new ArrayList<>(this));
            exportToCsv();
            this.isSaved = true;
            return true;
        } catch (IOException e) {
            System.out.println("Could not save registration data: " + e.getMessage());
            return false;
        }
    }

    public String getPathFile() {
        return pathFile;
    }

    public String getCsvPathFile() {
        return "registrations.csv";
    }

    private void exportToCsv() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(getCsvPathFile()))) {
            bw.write("StudentID,Name,Phone,Email,MountainCode,TuitionFee");
            bw.newLine();
            List<Student> sortedList = new ArrayList<>(this);
            Collections.sort(sortedList);
            for (Student s : sortedList) {
                bw.write(s.toCsv());
                bw.newLine();
            }
        }
    }
}
