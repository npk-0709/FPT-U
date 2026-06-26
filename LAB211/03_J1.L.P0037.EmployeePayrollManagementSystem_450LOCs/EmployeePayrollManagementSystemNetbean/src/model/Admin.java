package model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Admin extends Account {

    public Admin(String username, String password) {
        super(username, password);
    }

    @Override
    public String getRole() {
        return "Admin";
    }

    @Override
    public Set<Integer> getAllowedFeatures() {
        return new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
    }
}
