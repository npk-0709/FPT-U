package business;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import model.YouthPlayer;
import model.Validatable;
import tools.Inputter;

/**
 * Lớp quản lý danh sách YouthPlayer — xử lý nghiệp vụ CRUD, lọc, save/load.
 * Áp dụng Computational Thinking:
 *   - Quyết định: suy ra đề xuất lên đội 1 từ tuổi (age >= 18).
 *   - Kiểm chứng: validate age [8..21] và clubId tồn tại.
 *   - Giải thuật: lọc danh sách cầu thủ trẻ đủ tuổi lên đội 1.
 * Sử dụng cờ dirty để biết dữ liệu đã thay đổi hay chưa.
 */
public class YouthPlayersManager {

    private List<YouthPlayer> list = new ArrayList<>();
    private boolean dirty = false;
    private static final String FILE_PATH = "youth_players.txt";

    // =====================================================================
    //  Function 15: List all youth players
    // =====================================================================
    /**
     * Hiển thị tất cả cầu thủ trẻ dạng bảng, sắp theo tên.
     * Đánh dấu cầu thủ đủ tuổi lên đội 1 (age >= 18).
     */
    public void listAll() {
        if (list.isEmpty()) {
            System.out.println("The youth player list is empty.");
            return;
        }
        List<YouthPlayer> copy = new ArrayList<>(list);
        copy.sort(null); // dùng Comparable<YouthPlayer>: theo tên
        printHeader();
        for (YouthPlayer yp : copy) {
            System.out.println(yp);
        }
        System.out.println("--------------------------------------------------------------");
        System.out.println("Total: " + list.size() + " youth player(s).");
    }

    // =====================================================================
    //  Function 16: Add a new youth player
    // =====================================================================
    /**
     * Thêm cầu thủ trẻ mới. Validate tất cả ràng buộc:
     * - Youth Player ID duy nhất (AC-xxxx)
     * - Club ID phải tồn tại
     * - Name không rỗng
     * - Age trong khoảng [8, 21]
     * Áp dụng Computational Thinking:
     *   - Kiểm chứng: validate age [8..21] và clubId tồn tại.
     *   - Quyết định: nếu age >= 18 → in đề xuất lên đội 1.
     */
    public void add(ClubsManager clubs) {
        // Nhập Youth Player ID
        String id = Inputter.inputLoop("Youth Player ID: ",
                Validatable.YOUTH_PLAYER_ID_REGEX, "Format must be AC-xxxx");
        if (findById(id) != null) {
            System.out.println("This youth player ID already exists!");
            return;
        }

        // Hiển thị danh sách club để người dùng tham khảo
        System.out.println("\n--- Available Clubs ---");
        clubs.listAll();
        System.out.println();

        // Nhập Club ID — kiểm chứng: clubId phải tồn tại
        String clubId = Inputter.inputLoop("Club ID: ",
                Validatable.CLUB_ID_REGEX, "Format must be CL-xxxx");
        if (clubs.findById(clubId) == null) {
            System.out.println("This club does not exist!");
            return;
        }

        // Nhập tên cầu thủ trẻ
        String name = Inputter.inputNonEmpty("Youth player name: ");

        // Nhập tuổi — kiểm chứng: age [8..21]
        int age = Inputter.inputInt("Age (" + Validatable.YOUTH_MIN_AGE
                + "-" + Validatable.YOUTH_MAX_AGE + "): ",
                Validatable.YOUTH_MIN_AGE, Validatable.YOUTH_MAX_AGE);

        YouthPlayer yp = new YouthPlayer(id, clubId, name, age);
        list.add(yp);
        dirty = true;
        System.out.println("Youth player added successfully.");

        // Quyết định: suy ra đề xuất lên đội 1 từ tuổi
        checkAndSuggestPromotion(yp);
    }

