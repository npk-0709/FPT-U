import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;

public class Main {

    private static final int MAX_LOGIN_ATTEMPTS = 3;

    private static final Inputter inp = new Inputter();
    private static final Mountains mountains = new Mountains();
    private static final Students students = new Students();
    private static final Volunteers volunteers = new Volunteers();
    private static final Accounts accounts = new Accounts();

    private static Account currentUser;

    public static void main(String[] args) {
        if (!login()) {
            System.out.println("Too many failed login attempts. Goodbye!");
            return;
        }
        if (mountains.isEmpty()) {
            System.out.println("Mountain list is empty !");
        }
        runMenu("============== MOUNTAIN HIKING CHALLENGE REGISTRATION ==============",
                buildMainMenu(), "Exit", Main::confirmExit);
        System.out.println("Goodbye!");
    }

    // ==================== AUTHENTICATION ====================

    private static boolean login() {
        System.out.println("==================== LOGIN ====================");
        for (int attempt = 1; attempt <= MAX_LOGIN_ATTEMPTS; attempt++) {
            String username = inp.getString("Username: ");
            String password = inp.getString("Password: ");
            Account account = accounts.login(username, password);
            if (account != null) {
                currentUser = account;
                System.out.println("Login successful. Welcome, " + account.getName()
                        + " [" + account.getRole() + "].");
                return true;
            }
            System.out.println("Invalid username or password. Attempts left: "
                    + (MAX_LOGIN_ATTEMPTS - attempt));
        }
        return false;
    }

    // ==================== GENERIC MENU ENGINE ====================

    private static void runMenu(String title, List<MenuItem> items,
                                String exitLabel, BooleanSupplier onExit) {
        boolean running = true;
        while (running) {
            List<MenuItem> visible = visibleItems(items);
            printMenu(title, visible, exitLabel);
            int choice = inp.getMenuChoice("Your choice: ", 1, visible.size() + 1);
            if (choice == visible.size() + 1) {
                running = !onExit.getAsBoolean();
            } else {
                visible.get(choice - 1).execute();
            }
        }
    }

    private static List<MenuItem> visibleItems(List<MenuItem> items) {
        List<MenuItem> visible = new ArrayList<>();
        for (MenuItem item : items) {
            if (item.isAllowedFor(currentUser)) {
                visible.add(item);
            }
        }
        return visible;
    }

    private static void printMenu(String title, List<MenuItem> visible, String exitLabel) {
        System.out.println();
        System.out.println(title);
        int index = 1;
        for (MenuItem item : visible) {
            System.out.println(index++ + ". " + item.getLabel());
        }
        System.out.println(index + ". " + exitLabel);
        System.out.println("Logged in as: " + currentUser.getUsername()
                + " [" + currentUser.getRole() + "]");
        System.out.println("====================================================================");
    }

