import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private String fileName;

    public FileManager(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public static void writeToFile(String fileName, String content) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            pw.print(content);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void writeLineToFile(String fileName, String content) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            pw.println(content);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    public static void appendToFile(String fileName, String content) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName, true))) {
            pw.print(content);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void appendLineToFile(String fileName, String content) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName, true))) {
            pw.println(content);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    public static void writeLinesToFile(String fileName, List<String> lines) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            for (String line : lines) {
                pw.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    public static String readFileAsString(String fileName) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Lỗi đọc file: " + e.getMessage());
        }
        return sb.toString();
    }

    /**
     * Đọc file và trả về danh sách các dòng
     */
    public static List<String> readFileAsLines(String fileName) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Lỗi đọc file: " + e.getMessage());
        }
        return lines;
    }

    /**
     * Đọc và in ra màn hình nội dung file
     */
    public static void printFileContent(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Lỗi đọc file: " + e.getMessage());
        }
    }

    // ==================== KIỂM TRA FILE ====================

    /**
     * Kiểm tra file có tồn tại không
     */
    public static boolean isFileExists(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    /**
     * Tạo file mới (nếu chưa tồn tại)
     */
    public static boolean createFile(String fileName) {
        try {
            File file = new File(fileName);
            return file.createNewFile();
        } catch (IOException e) {
            System.out.println("Lỗi tạo file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa file
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        return file.delete();
    }

    /**
     * Xóa toàn bộ nội dung file
     */
    public static void clearFile(String fileName) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            pw.print("");
        } catch (IOException e) {
            System.out.println("Lỗi xóa nội dung file: " + e.getMessage());
        }
    }
}
