package romelo333.notenoughwands.modules.wands;

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WandsConfiguration {

    public static String CATEGORY_WANDS = "wandsettings";

    public static ForgeConfigSpec.BooleanValue showDurabilityBarForRF;
    public static ForgeConfigSpec.EnumValue<WandUsage> wandUsage;

    public static ForgeConfigSpec.DoubleValue fakePlayerFactor;
    public static ForgeConfigSpec.BooleanValue lessEffectiveForFakePlayer;

    public static ForgeConfigSpec.BooleanValue allowPassive;
    public static ForgeConfigSpec.BooleanValue allowHostile;
    public static ForgeConfigSpec.DoubleValue difficultyMult;
    public static ForgeConfigSpec.DoubleValue difficultyAdd;

    public static ForgeConfigSpec.BooleanValue freezeAllowPassive;
    public static ForgeConfigSpec.BooleanValue freezeAllowHostile;
    public static ForgeConfigSpec.DoubleValue freezeDifficultyMult;
    public static ForgeConfigSpec.DoubleValue freezeDifficultyAdd;

    public static ForgeConfigSpec.BooleanValue potionAllowPassive;
    public static ForgeConfigSpec.BooleanValue potionAllowHostile;
    public static ForgeConfigSpec.DoubleValue potionDifficultyMult;
    public static ForgeConfigSpec.DoubleValue potionDifficultyAdd;

    public static ForgeConfigSpec.DoubleValue teleportVolume;
    public static ForgeConfigSpec.IntValue maxdist;
    public static ForgeConfigSpec.BooleanValue teleportThroughWalls;

    private static ForgeConfigSpec.ConfigValue<List<? extends String>> entityBlackList;
    public static Map<ResourceLocation,Double> blacklistedEntities = new HashMap<>();

    public static void init(ForgeConfigSpec.Builder SERVER_BUILDER, ForgeConfigSpec.Builder CLIENT_BUILDER) {
        SERVER_BUILDER.comment("Settings for the wands").push(CATEGORY_WANDS);
        CLIENT_BUILDER.comment("Settings for the wands").push(CATEGORY_WANDS);

        teleportVolume = SERVER_BUILDER
                .comment("Volume of the teleportation sound (set to 0 to disable)")
                .defineInRange("teleportVolume", 1.0, 0.0, 10.0);
        maxdist = SERVER_BUILDER
                .comment("Maximum teleportation distance")
                .defineInRange("maxTeleportationDist", 30, 1, 1000000);
        teleportThroughWalls = SERVER_BUILDER
                .comment("If set to true then sneak-right click will teleport through walls. Otherwise sneak-right click will teleport half distance")
                .define("teleportThroughWalls", true);

        showDurabilityBarForRF = SERVER_BUILDER
                .comment("Set this to false if you don't want the durability bar for wands using RF")
                .define("showDurabilityBarForRF", true);
        wandUsage = SERVER_BUILDER
                .comment("Set the type of durability consumption for all wands")
                .defineEnum("wandUsage", WandUsage.NORMAL_RF);
        lessEffectiveForFakePlayer = SERVER_BUILDER
                .comment("If true this wand will be less effective for fake players")
                .define("lessEffectiveForFakePlayer", false);
        fakePlayerFactor = SERVER_BUILDER
                .comment("Factor to apply to the cost when this wand is used by a fake player (a machine). Set to -1 to disable its use this way")
                .defineInRange("fakePlayerFactor", 1.0, -1.0, 100000000.0);

        allowPassive = SERVER_BUILDER
                .comment("Allow capturing passive mobs")
                .define("allowPassive", true);
        allowHostile = SERVER_BUILDER
                .comment("Allow capturing hostile mobs")
                .define("allowHostile", true);
        difficultyMult = SERVER_BUILDER
                .comment("Multiply the HP of a mob with this number to get the difficulty scale that affects XP/RF usage (a final result of 1.0 means that the default XP/RF is used)")
                .defineInRange("difficultyMult", 0.0, 0.0, 100000000.0);
        difficultyAdd = SERVER_BUILDER
                .comment("Add this to the HP * difficultyMult to get the final difficulty scale that affects XP/RF usage (a final result of 1.0 means that the default XP/RF is used)")
                .defineInRange("difficultyAdd", 1.0, 0.0, 100000000.0);

        freezeAllowPassive = SERVER_BUILDER
                .comment("Allow freezing passive mobs")
                .define("freezeAllowPassive", true);
        freezeAllowHostile = SERVER_BUILDER
                .comment("Allow freezing hostile mobs")
                .define("freezeAllowHostile", true);
        freezeDifficultyMult = SERVER_BUILDER
                .comment("Multiply the HP of a mob with this number to get the difficulty scale that affects XP/RF usage (a final result of 1.0 means that the default XP/RF is used)")
                .defineInRange("freezeDifficultyMult", 0.0, 0.0, 100000000.0);
        freezeDifficultyAdd = SERVER_BUILDER
                .comment("Add this to the HP * difficultyMult to get the final difficulty scale that affects XP/RF usage (a final result of 1.0 means that the default XP/RF is used)")
                .defineInRange("freezeDifficultyAdd", 1.0, 0.0, 100000000.0);

        potionAllowPassive = SERVER_BUILDER
                .comment("Allow the potion wand to affect passive mobs")
                .define("potionAllowPassive", true);
        potionAllowHostile = SERVER_BUILDER
                .comment("Allow the potion wand to affect hostile mobs")
                .define("potionAllowHostile", true);
        potionDifficultyMult = SERVER_BUILDER
                .comment("Multiply the HP of a mob with this number to get the difficulty scale that affects XP/RF usage (a final result of 1.0 means that the default XP/RF is used)")
                .defineInRange("potionDifficultyMult", 0.0, 0.0, 100000000.0);
        potionDifficultyAdd = SERVER_BUILDER
                .comment("Add this to the HP * difficultyMult to get the final difficulty scale that affects XP/RF usage (a final result of 1.0 means that the default XP/RF is used)")
                .defineInRange("potionDifficultyAdd", 1.0, 0.0, 100000000.0);

        entityBlackList = SERVER_BUILDER
                .comment("Additional cost factor for capturing entities. -1 to prevent capturing")
                .defineList("entityBlackList", Lists.newArrayList(
                ), o -> true);

        SERVER_BUILDER.pop();
        CLIENT_BUILDER.pop();
    }

    public static void reloadConfig() {
        blacklistedEntities.clear();
        for (String s : entityBlackList.get()) {
            String[] split = s.split(",");
            double cost = -1.0f;
            if (split.length > 1) {
                cost = Double.parseDouble(split[1]);
            }
            blacklistedEntities.put(new ResourceLocation(s), cost);
        }

    }

    public static double getEntityCost(Entity entity) {
        ResourceLocation registryName = entity.getType().getRegistryName();
        return blacklistedEntities.getOrDefault(registryName, 1.0);
    }
}
