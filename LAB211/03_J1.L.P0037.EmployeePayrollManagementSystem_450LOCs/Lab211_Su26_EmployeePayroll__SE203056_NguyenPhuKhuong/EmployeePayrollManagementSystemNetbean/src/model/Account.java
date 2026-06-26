package model;

import java.util.Set;

public abstract class Account {

    private String username;
    private String password;

    protected Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public abstract String getRole();

    public abstract Set<Integer> getAllowedFeatures();

    public boolean canAccess(int feature) {
        return getAllowedFeatures().contains(feature);
    }

    public boolean matches(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return username + " (" + getRole() + ")";
    }
}
