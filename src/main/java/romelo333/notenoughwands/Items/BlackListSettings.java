package romelo333.notenoughwands.Items;

import mcjty.lib.varia.EntityTools;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import romelo333.notenoughwands.ConfigSetup;

import java.util.HashMap;
import java.util.Map;

public class BlackListSettings {
    protected static Map<String,Double> blacklistedBlocks = new HashMap<>();
    protected static Map<String,Double> blackListedMobs = new HashMap<>();

    public static double getBlacklistCost(Block block) {
        double cost = 1.0f;
        ResourceLocation registryName = block.getRegistryName();
        if (blacklistedBlocks.containsKey(registryName.getResourceDomain() + ":*")) {
            cost = blacklistedBlocks.get(registryName.getResourceDomain() + ":*");
        } else {
            String regName = registryName.toString();
            if (blacklistedBlocks.containsKey(regName)) {
                cost = blacklistedBlocks.get(regName);
            }
        }
        return cost;
    }

    private static void blackListBlock(Configuration cfg, String name) {
        setCostBlock(cfg, name, -1.0);
    }

    private static void setCostBlock(Configuration cfg, String name, double cost) {
        cfg.get(ConfigSetup.CATEGORY_MOVINGBLACKLIST, name, cost);
        blacklistedBlocks.put(name, cost);
    }

    public static double getBlacklistEntity(Entity entity) {
        double cost = 1.0f;
        String name = EntityTools.findEntityIdByClass(entity.getClass());
        if (blackListedMobs.containsKey(name)) {
            cost = blackListedMobs.get(name);
        }
        return cost;
    }

    private static void blackListEntity(Configuration cfg, String name) {
        setCostEntity(cfg, name, -1.0);
    }

    private static void setCostEntity(Configuration cfg, String name, double cost) {
        if (name != null && !name.isEmpty()) {
            cfg.get(ConfigSetup.CATEGORY_CAPTUREBLACKLIST, name, cost);
            blackListedMobs.put(name, cost);
        }
    }

    public static void setupMovingWandBlacklist(Configuration cfg) {
        ConfigCategory category = cfg.getCategory(ConfigSetup.CATEGORY_MOVINGBLACKLIST);
        if (category.isEmpty()) {
            // Initialize with defaults
            blackListBlock(cfg, "rftools:shield_block1");
            blackListBlock(cfg, "rftools:shield_block2");
            blackListBlock(cfg, "rftools:shield_block3");
            blackListBlock(cfg, "rftools:shield_block4");
            blackListBlock(cfg, "rftools:notick_invisible_shield_block");
            blackListBlock(cfg, "rftools:invisible_shield_block");
            blackListBlock(cfg, "rftools:notick_shield_block");
            blackListBlock(cfg, "rftools:shield_block");
            blackListBlock(cfg, "refinedstorage:*");
            blackListBlock(cfg, Blocks.BEDROCK.getRegistryName().toString());
            blackListBlock(cfg, Blocks.PORTAL.getRegistryName().toString());
            blackListBlock(cfg, Blocks.END_PORTAL.getRegistryName().toString());
            setCostBlock(cfg, Blocks.MOB_SPAWNER.getRegistryName().toString(), 5.0);
        } else {
            for (Map.Entry<String, Property> entry : category.entrySet()) {
                blacklistedBlocks.put(entry.getKey(), entry.getValue().getDouble());
            }
        }
    }

    public static void setupCapturingWandBlacklist(Configuration cfg) {
        ConfigCategory category = cfg.getCategory(ConfigSetup.CATEGORY_CAPTUREBLACKLIST);
        if (category.isEmpty()) {
            // Initialize with defaults
            blackListEntity(cfg, EntityTools.findEntityIdByClass(EntityDragon.class));
            setCostEntity(cfg, EntityTools.findEntityIdByClass(EntityWither.class), 2.0);
        } else {
            for (Map.Entry<String, Property> entry : category.entrySet()) {
                blackListedMobs.put(entry.getKey(), entry.getValue().getDouble());
            }
        }
    }
}
