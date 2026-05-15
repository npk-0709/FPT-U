import java.util.Collections;
import java.util.List;

public class Main {

    private static final Inputter inp = new Inputter();
    private static final Mountains mountains = new Mountains();
    private static final Students students = new Students();

    public static void main(String[] args) {
        if (mountains.isEmpty()) {
            System.out.println("[Warning] Mountain list is empty. Please check MountainList.csv");
        }
        boolean running = true;
        while (running) {
            showMenu();
            int choice = inp.getMenuChoice("Your choice: ", 1, 9);
            switch (choice) {
                case 1: addNewRegistration(); break;
                case 2: updateRegistration(); break;
                case 3: displayRegisteredList(); break;
                case 4: deleteRegistration(); break;
                case 5: searchByName(); break;
                case 6: filterByCampus(); break;
                case 7: showStatistics(); break;
                case 8: saveDataToFile(); break;
                case 9: running = exitProgram(); break;
                default: System.out.println("This function is not available.");
            }
        }
        System.out.println("Goodbye!");
    }

    private static void showMenu() {
        System.out.println();
        System.out.println("============== MOUNTAIN HIKING CHALLENGE REGISTRATION ==============");
        System.out.println("1. New Registration");
        System.out.println("2. Update Registration Information");
        System.out.println("3. Display Registered List");
        System.out.println("4. Delete Registration Information");
        System.out.println("5. Search Participants by Name");
        System.out.println("6. Filter Data by Campus");
        System.out.println("7. Statistics of Registration Numbers by Location");
        System.out.println("8. Save Data to File");
        System.out.println("9. Exit");
        System.out.println("====================================================================");
    }

    private static void addNewRegistration() {
        System.out.println("---- New Registration ----");
        String id;
        while (true) {
            id = inp.inputAndLoop("Student ID [SE/HE/DE/QE/CE + 6 digits]: ",
                    Acceptable.STUDENT_ID).toUpperCase();
            if (students.searchById(id) != null) {
                System.out.println("[Error] Student ID already exists. Please try again.");
            } else {
                break;
            }
        }
        String name = inp.inputAndLoop("Name (2-20 chars): ", Acceptable.NAME_VALID).trim();
        String phone = inp.inputAndLoop("Phone (10 digits, starting with 0): ", Acceptable.PHONE_VALID);
        String email = inp.inputAndLoop("Email: ", Acceptable.EMAIL_VALID);
        mountains.showAll();
        String mountainCode;
        while (true) {
            mountainCode = inp.getString("Mountain code: ").trim();
            if (mountains.isValidMountainCode(mountainCode)) {
                break;
            }
            System.out.println("[Error] Invalid mountain code. Please choose a code from the list.");
        }
        double fee = Student.calculateFee(phone);
        Student newStudent = new Student(id, name, phone, email, mountainCode, fee);
        students.add(newStudent);
        System.out.printf(java.util.Locale.US,
                "Registration added successfully. Tuition fee: %,.0f VND%n", fee);
    }

    private static void updateRegistration() {
        System.out.println("---- Update Registration ----");
        String id = inp.inputAndLoop("Student ID to update: ", Acceptable.STUDENT_ID).toUpperCase();
        Student s = students.searchById(id);
        if (s == null) {
            System.out.println("This student has not registered yet.");
            return;
        }
        System.out.println("Current information:");
        students.showAll(Collections.singletonList(s));
        System.out.println("Press Enter to keep the old value.");

        String name = inp.inputAndLoopAllowEmpty(
                "New name (current: " + s.getName() + "): ", Acceptable.NAME_VALID);
        if (!name.isEmpty()) s.setName(name);

        String phone = inp.inputAndLoopAllowEmpty(
                "New phone (current: " + s.getPhone() + "): ", Acceptable.PHONE_VALID);
        if (!phone.isEmpty()) {
            s.setPhone(phone);
        }

        String email = inp.inputAndLoopAllowEmpty(
                "New email (current: " + s.getEmail() + "): ", Acceptable.EMAIL_VALID);
        if (!email.isEmpty()) s.setEmail(email);

        while (true) {
            String code = inp.getString(
                    "New mountain code (current: " + s.getMountainCode() + ", Enter to keep): ").trim();
            if (code.isEmpty()) break;
            if (mountains.isValidMountainCode(code)) {
                s.setMountainCode(code);
                break;
            }
            System.out.println("[Error] Invalid mountain code.");
        }

        students.markUnsaved();
        System.out.println("Updated successfully.");
        students.showAll(Collections.singletonList(s));
    }

    private static void displayRegisteredList() {
        System.out.println("---- Registered Students ----");
        students.showAll();
    }

    private static void deleteRegistration() {
        System.out.println("---- Delete Registration ----");
        String id = inp.inputAndLoop("Student ID to delete: ", Acceptable.STUDENT_ID).toUpperCase();
        Student s = students.searchById(id);
        if (s == null) {
            System.out.println("This student has not registered yet.");
            return;
        }
        System.out.println("Student details:");
        students.showAll(Collections.singletonList(s));
        boolean ok = inp.confirmYesNo("Are you sure you want to delete this registration? (Y/N): ");
        if (ok) {
            students.delete(id);
            System.out.println("The registration has been successfully deleted.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private static void searchByName() {
        System.out.println("---- Search by Name ----");
        String name = inp.getString("Enter (partial) name to search: ");
        List<Student> result = students.searchByName(name);
        if (result.isEmpty()) {
            System.out.println("No one matches the search criteria!");
        } else {
            System.out.println("Matching Students:");
            students.showAll(result);
        }
    }

    private static void filterByCampus() {
        System.out.println("---- Filter by Campus ----");
        System.out.println("Campus codes: CE - Can Tho | DE - Da Nang | HE - Ha Noi | SE - Ho Chi Minh | QE - Quy Nhon");
        String campus = inp.inputAndLoop("Enter campus code: ", Acceptable.CAMPUS_CODE).toUpperCase();
        List<Student> result = students.filterByCampusCode(campus);
        if (result.isEmpty()) {
            System.out.println("No students have registered under this campus.");
        } else {
            System.out.println("Registered Students Under Campus (" + campus + "):");
            students.showAll(result);
        }
    }

    private static void showStatistics() {
        System.out.println("---- Statistics by Mountain Peak ----");
        students.statisticalizeByMountainPeak(mountains);
    }

    private static void saveDataToFile() {
        System.out.println("---- Save Data to File ----");
        if (students.saveToFile()) {
            System.out.println("Registration data has been successfully saved to `"
                    + students.getPathFile() + "`.");
        }
    }

    private static boolean exitProgram() {
        if (!students.isSaved()) {
            boolean save = inp.confirmYesNo(
                    "You have unsaved changes. Do you want to save before exiting? (Y/N): ");
            if (save) {
                saveDataToFile();
            } else {
                boolean confirm = inp.confirmYesNo(
                        "Are you sure you want to exit without saving? (Y/N): ");
                if (!confirm) {
                    return true;
                }
            }
        }
        return false;
    }
}
