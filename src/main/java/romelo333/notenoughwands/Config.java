package romelo333.notenoughwands;


import net.minecraftforge.common.config.Configuration;

public class Config {
    public static String CATEGORY_WANDS = "wands";
    public static String CATEGORY_MOVINGBLACKLIST = "movingblacklist";

    public static void init(Configuration cfg) {
        GenericWand.setupConfig(cfg);
    }
}
