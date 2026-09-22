package business;

import java.util.ArrayList;
import java.util.List;

import model.Role;
import model.User;
import tools.Inputter;

public class AuthManager {

    private static final int MAX_ATTEMPTS = 3;

    private final List<User> users = new ArrayList<>();

    public AuthManager() {
        seedDefaultUsers();
    }

    private void seedDefaultUsers() {
        users.add(new User("admin", "admin123", Role.ADMIN));
        users.add(new User("manager", "manager123", Role.MANAGER));
        users.add(new User("viewer", "viewer123", Role.VIEWER));
    }

    public User login() {
        printAccountHints();
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            String username = Inputter.inputNonEmpty("Username: ");
            String password = Inputter.inputNonEmpty("Password: ");
            User user = authenticate(username, password);
            if (user != null) {
                System.out.println("Welcome, " + user.getUsername()
                        + " [" + user.getRole() + "]");
                return user;
            }
            System.out.printf("Invalid credentials. Attempt %d/%d.%n", attempt, MAX_ATTEMPTS);
        }
        return null;
    }

    private User authenticate(String username, String password) {
        for (User user : users) {
            if (user.matches(username, password)) {
                return user;
            }
        }
        return null;
    }

    private void printAccountHints() {
        System.out.println();
        System.out.println("--- Please log in ---");
        System.out.println("Available accounts:");
        System.out.println("  admin   / admin123    (full access)");
        System.out.println("  manager / manager123  (players & youth)");
        System.out.println("  viewer  / viewer123   (read only)");
        System.out.println();
    }
}
