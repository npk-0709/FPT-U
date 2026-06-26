public enum Permission {
    CREATE_REGISTRATION("Create new registration"),
    UPDATE_REGISTRATION("Update registration"),
    VIEW_REGISTRATION("View / search / filter registrations"),
    DELETE_REGISTRATION("Delete registration"),
    VIEW_STATISTICS("View statistics"),
    SAVE_DATA("Save data to file"),
    MANAGE_VOLUNTEER("Manage volunteers"),
    MANAGE_ACCOUNT("Manage accounts");

    private final String description;

    Permission(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
