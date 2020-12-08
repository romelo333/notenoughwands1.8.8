package romelo333.notenoughwands.modules.protectionwand;

import net.minecraftforge.common.ForgeConfigSpec;

public class ProtectionWandConfiguration {

    public static String CATEGORY_PROTECTION_WAND = "protectionwand";

    public static ForgeConfigSpec.BooleanValue interactionProtection;
    public static ForgeConfigSpec.IntValue clientSideProtection;

    public static void init(ForgeConfigSpec.Builder SERVER_BUILDER, ForgeConfigSpec.Builder CLIENT_BUILDER) {
        SERVER_BUILDER.comment("Settings for the wands").push(CATEGORY_PROTECTION_WAND);
        CLIENT_BUILDER.comment("Settings for the wands").push(CATEGORY_PROTECTION_WAND);

        clientSideProtection = SERVER_BUILDER
                .comment("If this is >= 1 then the protection data will be synced to the client with this frequency (in ticks). This makes protection cleaner at the cost of network traffic")
                .defineInRange("clientSideProtection", -1, 0, Integer.MAX_VALUE);
        interactionProtection = SERVER_BUILDER
                .comment("If this is true then the protection wand will prevent ALL kind of interaction with protected blocks. If this is false then only block breaking is prevented")
                .define("interactionProtection", false);

        SERVER_BUILDER.pop();
        CLIENT_BUILDER.pop();
    }
}
