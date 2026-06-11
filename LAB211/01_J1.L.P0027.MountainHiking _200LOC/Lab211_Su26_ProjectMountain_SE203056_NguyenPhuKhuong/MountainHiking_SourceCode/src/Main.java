import java.util.Collections;
import java.util.List;

public class Main {

    private static final Inputter inp = new Inputter();
    private static final Mountains mountains = new Mountains();
    private static final Students students = new Students();
    private static final Volunteers volunteers = new Volunteers();

    public static void main(String[] args) {
        if (mountains.isEmpty()) {
            System.out.println("Mountain list is empty !");
        }
        boolean running = true;
        while (running) {
            showMenu();
            int choice = inp.getMenuChoice("Your choice: ", 1, 10);
            switch (choice) {
                case 1:
                    addNewRegistration();
                    break;
                case 2:
                    updateRegistration();
                    break;
                case 3:
                    displayRegisteredList();
                    break;
                case 4:
                    deleteRegistration();
                    break;
                case 5:
                    searchByName();
                    break;
                case 6:
                    filterByCampus();
                    break;
                case 7:
                    showStatistics();
                    break;
                case 8:
                    saveDataToFile();
                    break;
                case 9:
                    volunteerManagement();
                    break;
                case 10:
                    running = exitProgram();
                    break;
                default:
                    System.out.println("This function is not available.");
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
        System.out.println("9. Volunteer Management");
        System.out.println("10. Exit");
        System.out.println("====================================================================");
    }

    // ==================== STUDENT FUNCTIONS ====================

    private static void addNewRegistration() {
        System.out.println("---- New Registration ----");
        String id;
        while (true) {
            id = inp.inputAndLoop("Student ID [SE/HE/DE/QE/CE + 6 digits]: ",
                    Acceptable.STUDENT_ID).toUpperCase();
            if (students.searchById(id) != null) {
                System.out.println("Student ID already exists. Please try again.");
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
            System.out.println("Invalid mountain code. Please choose a code from the list.");
        }
        double fee = Student.calculateFee(phone);
        Student newStudent = new Student(id, name, phone, email, mountainCode, fee);
        if (students.add(newStudent)) {
            System.out.printf(java.util.Locale.US,
                    "Registration added successfully. Tuition fee: %,.0f VND%n", fee);
        } else {
            System.out.println("Could not add registration because the ID already exists.");
        }
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
            System.out.println("Invalid mountain code.");
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
        System.out.println("---- Search Registration ----");
        System.out.println("1. Search by Student ID");
        System.out.println("2. Search by Name");
        int choice = inp.getMenuChoice("Your choice: ", 1, 2);
        if (choice == 1) {
            String id = inp.inputAndLoop("Student ID: ", Acceptable.STUDENT_ID).toUpperCase();
            Student s = students.searchById(id);
            if (s == null) {
                System.out.println("No one matches the search criteria!");
            } else {
                students.showAll(Collections.singletonList(s));
            }
            return;
        }
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
        boolean studentSaved = false;
        boolean volunteerSaved = false;

        if (students.saveToFile()) {
            System.out.println("Registration data has been successfully saved to `"
                    + students.getPathFile() + "`.");
            System.out.println("CSV report has been exported to `"
                    + students.getCsvPathFile() + "`.");
            studentSaved = true;
        }
        if (volunteers.saveToFile()) {
            System.out.println("Volunteer data has been successfully saved to `"
                    + volunteers.getPathFile() + "`.");
            System.out.println("Volunteer CSV has been exported to `"
                    + volunteers.getCsvPathFile() + "`.");
            volunteerSaved = true;
        }
        if (!studentSaved && !volunteerSaved) {
            System.out.println("Could not save data.");
        }
    }

    // ==================== VOLUNTEER FUNCTIONS ====================

    private static void volunteerManagement() {
        boolean running = true;
        while (running) {
            showVolunteerMenu();
            int choice = inp.getMenuChoice("Your choice: ", 1, 6);
            switch (choice) {
                case 1:
                    addNewVolunteer();
                    break;
                case 2:
                    displayVolunteerList();
                    break;
                case 3:
                    updateVolunteer();
                    break;
                case 4:
                    assignVolunteerToShift();
                    break;
                case 5:
                    deleteVolunteer();
                    break;
                case 6:
                    running = false;
                    break;
                default:
                    System.out.println("This function is not available.");
            }
        }
    }

    private static void showVolunteerMenu() {
        System.out.println();
        System.out.println("================ VOLUNTEER MANAGEMENT ================");
        System.out.println("1. Add New Volunteer");
        System.out.println("2. Display Volunteer List");
        System.out.println("3. Update Volunteer (Skill / Max Shifts)");
        System.out.println("4. Assign Volunteer to Shift");
        System.out.println("5. Delete Volunteer");
        System.out.println("6. Back to Main Menu");
        System.out.println("=======================================================");
    }

    private static void addNewVolunteer() {
        System.out.println("---- Add New Volunteer ----");
        String id;
        while (true) {
            id = inp.inputAndLoop("Volunteer ID [VL + 3 digits, e.g. VL001]: ",
                    Acceptable.VOLUNTEER_ID).toUpperCase();
            if (volunteers.searchById(id) != null) {
                System.out.println("Volunteer ID already exists. Please try again.");
            } else {
                break;
            }
        }

        String name = inp.inputAndLoop("Name (3-30 chars): ",
                Acceptable.VOLUNTEER_NAME_VALID).trim();

        System.out.println("Select skill:");
        Skill.showAll();
        int skillChoice = inp.getMenuChoice("Skill (1-" + Skill.values().length + "): ",
                1, Skill.values().length);
        Skill skill = Skill.getByIndex(skillChoice);

        String maxShiftStr = inp.inputAndLoop("Max shifts per day (1-3): ",
                Acceptable.SHIFT_VALID);
        int maxShifts = Integer.parseInt(maxShiftStr);

        Volunteer v = new Volunteer(id, name, skill, maxShifts);
        if (volunteers.add(v)) {
            System.out.println("Volunteer added successfully.");
            System.out.println(v.getDisplayInfo());
        } else {
            System.out.println("Could not add volunteer.");
        }
    }

    private static void displayVolunteerList() {
        System.out.println("---- Volunteer List ----");
        volunteers.showAll();
    }

    private static void updateVolunteer() {
        System.out.println("---- Update Volunteer ----");
        String id = inp.inputAndLoop("Volunteer ID to update: ",
                Acceptable.VOLUNTEER_ID).toUpperCase();
        Volunteer v = volunteers.searchById(id);
        if (v == null) {
            System.out.println("Volunteer not found.");
            return;
        }
        System.out.println("Current information:");
        System.out.println(v.getDisplayInfo());
        System.out.println("Press Enter to keep the old value.");

        // Update skill
        System.out.println("Current skill: " + v.getSkill());
        System.out.println("Select new skill (Enter to keep):");
        Skill.showAll();
        String skillInput = inp.getString("Skill (1-" + Skill.values().length + ", Enter to keep): ").trim();
        if (!skillInput.isEmpty()) {
            try {
                int idx = Integer.parseInt(skillInput);
                Skill newSkill = Skill.getByIndex(idx);
                if (newSkill != null) {
                    v.setSkill(newSkill);
                } else {
                    System.out.println("Invalid choice, keeping old skill.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, keeping old skill.");
            }
        }

        // Update maxShiftsPerDay
        String shiftInput = inp.getString(
                "New max shifts/day (current: " + v.getMaxShiftsPerDay() + ", 1-3, Enter to keep): ").trim();
        if (!shiftInput.isEmpty() && Acceptable.isValid(shiftInput, Acceptable.SHIFT_VALID)) {
            v.setMaxShiftsPerDay(Integer.parseInt(shiftInput));
        } else if (!shiftInput.isEmpty()) {
            System.out.println("Invalid value, keeping old max shifts.");
        }

        volunteers.markUnsaved();
        System.out.println("Updated successfully.");
        System.out.println(v.getDisplayInfo());
    }

    private static void assignVolunteerToShift() {
        System.out.println("---- Assign Volunteer to Shift ----");
        String id = inp.inputAndLoop("Volunteer ID: ",
                Acceptable.VOLUNTEER_ID).toUpperCase();
        Volunteer v = volunteers.searchById(id);
        if (v == null) {
            System.out.println("Volunteer not found.");
            return;
        }
        System.out.println("Volunteer info: " + v.getDisplayInfo());

        // Ask slot type
        System.out.println("Slot type:");
        System.out.println("1. GENERAL (any skill)");
        System.out.println("2. MEDIC (requires MEDIC skill)");
        int slotChoice = inp.getMenuChoice("Slot type (1-2): ", 1, 2);

        if (slotChoice == 2) {
            // MEDIC slot: check skill
            if (!v.hasSkillFor(Skill.MEDIC)) {
                System.out.println("This volunteer does not have MEDIC skill. Cannot assign to MEDIC slot.");
                return;
            }
        }

        // Try assign
        if (v.assign()) {
            volunteers.markUnsaved();
            System.out.println("Assigned successfully! Shifts today: "
                    + v.getShiftsToday() + "/" + v.getMaxShiftsPerDay());
        } else {
            System.out.println("Over shift limit! This volunteer has already reached max shifts ("
                    + v.getMaxShiftsPerDay() + ") for today.");
        }
    }

    private static void deleteVolunteer() {
        System.out.println("---- Delete Volunteer ----");
        String id = inp.inputAndLoop("Volunteer ID to delete: ",
                Acceptable.VOLUNTEER_ID).toUpperCase();
        Volunteer v = volunteers.searchById(id);
        if (v == null) {
            System.out.println("Volunteer not found.");
            return;
        }
        System.out.println("Volunteer details:");
        System.out.println(v.getDisplayInfo());
        boolean ok = inp.confirmYesNo("Are you sure you want to delete this volunteer? (Y/N): ");
        if (ok) {
            volunteers.delete(id);
            System.out.println("Volunteer has been successfully deleted.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    // ==================== EXIT ====================

    private static boolean exitProgram() {
        boolean hasUnsaved = !students.isSaved() || !volunteers.isSaved();
        if (hasUnsaved) {
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
