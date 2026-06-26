package model;

import java.util.EnumSet;
import java.util.Set;

public enum Role {

    ADMIN(EnumSet.allOf(Permission.class)),

    MANAGER(EnumSet.of(
            Permission.CLUB_VIEW,
            Permission.PLAYER_VIEW,
            Permission.PLAYER_MANAGE,
            Permission.YOUTH_VIEW,
            Permission.YOUTH_MANAGE,
            Permission.DATA_PERSIST
    )),

    VIEWER(EnumSet.of(
            Permission.CLUB_VIEW,
            Permission.PLAYER_VIEW,
            Permission.YOUTH_VIEW
    ));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public boolean can(Permission permission) {
        return permissions.contains(permission);
    }
}
