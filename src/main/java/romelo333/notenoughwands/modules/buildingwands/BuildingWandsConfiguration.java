package romelo333.notenoughwands.modules.buildingwands;

import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildingWandsConfiguration {

    public static String CATEGORY_BUILDINGWANDS = "buildingwands";

    public static ForgeConfigSpec.DoubleValue maxHardness;
    public static ForgeConfigSpec.IntValue placeDistance;
    public static ForgeConfigSpec.DoubleValue hardnessDistance;

    private static ForgeConfigSpec.ConfigValue<List<? extends String>> blockBlackList;
    public static Map<ResourceLocation,Double> blacklistedBlocks = new HashMap<>();

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
        blockBlackList = SERVER_BUILDER
                .comment("Additional cost factor for moving/swapping blocks. -1 to prevent moving/swapping")
                .defineList("blockBlackList", Lists.newArrayList(
                        "rftoolsbuilder:shielding_solid,-1",
                        "rftoolsbuilder:shielding_translucent,-1",
                        "rftoolsbuilder:shielding_cutout,-1",
                        "rftoolsbuilder:shielding,-1",
                        "minecraft:spawner,5",
                        "minecraft:bedrock,-1",
                        "minecraft:nether_portal,-1",
                        "minecraft:end_portal_frame,-1",
                        "minecraft:end_portal,-1"
                ), o -> true);

        SERVER_BUILDER.pop();
        CLIENT_BUILDER.pop();
    }

    public static void reloadConfig() {
        blacklistedBlocks.clear();
        for (String s : blockBlackList.get()) {
            String[] split = s.split(",");
            double cost = -1.0f;
            if (split.length > 1) {
                cost = Double.parseDouble(split[1]);
            }
            blacklistedBlocks.put(new ResourceLocation(s), cost);
        }

    }

    public static double getBlockCost(BlockState state) {
        ResourceLocation registryName = state.getBlock().getRegistryName();
        return blacklistedBlocks.getOrDefault(registryName, 1.0);
    }
}
