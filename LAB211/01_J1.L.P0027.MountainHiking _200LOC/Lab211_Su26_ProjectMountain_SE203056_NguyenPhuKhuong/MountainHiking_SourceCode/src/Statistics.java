import java.util.HashMap;
import java.util.List;

public class Statistics extends HashMap<String, StatisticalInfo> {

    public Statistics() {
    }

    public Statistics(List<Student> list, Mountains mountains) {
        statisticalize(list, mountains);
    }

    public void statisticalize(List<Student> list, Mountains mountains) {
        if (list == null) {
            return;
        }
        this.clear();
        for (Student s : list) {
            String code = s.getMountainCode();
            if (code == null) {
                continue;
            }
            StatisticalInfo info = this.get(code);
            if (info == null) {
                String name = "";
                if (mountains != null) {
                    Mountain m = mountains.get(code);
                    if (m != null) {
                        name = m.getMountain();
                    }
                }
                info = new StatisticalInfo(code, name, 0, 0);
                this.put(code, info);
            }
            info.addStudent(s.getTuitionFee());
        }
    }

    public void show() {
        if (this.isEmpty()) {
            System.out.println("No registration data to summarize.");
            return;
        }
        String line = "----------------------------------------------------------------------------";
        System.out.println("Statistics of Registration by Mountain Peak:");
        System.out.println(line);
        System.out.printf("%-5s | %-25s | %-22s | %15s%n",
                "Code", "Peak Name", "Number of Participants", "Total Cost");
        System.out.println(line);
        for (StatisticalInfo info : this.values()) {
            if (info.getNumOfStudent() > 0) {
                System.out.println(info);
            }
        }
        System.out.println(line);
    }
}
