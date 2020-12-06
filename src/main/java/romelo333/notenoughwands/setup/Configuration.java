package romelo333.notenoughwands.setup;

public interface Configuration {

    IntGetter get(String categoryWands, String s, int needsxp, String s1);
    IntGetter get(String categoryWands, String s, boolean needsxp, String s1);
    IntGetter get(String categoryWands, String s, float needsxp, String s1);

    boolean getBoolean(String s, String categoryWands, boolean teleportThroughWalls, String s1);

    void get(String categoryMovingblacklist, String name, double cost);

    interface IntGetter {
        int getInt();
        double getDouble();
        boolean getBoolean();
    }
}
