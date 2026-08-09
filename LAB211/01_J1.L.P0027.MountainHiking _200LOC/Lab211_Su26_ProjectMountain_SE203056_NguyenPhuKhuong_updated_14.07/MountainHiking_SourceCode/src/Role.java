import java.util.EnumSet;
import java.util.Set;

public enum Role {

    ADMIN(EnumSet.allOf(Permission.class)),

    STAFF(EnumSet.of(
            Permission.CREATE_REGISTRATION,
            Permission.UPDATE_REGISTRATION,
            Permission.VIEW_REGISTRATION,
            Permission.VIEW_STATISTICS,
            Permission.SAVE_DATA,
            Permission.MANAGE_VOLUNTEER)),

    VIEWER(EnumSet.of(
            Permission.VIEW_REGISTRATION,
            Permission.VIEW_STATISTICS));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public boolean has(Permission permission) {
        return permission != null && permissions.contains(permission);
    }

    public static Role getByIndex(int index) {
        Role[] values = Role.values();
        if (index < 1 || index > values.length) {
            return null;
        }
        return values[index - 1];
    }

    public static void showAll() {
        Role[] values = Role.values();
        for (int i = 0; i < values.length; i++) {
            System.out.println((i + 1) + ". " + values[i].name());
        }
    }
}
