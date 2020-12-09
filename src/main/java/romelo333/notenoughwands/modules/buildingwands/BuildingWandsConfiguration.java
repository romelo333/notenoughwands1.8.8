package romelo333.notenoughwands.modules.buildingwands;

import net.minecraftforge.common.ForgeConfigSpec;

public class BuildingWandsConfiguration {

    public static String CATEGORY_BUILDINGWANDS = "buildingwands";
//    public static String CATEGORY_MOVINGBLACKLIST = "movingblacklist";
//    public static String CATEGORY_CAPTUREBLACKLIST = "captureblacklist";

    public static ForgeConfigSpec.DoubleValue maxHardness;
    public static ForgeConfigSpec.IntValue placeDistance;
    public static ForgeConfigSpec.DoubleValue hardnessDistance;

    public static void init(ForgeConfigSpec.Builder SERVER_BUILDER, ForgeConfigSpec.Builder CLIENT_BUILDER) {
        SERVER_BUILDER.comment("Settings for the wands").push(CATEGORY_BUILDINGWANDS);
        CLIENT_BUILDER.comment("Settings for the wands").push(CATEGORY_BUILDINGWANDS);

        maxHardness = SERVER_BUILDER
                .comment("Set the maximum hardness that the displacement and moving wands can move")
                .defineInRange("maxHardness", 50.0, 0.0, 1000000000.0);
        hardnessDistance = SERVER_BUILDER
                .comment("How far away the hardness can be to allow swapping (100 means basically everything allowed)")
                .defineInRange("hardnessDistance", 35.0, 0.0, 100.0);
        placeDistance = SERVER_BUILDER
                .comment("Distance at which to place blocks in 'in-air' mode")
                .defineInRange("maxPlaceDistance", 4, 0, Integer.MAX_VALUE);

        SERVER_BUILDER.pop();
        CLIENT_BUILDER.pop();
    }
}
