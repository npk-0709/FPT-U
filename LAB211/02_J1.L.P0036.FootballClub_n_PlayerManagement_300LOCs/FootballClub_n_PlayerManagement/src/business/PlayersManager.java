package business;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import model.Player;
import model.Validatable;
import tools.Inputter;

/**
 * Lớp quản lý danh sách Player — xử lý nghiệp vụ CRUD, sort, search, save/load.
 * Sử dụng cờ dirty để biết dữ liệu đã thay đổi hay chưa.
 */
public class PlayersManager {

    private List<Player> list = new ArrayList<>();
    private boolean dirty = false;
    private static final String FILE_PATH = "players.txt";

    // =====================================================================
    //  Function 6: List players sorted by club name, then shirt number
    // =====================================================================
    /**
     * Hiển thị tất cả cầu thủ, sắp tăng dần theo tên club,
     * cùng club thì sắp theo số áo tăng dần.
     * Áp dụng Polymorphism: Comparator chaining + null-safe.
     */
    public void listSortedByClubThenShirt(ClubsManager clubs) {
        if (list.isEmpty()) {
            System.out.println("The player list is empty.");
            return;
        }
        List<Player> copy = new ArrayList<>(list);

        // Comparator chaining: club name ASC → shirt number ASC
        // nullsLast xử lý trường hợp club name null (club không tồn tại)
        Comparator<Player> byClubName = Comparator.comparing(
                p -> clubs.getName(p.getClubID()),
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
        );
        copy.sort(byClubName.thenComparingInt(Player::getShirtNumber));

        printHeader();
        copy.forEach(System.out::println);
    }

    // =====================================================================
    //  Function 7: Search players by partial player name
    // =====================================================================
    /**
     * Tìm cầu thủ theo tên (so khớp một phần, case-insensitive).
     */
    public void searchByPartialName() {
        String kw = Inputter.inputNonEmpty("Partial player name: ").toLowerCase();
        List<Player> found = new ArrayList<>();
        for (Player p : list) {
            if (p.getName().toLowerCase().contains(kw)) {
                found.add(p);
            }
        }
        if (found.isEmpty()) {
            System.out.println("No players match the search criteria!");
        } else {
            printHeader();
            found.forEach(System.out::println);
        }
    }

    // =====================================================================
    //  Function 8: Add a new player
    // =====================================================================
    /**
     * Thêm cầu thủ mới. Validate tất cả ràng buộc:
     * - Player ID duy nhất (Pxxxx)
     * - Club ID phải tồn tại
     * - Position hợp lệ (case-insensitive)
     * - Shirt number 1–99 và duy nhất trong cùng club
     */
    public void add(ClubsManager clubs) {
        // Nhập Player ID
        String id = Inputter.inputLoop("Player ID: ",
                Validatable.PLAYER_ID_REGEX, "Format must be Pxxxx");
        if (findById(id) != null) {
            System.out.println("This player ID already exists!");
            return;
        }

        // Hiển thị danh sách club để người dùng tham khảo
        System.out.println("\n--- Available Clubs ---");
        clubs.listAll();
        System.out.println();

        // Nhập Club ID — kiểm tra tồn tại
        String clubId = Inputter.inputLoop("Club ID: ",
                Validatable.CLUB_ID_REGEX, "Format must be CL-xxxx");
        if (clubs.findById(clubId) == null) {
            System.out.println("This club does not exist!");
            return;
        }

        // Nhập tên cầu thủ
        String name = Inputter.inputNonEmpty("Player name: ");

        // Nhập vị trí — validate trong tập POSITIONS
        String position;
        while (true) {
            position = Inputter.inputNonEmpty(
                    "Position (Goalkeeper/Defender/Midfielder/Forward/Winger): ");
            if (Validatable.isPosition(position)) break;
            System.out.println("Invalid position! Must be: Goalkeeper, Defender, Midfielder, Forward, Winger.");
        }
        // Chuẩn hoá: viết hoa chữ cái đầu
        position = position.substring(0, 1).toUpperCase()
                 + position.substring(1).toLowerCase();

        // Nhập số áo — validate 1–99 + duy nhất trong club
        int shirt = Inputter.inputInt("Shirt number (1-99): ", 1, 99);
        if (shirtTakenInClub(clubId, shirt, null)) {
            System.out.println("This shirt number already exists in this club!");
            return;
        }

        list.add(new Player(id, clubId, name, position, shirt));
        dirty = true;
        System.out.println("Player added successfully.");
    }

    // =====================================================================
    //  Function 9: Remove a player by ID
    // =====================================================================
    /**
     * Xoá cầu thủ theo ID. Báo lỗi nếu không tồn tại.
     */
    public void remove() {
        String id = Inputter.inputLoop("Player ID: ",
                Validatable.PLAYER_ID_REGEX, "Format must be Pxxxx");
        Player p = findById(id);
        if (p == null) {
            System.out.println("This player does not exist!");
            return;
        }
        list.remove(p);
        dirty = true;
        System.out.println("Player removed successfully.");
    }

