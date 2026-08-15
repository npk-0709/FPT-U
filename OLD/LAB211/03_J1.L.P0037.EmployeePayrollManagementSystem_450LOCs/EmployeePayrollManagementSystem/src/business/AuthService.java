package business;

import model.Account;
import model.Admin;
import model.Staff;
import tools.Inputter;

import java.util.ArrayList;
import java.util.List;

public class AuthService {

    private static final int MAX_ATTEMPTS = 3;

    private final List<Account> accounts = new ArrayList<>();

    public AuthService() {
        accounts.add(new Admin("admin", "admin123"));
        accounts.add(new Staff("staff", "staff123"));
    }

    public Account login() {
        System.out.println("===== LOGIN =====");
        System.out.println("user&pass of admin: admin&admin123");
        System.out.println("user&pass of staff: staff&staff123");
        int attemptsLeft = MAX_ATTEMPTS;
        while (attemptsLeft > 0) {
            String username = Inputter.inputNonEmpty("Username: ");
            String password = Inputter.inputNonEmpty("Password: ");
            Account account = authenticate(username, password);
            if (account != null) {
                System.out.println("Login successful. Welcome " + account + ".");
                return account;
            }
            attemptsLeft--;
            System.out.println("Invalid username or password. Attempts left: " + attemptsLeft);
        }
        return null;
    }

    private Account authenticate(String username, String password) {
        for (Account account : accounts) {
            if (account.matches(username, password)) {
                return account;
            }
        }
        return null;
    }
}
