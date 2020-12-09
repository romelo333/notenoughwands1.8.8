package romelo333.notenoughwands.modules.wands;

public enum WandUsage {
    NONE,
    DURABILITY,
    XP,
    EASY_RF,
    NORMAL_RF,
    HARD_RF;

    public boolean needsPower() {
        return this == EASY_RF || this == NORMAL_RF || this == HARD_RF;
    }
}
