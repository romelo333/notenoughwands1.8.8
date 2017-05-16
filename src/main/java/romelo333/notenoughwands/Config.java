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

    public static String wandSettingPreset = "default";
    public static WandUsage wandUsage = WandUsage.DEFAULT;


    public static void init(Configuration cfg) {
        wandSettingPreset = cfg.getString("wandSettingPreset", CATEGORY_GENERAL, wandSettingPreset, "A global setting to control all wands at once for RF/XP/Durability usage. " +
                "If set to 'default' then every wand can configure this on its own (i.e. normal mode). You can also use 'easy_rf', 'normal_rf', or 'hard_rf' to set the wands " +
                "to use RF in various difficulty modes");
        if ("easy_rf".equals(wandSettingPreset) || "easyrf".equals(wandSettingPreset) || "easy".equals(wandSettingPreset)) {
            wandUsage = WandUsage.EASY_RF;
        } else if ("normal_rf".equals(wandSettingPreset) || "normalrf".equals(wandSettingPreset) || "normal".equals(wandSettingPreset) || "rf".equals(wandSettingPreset)) {
            wandUsage = WandUsage.NORMAL_RF;
        } else if ("hard_rf".equals(wandSettingPreset) || "hardrf".equals(wandSettingPreset) || "hard".equals(wandSettingPreset)) {
            wandUsage = WandUsage.HARD_RF;
        } else {
            wandUsage = WandUsage.DEFAULT;
        }

        GenericWand.setupConfig(cfg);
        BlackListSettings.setupCapturingWandBlacklist(cfg);
        BlackListSettings.setupMovingWandBlacklist(cfg);

        interactionProtection = cfg.get(Config.CATEGORY_GENERAL, "interactionProtection", interactionProtection, "If this is true then the protection wand will prevent ALL kind of interaction with protected blocks. If this is false then only block breaking is prevented").getBoolean();
        clientSideProtection = cfg.get(Config.CATEGORY_GENERAL, "clientSideProtection", clientSideProtection, "If this is >= 1 then the protection data will be synced to the client with this frequency (in ticks). This makes protection cleaner at the cost of network traffic").getInt();
        showDurabilityBarForRF = cfg.get(Config.CATEGORY_GENERAL, "showDurabilityBarForRF", showDurabilityBarForRF, "Set this to false if you don't want the durability bar for wands using RF").getBoolean();
    }
}
