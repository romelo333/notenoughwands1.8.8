package romelo333.notenoughwands.modules.wands;

import net.minecraftforge.common.ForgeConfigSpec;
import romelo333.notenoughwands.WandUsage;

public class WandsConfiguration {

    public static String CATEGORY_WANDS = "wandsettings";
//    public static String CATEGORY_MOVINGBLACKLIST = "movingblacklist";
//    public static String CATEGORY_CAPTUREBLACKLIST = "captureblacklist";

    public static ForgeConfigSpec.BooleanValue showDurabilityBarForRF;
    public static ForgeConfigSpec.EnumValue<WandUsage> wandUsage;

    public static void init(ForgeConfigSpec.Builder SERVER_BUILDER, ForgeConfigSpec.Builder CLIENT_BUILDER) {
        SERVER_BUILDER.comment("Settings for the wands").push(CATEGORY_WANDS);
        CLIENT_BUILDER.comment("Settings for the wands").push(CATEGORY_WANDS);

        showDurabilityBarForRF = SERVER_BUILDER
                .comment("Set this to false if you don't want the durability bar for wands using RF")
                .define("showDurabilityBarForRF", true);
        wandUsage = SERVER_BUILDER
                .comment("Set the type of durability consumption for all wands")
                .defineEnum("wandUsage", WandUsage.NORMAL_RF);

        SERVER_BUILDER.pop();
        CLIENT_BUILDER.pop();
    }
}
