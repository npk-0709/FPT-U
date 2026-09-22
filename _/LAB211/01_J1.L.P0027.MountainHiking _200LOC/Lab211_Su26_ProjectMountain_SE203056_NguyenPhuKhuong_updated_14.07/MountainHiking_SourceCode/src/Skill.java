public enum Skill {
    MEDIC,
    LOGISTIC,
    GUIDE_ASSIST;

    public static void showAll() {
        Skill[] values = Skill.values();
        for (int i = 0; i < values.length; i++) {
            System.out.println((i + 1) + ". " + values[i].name());
        }
    }
    public static Skill getByIndex(int index) {
        Skill[] values = Skill.values();
        if (index < 1 || index > values.length) {
            return null;
        }
        return values[index - 1];
    }
}
