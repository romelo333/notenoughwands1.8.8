package romelo333.notenoughwands;


import net.minecraftforge.common.config.Configuration;
import romelo333.notenoughwands.Items.GenericWand;

public class Config {
    public static String CATEGORY_WANDS = "wandsettings";
    public static String CATEGORY_MOVINGBLACKLIST = "movingblacklist";

    public static void init(Configuration cfg) {
        GenericWand.setupConfig(cfg);
    }
}
