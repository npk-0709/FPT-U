package model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Staff extends Account {

    public Staff(String username, String password) {
        super(username, password);
    }

    @Override
    public String getRole() {
        return "Staff";
    }

    @Override
    public Set<Integer> getAllowedFeatures() {
        return new HashSet<>(Arrays.asList(1, 5, 6, 7, 9, 10));
    }
}