    // =====================================================================
    //  Function 17: Update a youth player by ID
    // =====================================================================
    /**
     * Cập nhật thông tin cầu thủ trẻ. Enter rỗng = giữ nguyên field.
     * Sau khi cập nhật tuổi → suy ra đề xuất lên đội 1.
     * Áp dụng Computational Thinking:
     *   - Kiểm chứng: validate age [8..21].
     *   - Quyết định: nếu age mới >= 18 → in đề xuất lên đội 1.
     */
    public void update(ClubsManager clubs) {
        String id = Inputter.inputLoop("Youth Player ID: ",
                Validatable.YOUTH_PLAYER_ID_REGEX, "Format must be AC-xxxx");
        YouthPlayer yp = findById(id);
        if (yp == null) {
            System.out.println("This youth player does not exist!");
            return;
        }

        // Hiển thị thông tin hiện tại
        System.out.println("Current info: " + yp.getDisplayInfo());

        // Cập nhật tên — rỗng thì bỏ qua
        String name = Inputter.inputOptional(
                "Name [" + yp.getName() + "] (Enter to skip): ");
        if (!name.isEmpty()) yp.setName(name);

        // Cập nhật tuổi — validate [8..21]
        String ageStr = Inputter.inputOptional(
                "Age [" + yp.getAge() + "] (Enter to skip): ");
        if (!ageStr.isEmpty()) {
            try {
                int age = Integer.parseInt(ageStr);
                if (age >= Validatable.YOUTH_MIN_AGE && age <= Validatable.YOUTH_MAX_AGE) {
                    yp.setAge(age);
                } else {
                    System.out.println("Age must be between " + Validatable.YOUTH_MIN_AGE
                            + " and " + Validatable.YOUTH_MAX_AGE + ". Keeping current value.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Keeping current value.");
            }
        }

        dirty = true;
        System.out.println("Youth player updated successfully.");

        // Quyết định: suy ra đề xuất lên đội 1 từ tuổi mới
        checkAndSuggestPromotion(yp);
    }

    // =====================================================================
    //  Function 18: Remove a youth player by ID (with confirmation)
    // =====================================================================
    /**
     * Xoá cầu thủ trẻ theo ID. Hỏi xác nhận trước khi xoá.
     */
    public void remove() {
        String id = Inputter.inputLoop("Youth Player ID: ",
                Validatable.YOUTH_PLAYER_ID_REGEX, "Format must be AC-xxxx");
        YouthPlayer yp = findById(id);
        if (yp == null) {
            System.out.println("This youth player does not exist!");
            return;
        }

        // Hiển thị thông tin trước khi xóa
        System.out.println("Youth player to delete: " + yp.getDisplayInfo());

        // Xác nhận trước khi xóa
        boolean confirm = Inputter.inputYesNo("Are you sure you want to delete? (Y/N): ");
        if (confirm) {
            list.remove(yp);
            dirty = true;
            System.out.println("Youth player removed successfully.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    // =====================================================================
    //  Function 19: List youth players eligible for first team
    // =====================================================================
    /**
     * Giải thuật: Lọc danh sách cầu thủ trẻ đủ tuổi lên đội 1 (age >= 18).
     * Áp dụng Computational Thinking:
     *   - Giải thuật: duyệt danh sách, lọc theo điều kiện age >= 18.
     */
    public void listEligibleForFirstTeam() {
        if (list.isEmpty()) {
            System.out.println("The youth player list is empty.");
            return;
        }

        List<YouthPlayer> eligible = new ArrayList<>();
        for (YouthPlayer yp : list) {
            if (yp.isEligibleForFirstTeam()) {
                eligible.add(yp);
            }
        }

        if (eligible.isEmpty()) {
            System.out.println("No youth players are currently eligible for the first team (age >= "
                    + Validatable.FIRST_TEAM_AGE + ").");
            return;
        }

        eligible.sort(null); // sắp theo tên
        System.out.println("=== Youth Players Eligible for First Team (age >= "
                + Validatable.FIRST_TEAM_AGE + ") ===");
        printHeader();
        for (YouthPlayer yp : eligible) {
            System.out.println(yp);
        }
        System.out.println("--------------------------------------------------------------");
        System.out.println("Total eligible: " + eligible.size() + " youth player(s).");
    }

    // =====================================================================
    //  Save youth players to file
    // =====================================================================
    /**
     * Ghi danh sách youth player ra file youth_players.txt.
     */
    public void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (YouthPlayer yp : list) {
                pw.printf("%s, %s, %s, %d%n",
                        yp.getId(), yp.getClubId(), yp.getName(), yp.getAge());
            }
            dirty = false;
            System.out.println("Youth player data saved to youth_players.txt.");
        } catch (IOException e) {
            System.out.println("Save failed: " + e.getMessage());
        }
    }

    // =====================================================================
    //  Load youth players from file (strict validation)
    // =====================================================================
    /**
     * Nạp dữ liệu youth player từ file. Validate chặt từng dòng.
     * Kiểm tra: format, Youth Player ID regex, Club ID tồn tại,
     * age [8..21], trùng ID.
     * @param path  đường dẫn file
     * @param clubs ClubsManager để validate clubId
     * @return true nếu nạp thành công
     */
    public boolean loadStrict(String path, ClubsManager clubs) {
        List<YouthPlayer> tmp = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\s*,\\s*");
                // Phải đúng 4 trường
                if (parts.length != 4) return false;
                // Validate Youth Player ID format
                if (!Validatable.isValid(parts[0], Validatable.YOUTH_PLAYER_ID_REGEX))
                    return false;
                // Validate Club ID format + tồn tại
                if (!Validatable.isValid(parts[1], Validatable.CLUB_ID_REGEX))
                    return false;
                if (clubs.findById(parts[1]) == null) return false;
                // Name không rỗng
                if (parts[2].isEmpty()) return false;
                // Validate age [8..21]
                int age;
                try {
                    age = Integer.parseInt(parts[3]);
                } catch (NumberFormatException e) {
                    return false;
                }
                if (age < Validatable.YOUTH_MIN_AGE || age > Validatable.YOUTH_MAX_AGE)
                    return false;
                // Kiểm tra trùng Youth Player ID trong tmp
                for (YouthPlayer existing : tmp) {
                    if (existing.getId().equals(parts[0])) return false;
                }
                tmp.add(new YouthPlayer(parts[0], parts[1], parts[2], age));
            }
        } catch (FileNotFoundException e) {
            // File chưa tồn tại thì cho qua (danh sách rỗng)
            list = new ArrayList<>();
            dirty = false;
            return true;
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

    /** Tìm cầu thủ trẻ theo ID (case-insensitive). */
    public YouthPlayer findById(String id) {
        for (YouthPlayer yp : list) {
            if (yp.getId().equalsIgnoreCase(id)) return yp;
        }
        return null;
    }

    /** Kiểm tra dữ liệu đã thay đổi chưa (dirty flag). */
    public boolean isDirty() {
        return dirty;
    }

    /** Lấy danh sách youth player. */
    public List<YouthPlayer> getList() {
        return list;
    }

    /**
     * Computational Thinking — Quyết định:
     * Suy ra đề xuất lên đội 1 từ tuổi.
     * Nếu age >= 18 → in thông báo đề xuất.
     * @param yp cầu thủ trẻ cần kiểm tra
     */
    private void checkAndSuggestPromotion(YouthPlayer yp) {
        if (yp.isEligibleForFirstTeam()) {
            System.out.println("╔══════════════════════════════════════════════════════╗");
            System.out.println("║  ★ PROMOTION SUGGESTION                              ║");
            System.out.println("║  Youth player: " + padRight(yp.getName(), 38) + "║");
            System.out.println("║  Age: " + padRight(String.valueOf(yp.getAge()), 47) + "║");
            System.out.println("║  → Eligible to be promoted to the FIRST TEAM!        ║");
            System.out.println("╚══════════════════════════════════════════════════════╝");
        }
    }

    /** In header bảng youth player. */
    private void printHeader() {
        System.out.printf("%-8s| %-10s| %-22s| %-4s| %s%n",
                "ID", "Club ID", "Player Name", "Age", "First Team?");
        System.out.println("--------------------------------------------------------------");
    }

    /** Pad chuỗi bên phải để căn bảng. */
    private String padRight(String s, int n) {
        if (s.length() >= n) return s.substring(0, n);
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < n) sb.append(' ');
        return sb.toString();
    }
}