    private static List<MenuItem> buildMainMenu() {
        List<MenuItem> items = new ArrayList<>();
        items.add(new MenuItem("New Registration", Permission.CREATE_REGISTRATION, Main::addNewRegistration));
        items.add(new MenuItem("Update Registration Information", Permission.UPDATE_REGISTRATION, Main::updateRegistration));
        items.add(new MenuItem("Display Registered List", Permission.VIEW_REGISTRATION, Main::displayRegisteredList));
        items.add(new MenuItem("Delete Registration Information", Permission.DELETE_REGISTRATION, Main::deleteRegistration));
        items.add(new MenuItem("Search Participants by Name", Permission.VIEW_REGISTRATION, Main::searchByName));
        items.add(new MenuItem("Filter Data by Campus", Permission.VIEW_REGISTRATION, Main::filterByCampus));
        items.add(new MenuItem("Statistics of Registration Numbers by Location", Permission.VIEW_STATISTICS, Main::showStatistics));
        items.add(new MenuItem("Save Data to File", Permission.SAVE_DATA, Main::saveDataToFile));
        items.add(new MenuItem("Volunteer Management", Permission.MANAGE_VOLUNTEER, Main::volunteerManagement));
        items.add(new MenuItem("Account Management", Permission.MANAGE_ACCOUNT, Main::accountManagement));
        return items;
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
        List<MenuItem> items = new ArrayList<>();
        items.add(new MenuItem("Add New Volunteer", null, Main::addNewVolunteer));
        items.add(new MenuItem("Display Volunteer List", null, Main::displayVolunteerList));
        items.add(new MenuItem("Update Volunteer (Skill / Max Shifts)", null, Main::updateVolunteer));
        items.add(new MenuItem("Assign Volunteer to Shift", null, Main::assignVolunteerToShift));
        items.add(new MenuItem("Delete Volunteer", null, Main::deleteVolunteer));
        runMenu("================ VOLUNTEER MANAGEMENT ================",
                items, "Back to Main Menu", () -> true);
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

        System.out.println("Slot type:");
        System.out.println("1. GENERAL (any skill)");
        System.out.println("2. MEDIC (requires MEDIC skill)");
        int slotChoice = inp.getMenuChoice("Slot type (1-2): ", 1, 2);

        if (slotChoice == 2 && !v.hasSkillFor(Skill.MEDIC)) {
            System.out.println("This volunteer does not have MEDIC skill. Cannot assign to MEDIC slot.");
            return;
        }

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

    // ==================== ACCOUNT FUNCTIONS ====================

    private static void accountManagement() {
        List<MenuItem> items = new ArrayList<>();
        items.add(new MenuItem("Display Account List", null, Main::displayAccounts));
        items.add(new MenuItem("Add New Account", null, Main::addAccount));
        items.add(new MenuItem("Delete Account", null, Main::deleteAccount));
        runMenu("================ ACCOUNT MANAGEMENT ================",
                items, "Back to Main Menu", () -> true);
    }

    private static void displayAccounts() {
        System.out.println("---- Account List ----");
        accounts.showAll();
    }

    private static void addAccount() {
        System.out.println("---- Add New Account ----");
        String username;
        while (true) {
            username = inp.inputAndLoop("Username [3-20 letters/digits/_]: ",
                    Acceptable.USERNAME_VALID).toLowerCase();
            if (accounts.searchByUsername(username) != null) {
                System.out.println("Username already exists. Please try again.");
            } else {
                break;
            }
        }
        String name = inp.inputAndLoop("Display name (2-20 chars): ", Acceptable.NAME_VALID).trim();
        String password = inp.inputAndLoop("Password (6-20 chars, no space): ", Acceptable.PASSWORD_VALID);

        System.out.println("Select role:");
        Role.showAll();
        int roleChoice = inp.getMenuChoice("Role (1-" + Role.values().length + "): ",
                1, Role.values().length);
        Role role = Role.getByIndex(roleChoice);

        Account account = new Account(username, name, password, role);
        if (accounts.add(account)) {
            System.out.println("Account created successfully.");
            System.out.println(account.getDisplayInfo());
        } else {
            System.out.println("Could not create account.");
        }
    }

    private static void deleteAccount() {
        System.out.println("---- Delete Account ----");
        String username = inp.inputAndLoop("Username to delete: ",
                Acceptable.USERNAME_VALID).toLowerCase();
        Account account = accounts.searchByUsername(username);
        if (account == null) {
            System.out.println("Account not found.");
            return;
        }
        if (account.getUsername().equalsIgnoreCase(currentUser.getUsername())) {
            System.out.println("You cannot delete the account you are currently logged in with.");
            return;
        }
        System.out.println("Account details:");
        System.out.println(account.getDisplayInfo());
        boolean ok = inp.confirmYesNo("Are you sure you want to delete this account? (Y/N): ");
        if (ok) {
            accounts.delete(username);
            System.out.println("Account has been successfully deleted.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    // ==================== EXIT ====================

    private static boolean confirmExit() {
        boolean hasUnsaved = !students.isSaved() || !volunteers.isSaved();
        if (!hasUnsaved) {
            return true;
        }
        boolean save = inp.confirmYesNo(
                "You have unsaved changes. Do you want to save before exiting? (Y/N): ");
        if (save) {
            saveDataToFile();
            return true;
        }
        return inp.confirmYesNo("Are you sure you want to exit without saving? (Y/N): ");
    }
}
