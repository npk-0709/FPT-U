import java.util.Locale;
import java.util.Objects;

public class Account extends Person {

    private static final long serialVersionUID = 3L;

    private String password;
    private Role role;

    public Account() {
    }

    public Account(String username, String name, String password, Role role) {
        super(username, name);
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public boolean authenticate(String inputPassword) {
        return password != null && password.equals(inputPassword);
    }

    public boolean can(Permission permission) {
        return role != null && role.has(permission);
    }

    @Override
    public String getDisplayInfo() {
        return String.format(Locale.US, "%-15s | %-20s | %-8s", id, name, role);
    }

    @Override
    public String toString() {
        return getDisplayInfo();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Account)) {
            return false;
        }
        Account other = (Account) obj;
        return id != null && other.id != null && id.equalsIgnoreCase(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id == null ? "" : id.toLowerCase());
    }
}
