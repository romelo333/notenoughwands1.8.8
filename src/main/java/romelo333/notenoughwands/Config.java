package romelo333.notenoughwands;


import net.minecraftforge.common.config.Configuration;
import romelo333.notenoughwands.Items.BlackListSettings;
import romelo333.notenoughwands.Items.GenericWand;

public class Config {
    public static String CATEGORY_GENERAL = "general";
    public static String CATEGORY_WANDS = "wandsettings";
    public static String CATEGORY_MOVINGBLACKLIST = "movingblacklist";
    public static String CATEGORY_CAPTUREBLACKLIST = "captureblacklist";

    public static boolean interactionProtection = false;
    public static int clientSideProtection = -1;
    public static boolean showDurabilityBarForRF = true;

    public static void init(Configuration cfg) {
        GenericWand.setupConfig(cfg);
        BlackListSettings.setupCapturingWandBlacklist(cfg);
        BlackListSettings.setupMovingWandBlacklist(cfg);

        interactionProtection = cfg.get(Config.CATEGORY_GENERAL, "interactionProtection", interactionProtection, "If this is true then the protection wand will prevent ALL kind of interaction with protected blocks. If this is false then only block breaking is prevented").getBoolean();
        clientSideProtection = cfg.get(Config.CATEGORY_GENERAL, "clientSideProtection", clientSideProtection, "If this is >= 1 then the protection data will be synced to the client with this frequency (in ticks). This makes protection cleaner at the cost of network traffic").getInt();
        showDurabilityBarForRF = cfg.get(Config.CATEGORY_GENERAL, "showDurabilityBarForRF", showDurabilityBarForRF, "Set this to false if you don't want the durability bar for wands using RF").getBoolean();
    }
}
