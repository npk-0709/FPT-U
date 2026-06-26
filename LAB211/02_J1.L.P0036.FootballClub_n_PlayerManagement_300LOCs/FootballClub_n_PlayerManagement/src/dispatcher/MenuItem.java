package dispatcher;

import model.Permission;

public class MenuItem {

    private final String label;
    private final Permission permission;
    private final Runnable action;

    public MenuItem(String label, Permission permission, Runnable action) {
        this.label = label;
        this.permission = permission;
        this.action = action;
    }

    public String getLabel() {
        return label;
    }

    public Permission getPermission() {
        return permission;
    }

    public void run() {
        action.run();
    }
}
