package romelo333.notenoughwands.modules.wands;

import com.google.common.collect.Lists;
import mcjty.lib.varia.Tools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WandsConfiguration {

    public static String CATEGORY_WANDS = "wandsettings";

    public static ModConfigSpec.BooleanValue showDurabilityBarForRF;
    public static WandUsage cachedWandUsage;
    public static ModConfigSpec.EnumValue<WandUsage> wandUsage;

    public static ModConfigSpec.DoubleValue fakePlayerFactor;
    public static ModConfigSpec.BooleanValue lessEffectiveForFakePlayer;

    public static ModConfigSpec.BooleanValue allowPassive;
    public static ModConfigSpec.BooleanValue allowHostile;
    public static ModConfigSpec.DoubleValue difficultyMult;
    public static ModConfigSpec.DoubleValue difficultyAdd;

    public static ModConfigSpec.BooleanValue freezeAllowPassive;
    public static ModConfigSpec.BooleanValue freezeAllowHostile;
    public static ModConfigSpec.DoubleValue freezeDifficultyMult;
    public static ModConfigSpec.DoubleValue freezeDifficultyAdd;

    public static ModConfigSpec.BooleanValue potionAllowPassive;
    public static ModConfigSpec.BooleanValue potionAllowHostile;
    public static ModConfigSpec.DoubleValue potionDifficultyMult;
    public static ModConfigSpec.DoubleValue potionDifficultyAdd;

    public static ModConfigSpec.DoubleValue teleportVolume;
    public static ModConfigSpec.IntValue maxdist;
    public static ModConfigSpec.BooleanValue teleportThroughWalls;

    private static ModConfigSpec.ConfigValue<List<? extends String>> entityBlackList;
    public static Map<ResourceLocation,Double> blacklistedEntities = new HashMap<>();
    private static boolean blacklistedEntitiesLoaded = false;

    public static void init(ModConfigSpec.Builder SERVER_BUILDER, ModConfigSpec.Builder CLIENT_BUILDER) {
        SERVER_BUILDER.comment("Settings for the wands").push(CATEGORY_WANDS);
        CLIENT_BUILDER.comment("Settings for the wands").push(CATEGORY_WANDS);

        SERVER_BUILDER.push("general");
        showDurabilityBarForRF = SERVER_BUILDER
                .comment("Set this to false if you don't want the durability bar for wands using RF")
                .define("showDurabilityBarForRF", true);
        wandUsage = SERVER_BUILDER
                .comment("Set the type of durability consumption for all wands")
                .defineEnum("wandUsage", WandUsage.NORMAL_RF);
        SERVER_BUILDER.pop();

        SERVER_BUILDER.push("teleportation_wand");
        teleportVolume = SERVER_BUILDER
                .comment("Volume of the teleportation sound (set to 0 to disable)")
                .defineInRange("teleportVolume", 1.0, 0.0, 10.0);
        maxdist = SERVER_BUILDER
                .comment("Maximum teleportation distance")
                .defineInRange("maxTeleportationDist", 30, 1, 1000000);
        teleportThroughWalls = SERVER_BUILDER
                .comment("If set to true then sneak-right click will teleport through walls. Otherwise sneak-right click will teleport half distance")
                .define("teleportThroughWalls", true);
        SERVER_BUILDER.pop();

        SERVER_BUILDER.push("acceleration_wand");
        lessEffectiveForFakePlayer = SERVER_BUILDER
                .comment("If true this wand will be less effective for fake players")
                .define("lessEffectiveForFakePlayer", false);
        fakePlayerFactor = SERVER_BUILDER
                .comment("Factor to apply to the cost when this wand is used by a fake player (a machine). Set to -1 to disable its use this way")
                .defineInRange("fakePlayerFactor", 1.0, -1.0, 100000000.0);
        SERVER_BUILDER.pop();

        SERVER_BUILDER.push("capturing_wand");
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
        entityBlackList = SERVER_BUILDER
                .comment("Additional cost factor for capturing entities. -1 to prevent capturing")
                .defineList("entityBlackList", Lists.newArrayList(
                ), o -> true);
        SERVER_BUILDER.pop();

        SERVER_BUILDER.push("freezing_wand");
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
        SERVER_BUILDER.pop();

        SERVER_BUILDER.push("potion_wand");
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
        SERVER_BUILDER.pop();

        SERVER_BUILDER.pop();
        CLIENT_BUILDER.pop();
    }

    public static void reloadConfig() {
        if (blacklistedEntitiesLoaded) {
            return;
        }
        blacklistedEntities.clear();
        for (String s : entityBlackList.get()) {
            String[] split = s.split(",");
            double cost = -1.0f;
            if (split.length > 1) {
                cost = Double.parseDouble(split[1]);
            }
            blacklistedEntities.put(ResourceLocation.parse(split[0]), cost);
        }

    }

    public static double getEntityCost(Entity entity) {
        reloadConfig();
        ResourceLocation registryName = Tools.getId(entity.getType());
        return blacklistedEntities.getOrDefault(registryName, 1.0);
    }
}
