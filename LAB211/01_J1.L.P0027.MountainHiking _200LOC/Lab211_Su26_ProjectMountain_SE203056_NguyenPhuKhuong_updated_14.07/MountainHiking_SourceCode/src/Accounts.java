import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Accounts extends ArrayList<Account> {

    private final String pathFile;

    public Accounts() {
        this("accounts.dat");
    }

    public Accounts(String pathFile) {
        this.pathFile = pathFile;
        readFromFile();
        if (this.isEmpty()) {
            seedDefaultAccounts();
        }
    }

    private void seedDefaultAccounts() {
        super.add(new Account("admin", "Administrator", "123456", Role.ADMIN));
        super.add(new Account("staff", "Staff Member", "123456", Role.STAFF));
        super.add(new Account("viewer", "Viewer Guest", "123456", Role.VIEWER));
        saveToFile();
    }

    @Override
    public boolean add(Account account) {
        if (account == null || searchByUsername(account.getUsername()) != null) {
            return false;
        }
        boolean ok = super.add(account);
        if (ok) {
            saveToFile();
        }
        return ok;
    }

    public boolean delete(String username) {
        if (username == null) {
            return false;
        }
        for (int i = 0; i < this.size(); i++) {
            if (this.get(i).getUsername().equalsIgnoreCase(username)) {
                this.remove(i);
                saveToFile();
                return true;
            }
        }
        return false;
    }

    public Account searchByUsername(String username) {
        if (username == null) {
            return null;
        }
        for (Account a : this) {
            if (a.getUsername().equalsIgnoreCase(username.trim())) {
                return a;
            }
        }
        return null;
    }

    public Account login(String username, String password) {
        Account account = searchByUsername(username);
        if (account != null && account.authenticate(password)) {
            return account;
        }
        return null;
    }

    public void showAll() {
        if (this.isEmpty()) {
            System.out.println("No accounts available.");
            return;
        }
        List<Account> sortedList = new ArrayList<>(this);
        Collections.sort(sortedList, (a, b) -> a.getUsername().compareToIgnoreCase(b.getUsername()));
        String line = "-------------------------------------------------";
        System.out.println(line);
        System.out.printf("%-15s | %-20s | %-8s%n", "Username", "Name", "Role");
        System.out.println(line);
        for (Account a : sortedList) {
            System.out.println(a.getDisplayInfo());
        }
        System.out.println(line);
    }

    @SuppressWarnings("unchecked")
    public final void readFromFile() {
        File file = new File(pathFile);
        if (!file.exists() || file.length() == 0) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                List<Account> list = (List<Account>) obj;
                this.clear();
                super.addAll(list);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[Warning] Could not load account data: " + e.getMessage());
        }
    }

    public boolean saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(pathFile))) {
            oos.writeObject(new ArrayList<>(this));
            return true;
        } catch (IOException e) {
            System.out.println("Could not save account data: " + e.getMessage());
            return false;
        }
    }

    public String getPathFile() {
        return pathFile;
    }
}