    // =====================================================================
    //  Function 10: Update a player by ID
    // =====================================================================
    /**
     * Cập nhật thông tin cầu thủ. Enter rỗng = giữ nguyên field.
     * Khi cập nhật số áo, kiểm tra duy nhất trong cùng club.
     */
    public void update(ClubsManager clubs) {
        String id = Inputter.inputLoop("Player ID: ",
                Validatable.PLAYER_ID_REGEX, "Format must be Pxxxx");
        Player p = findById(id);
        if (p == null) {
            System.out.println("This player does not exist!");
            return;
        }

        // Cập nhật tên
        String name = Inputter.inputOptional(
                "Name [" + p.getName() + "] (Enter to skip): ");
        if (!name.isEmpty()) p.setName(name);

        // Cập nhật vị trí — validate nếu nhập
        String pos = Inputter.inputOptional(
                "Position [" + p.getPosition() + "] (Enter to skip): ");
        if (!pos.isEmpty()) {
            if (Validatable.isPosition(pos)) {
                // Chuẩn hoá
                pos = pos.substring(0, 1).toUpperCase()
                    + pos.substring(1).toLowerCase();
                p.setPosition(pos);
            } else {
                System.out.println("Invalid position! Keeping current value.");
            }
        }

        // Cập nhật số áo — validate 1–99 + duy nhất trong club
        String shirtStr = Inputter.inputOptional(
                "Shirt number [" + p.getShirtNumber() + "] (Enter to skip): ");
        if (!shirtStr.isEmpty()) {
            try {
                int shirt = Integer.parseInt(shirtStr);
                if (shirt >= 1 && shirt <= 99) {
                    if (!shirtTakenInClub(p.getClubID(), shirt, p)) {
                        p.setShirtNumber(shirt);
                    } else {
                        System.out.println("This shirt number already exists in this club!");
                    }
                } else {
                    System.out.println("Shirt number must be between 1 and 99. Keeping current value.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Keeping current value.");
            }
        }

        dirty = true;
        System.out.println("Player updated successfully.");
    }

    // =====================================================================
    //  Function 11: List players by a specific position
    // =====================================================================
    /**
     * Hiển thị tất cả cầu thủ theo vị trí cụ thể (case-insensitive).
     */
    public void listByPosition() {
        String pos = Inputter.inputNonEmpty("Position: ");
        printHeader();
        boolean any = false;
        for (Player p : list) {
            if (p.getPosition().equalsIgnoreCase(pos)) {
                System.out.println(p);
                any = true;
            }
        }
        if (!any) System.out.println("No players play in this position.");
    }

    // =====================================================================
    //  Function 12: Save players to file
    // =====================================================================
    /**
     * Ghi danh sách player ra file players.txt.
     */
    public void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Player p : list) {
                pw.printf("%s, %s, %s, %s, %d%n",
                        p.getId(), p.getClubID(), p.getName(),
                        p.getPosition(), p.getShirtNumber());
            }
            dirty = false;
            System.out.println("Player data saved to players.txt.");
        } catch (IOException e) {
            System.out.println("Save failed: " + e.getMessage());
        }
    }

    // =====================================================================
    //  Function 13: Load players from file (strict validation)
    // =====================================================================
    /**
     * Nạp dữ liệu player từ file. Validate chặt từng dòng.
     * Kiểm tra: format, Player ID regex, Club ID tồn tại, position hợp lệ,
     * shirt 1–99, trùng ID, trùng số áo trong cùng club.
     * @param path  đường dẫn file
     * @param clubs ClubsManager để validate clubID
     * @return true nếu nạp thành công
     */
    public boolean loadStrict(String path, ClubsManager clubs) {
        List<Player> tmp = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\s*,\\s*");
                // Phải đúng 5 trường
                if (parts.length != 5) return false;
                // Validate Player ID format
                if (!Validatable.isValid(parts[0], Validatable.PLAYER_ID_REGEX))
                    return false;
                // Validate Club ID format + tồn tại
                if (!Validatable.isValid(parts[1], Validatable.CLUB_ID_REGEX))
                    return false;
                if (clubs.findById(parts[1]) == null) return false;
                // Player name không rỗng
                if (parts[2].isEmpty()) return false;
                // Validate position
                if (!Validatable.isPosition(parts[3])) return false;
                // Validate shirt number 1–99
                int shirt;
                try {
                    shirt = Integer.parseInt(parts[4]);
                } catch (NumberFormatException e) {
                    return false;
                }
                if (shirt < 1 || shirt > 99) return false;
                // Kiểm tra trùng Player ID trong tmp
                for (Player existing : tmp) {
                    if (existing.getId().equals(parts[0])) return false;
                }
                // Kiểm tra trùng số áo trong cùng club
                for (Player existing : tmp) {
                    if (existing.getClubID().equals(parts[1])
                            && existing.getShirtNumber() == shirt) {
                        return false;
                    }
                }
                tmp.add(new Player(parts[0], parts[1], parts[2], parts[3], shirt));
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

    /** Hiển thị tất cả cầu thủ (không sort). */
    public void listAll() {
        if (list.isEmpty()) {
            System.out.println("The player list is empty.");
            return;
        }
        printHeader();
        list.forEach(System.out::println);
    }

    /** Tìm cầu thủ theo ID (case-insensitive). */
    public Player findById(String id) {
        for (Player p : list) {
            if (p.getId().equalsIgnoreCase(id)) return p;
        }
        return null;
    }

    /**
     * Kiểm tra số áo đã được dùng trong club chưa.
     * @param clubId ID club
     * @param shirt  số áo cần kiểm tra
     * @param except cầu thủ cần loại trừ (dùng khi update)
     * @return true nếu đã có người mặc số áo đó trong club
     */
    private boolean shirtTakenInClub(String clubId, int shirt, Player except) {
        for (Player p : list) {
            if (p != except
                    && p.getClubID().equals(clubId)
                    && p.getShirtNumber() == shirt) {
                return true;
            }
        }
        return false;
    }

    /** Kiểm tra dữ liệu đã thay đổi chưa (dirty flag). */
    public boolean isDirty() {
        return dirty;
    }

    /** Lấy danh sách player. */
    public List<Player> getList() {
        return list;
    }

    /** In header bảng player. */
    private void printHeader() {
        System.out.printf("%-7s| %-10s| %-22s| %-12s| %s%n",
                "ID", "Club ID", "Player Name", "Position", "Shirt");
        System.out.println("--------------------------------------------------------------");
    }
}
