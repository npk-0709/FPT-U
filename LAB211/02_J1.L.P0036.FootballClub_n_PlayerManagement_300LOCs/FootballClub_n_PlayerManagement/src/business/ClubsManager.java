package business;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import model.Club;
import model.Validatable;
import tools.Inputter;

/**
 * Lớp quản lý danh sách Club — xử lý nghiệp vụ CRUD, lọc, save/load.
 * Sử dụng cờ dirty để biết dữ liệu đã thay đổi hay chưa.
 */
public class ClubsManager {

    private List<Club> list = new ArrayList<>();
    private boolean dirty = false;
    private static final String FILE_PATH = "clubs.txt";

    // =====================================================================
    //  Function 1: List all clubs
    // =====================================================================
    /**
     * Hiển thị tất cả club dạng bảng, sắp theo tên (Comparable).
     */
    public void listAll() {
        if (list.isEmpty()) {
            System.out.println("The club list is empty.");
            return;
        }
        list.sort(null); // dùng Comparable<Club>: theo tên
        printHeader();
        list.forEach(System.out::println);
    }

    // =====================================================================
    //  Function 2: Add a new club
    // =====================================================================
    /**
     * Thêm club mới. Validate ID, name, sponsor, budget.
     * Chặn trùng ID.
     */
    public void add() {
        String id = Inputter.inputLoop("Club ID: ",
                Validatable.CLUB_ID_REGEX, "Format must be CL-xxxx");
        if (findById(id) != null) {
            System.out.println("This club ID already exists!");
            return;
        }
        String name    = Inputter.inputNonEmpty("Club name: ");
        String sponsor = Inputter.inputNonEmpty("Sponsor brand: ");
        double budget  = Inputter.inputPositiveDouble("Budget (million EUR): ");

        list.add(new Club(id, name, sponsor, budget));
        dirty = true;
        System.out.println("Club added successfully.");
    }

    // =====================================================================
    //  Function 3: Search club by ID
    // =====================================================================
    /**
     * Tìm kiếm club theo ID. Hiển thị thông tin hoặc báo không tồn tại.
     */
    public void searchById() {
        String id = Inputter.inputLoop("Club ID: ",
                Validatable.CLUB_ID_REGEX, "Format must be CL-xxxx");
        Club c = findById(id);
        if (c == null) {
            System.out.println("This club does not exist!");
        } else {
            printHeader();
            System.out.println(c);
        }
    }

    // =====================================================================
    //  Function 4: Update a club by ID
    // =====================================================================
    /**
     * Cập nhật club theo ID. Enter rỗng = giữ nguyên field.
     */
    public void update() {
        String id = Inputter.inputLoop("Club ID: ",
                Validatable.CLUB_ID_REGEX, "Format must be CL-xxxx");
        Club c = findById(id);
        if (c == null) {
            System.out.println("This club does not exist!");
            return;
        }

        // Cập nhật tên — rỗng thì bỏ qua
        String name = Inputter.inputOptional(
                "Club name [" + c.getName() + "] (Enter to skip): ");
        if (!name.isEmpty()) c.setName(name);

        // Cập nhật sponsor — rỗng thì bỏ qua
        String sponsor = Inputter.inputOptional(
                "Sponsor brand [" + c.getSponsor() + "] (Enter to skip): ");
        if (!sponsor.isEmpty()) c.setSponsor(sponsor);

        // Cập nhật budget — rỗng thì bỏ qua, validate dương
        String budgetStr = Inputter.inputOptional(
                "Budget [" + String.format("%.0f", c.getBudget()) + "] (Enter to skip): ");
        if (!budgetStr.isEmpty()) {
            try {
                double budget = Double.parseDouble(budgetStr);
                if (budget > 0) {
                    c.setBudget(budget);
                } else {
                    System.out.println("Budget must be positive. Keeping current value.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Keeping current value.");
            }
        }

        dirty = true;
        System.out.println("Club updated successfully.");
    }

    // =====================================================================
    //  Function 5: List clubs with budget <= input value
    // =====================================================================
    /**
     * Hiển thị tất cả club có budget ≤ giá trị nhập vào.
     */
    public void listByBudget() {
        double max = Inputter.inputPositiveDouble("Max budget (million EUR): ");
        printHeader();
        boolean any = false;
        for (Club c : list) {
            if (c.getBudget() <= max) {
                System.out.println(c);
                any = true;
            }
        }
        if (!any) System.out.println("No clubs match the budget condition.");
    }

    // =====================================================================
    //  Function 12: Save clubs to file
    // =====================================================================
    /**
     * Ghi danh sách club ra file clubs.txt.
     */
    public void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Club c : list) {
                pw.printf("%s, %s, %s, %.0f%n",
                        c.getClubID(), c.getName(), c.getSponsor(), c.getBudget());
            }
            dirty = false;
            System.out.println("Club data saved to clubs.txt.");
        } catch (IOException e) {
            System.out.println("Save failed: " + e.getMessage());
        }
    }

    // =====================================================================
    //  Function 13: Load clubs from file (strict validation)
    // =====================================================================
    /**
     * Nạp dữ liệu club từ file. Validate chặt từng dòng.
     * Nếu bất kỳ dòng nào sai → trả false (Load data failed).
     * @param path đường dẫn file
     * @return true nếu nạp thành công
     */
    public boolean loadStrict(String path) {
        List<Club> tmp = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split("\\s*,\\s*");
                // Phải đúng 4 trường
                if (parts.length != 4) return false;
                // Validate Club ID format
                if (!Validatable.isValid(parts[0], Validatable.CLUB_ID_REGEX)) return false;
                // Name & Sponsor không rỗng
                if (parts[1].isEmpty() || parts[2].isEmpty()) return false;
                // Budget phải là số dương
                double budget;
                try {
                    budget = Double.parseDouble(parts[3]);
                } catch (NumberFormatException e) {
                    return false;
                }
                if (budget <= 0) return false;
                // Kiểm tra trùng Club ID trong tmp
                for (Club existing : tmp) {
                    if (existing.getClubID().equals(parts[0])) return false;
                }
                tmp.add(new Club(parts[0], parts[1], parts[2], budget));
            }
        } catch (Exception e) {
            return false;
        }
        list = tmp;
        dirty = false;
        return true;
    }

    // =====================================================================
    //  Helper methods
    // =====================================================================

    /** Tìm club theo ID (case-insensitive). */
    public Club findById(String id) {
        for (Club c : list) {
            if (c.getClubID().equalsIgnoreCase(id)) return c;
        }
        return null;
    }

    /** Lấy tên club theo ID (dùng cho sort player). */
    public String getName(String clubId) {
        Club c = findById(clubId);
        return c != null ? c.getName() : null;
    }

    /** Kiểm tra dữ liệu đã thay đổi chưa (dirty flag). */
    public boolean isDirty() {
        return dirty;
    }

    /** Lấy danh sách club. */
    public List<Club> getList() {
        return list;
    }

    /** In header bảng club. */
    private void printHeader() {
        System.out.printf("%-10s| %-26s| %-10s| %s%n",
                "Club ID", "Club Name", "Sponsor", "Budget");
        System.out.println("-".repeat(62));
    }
}
