package model;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public interface Validatable {

    String CLUB_ID_REGEX          = "^CL-\\d{4}$";
    String PLAYER_ID_REGEX        = "^P\\d{4}$";
    String YOUTH_PLAYER_ID_REGEX  = "^AC-\\d{4}$";

    int YOUTH_MIN_AGE   = 8;
    int YOUTH_MAX_AGE   = 21;
    int FIRST_TEAM_AGE  = 18;

    Set<String> POSITIONS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "goalkeeper", "defender", "midfielder", "forward", "winger"
    )));

    static boolean isValid(String value, String pattern) {
        return value != null && value.matches(pattern);
    }

    static boolean isPosition(String value) {
        return value != null && POSITIONS.contains(value.toLowerCase());
    }
}
