package model;

public class User {

    private final String username;
    private final String password;
    private final Role role;

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    public boolean can(Permission permission) {
        return role.can(permission);
    }

    public boolean matches(String username, String password) {
        return this.username.equalsIgnoreCase(username) && this.password.equals(password);
    }
}
