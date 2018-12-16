package romelo333.notenoughwands.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipOptions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.loot.LootPool;
import romelo333.notenoughwands.*;
import romelo333.notenoughwands.mcjtylib.BlockOutlineRenderer;
import romelo333.notenoughwands.varia.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//import net.minecraft.client.entity.EntityClientPlayerMP;

// @todo fabric
//@Optional.Interface(modid = "redstoneflux", iface = "cofh.redstoneflux.api.IEnergyContainerItem")
public class GenericWand extends Item implements IEnergyItem /*, IEnergyContainerItem*/ {
    protected int needsxp = 0;
    protected int needsrf = 0;
    protected int maxrf = 0;

    protected int lootRarity = 10;

    private static List<GenericWand> wands = new ArrayList<>();

    // @todo fabric
//    @Override
//    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
//        return new ItemCapabilityProvider(stack, this);
//    }


    public GenericWand(int maxDurability) {
        super(new Settings()
                .stackSize(1)
                .durability(maxDurability)
                .itemGroup(ItemGroup.TOOLS));
    }

    // Check if a given block can be picked up.
    public static double checkPickup(PlayerEntity player, World world, BlockPos pos, Block block, float maxHardness) {
        BlockState state = world.getBlockState(pos);
        float hardness = block.getHardness(state, world, pos);
        if (hardness < 0 || hardness > maxHardness){
            Tools.error(player, "This block is to hard to take!");
            return -1.0f;
        }
        // @todo fabric
//        if (!block.canEntityDestroy(state, world, pos, player)){
//            Tools.error(player, "You are not allowed to take this block!");
//            return -1.0f;
//        }
        ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
        if (protectedBlocks.isProtected(world, pos)) {
            Tools.error(player, "This block is protected. You cannot take it!");
            return -1.0f;
        }

        double cost = BlackListSettings.getBlacklistCost(block);
        if (cost <= 0.001f) {
            Tools.error(player, "It is illegal to take this block");
            return -1.0f;
        }
        return cost;
    }


    @Override
    public void addInformation(ItemStack stack, World player, List<TextComponent> list, TooltipOptions b) {
        super.addInformation(stack, player, list, b);
        if (needsrf > 0) {
            list.add(new StringTextComponent(TextFormat.GREEN+"Energy: " + getEnergyStored(stack) + " / " + getMaxEnergyStored(stack)));
        }
    }

    // @todo fabric
//    @Override
//    public boolean showDurabilityBar(ItemStack stack) {
//        if (needsrf > 0 && Config.showDurabilityBarForRF) {
//            return true;
//        }
//        return super.showDurabilityBar(stack);
//    }

//    @Override
//    public double getDurabilityForDisplay(ItemStack stack) {
//        if (needsrf > 0 && Config.showDurabilityBarForRF) {
//            int max = getMaxEnergyStored(stack);
//            return (max - getEnergyStored(stack)) / (double) max;
//        }
//        return super.getDurabilityForDisplay(stack);
//    }

    protected GenericWand setup(String name) {
        Registry.ITEM.register(new Identifier(NotEnoughWands.MODID, name), this);
        wands.add(this);
        return this;
    }

    GenericWand xpUsage(int xp) {
        this.needsxp = xp;
        return this;
    }

    GenericWand rfUsage(int maxrf, int rf) {
        this.maxrf = maxrf;
        this.needsrf = rf;
        return this;
    }

    GenericWand loot(int rarity) {
        lootRarity = rarity;
        return this;
    }

    protected String getConfigPrefix() {
        return Registry.ITEM.getId(this).getPath();
    }

    protected void initConfig(Configuration cfg) {

    }

    public void initConfig(Configuration cfg, int easy_usages, int easy_maxrf, int normal_usages, int normal_maxrf, int hard_usages, int hard_maxrf) {
        switch (Config.wandUsage) {
            case DEFAULT:
                needsxp = cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_needsxp", needsxp, "How much levels this wand should consume on usage").getInt();
                needsrf = cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_needsrf", needsrf, "How much RF this wand should consume on usage").getInt();
                maxrf = cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_maxrf", maxrf, "Maximum RF this wand can hold").getInt();
                // @todo fabric
//                setMaxDamage(cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_maxdurability", getMaxDamage(), "Maximum durability for this wand").getInt());
                break;
            case EASY_RF:
                needsxp = 0;
                // @todo fabric
//                setMaxDamage(0);
                needsrf = easy_maxrf / easy_usages;
                maxrf = easy_maxrf;
                cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_needsxp", needsxp, "How much levels this wand should consume on usage").getInt();
                cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_needsrf", needsrf, "How much RF this wand should consume on usage").getInt();
                cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_maxrf", maxrf, "Maximum RF this wand can hold").getInt();
                // @todo fabric
//                cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_maxdurability", getMaxDamage(), "Maximum durability for this wand").getInt();
                break;
            case NORMAL_RF:
                needsxp = 0;
                // @todo fabric
//                setMaxDamage(0);
                needsrf = normal_maxrf / normal_usages;
                maxrf = normal_maxrf;
                cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_needsxp", needsxp, "How much levels this wand should consume on usage").getInt();
                cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_needsrf", needsrf, "How much RF this wand should consume on usage").getInt();
                cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_maxrf", maxrf, "Maximum RF this wand can hold").getInt();
                // @todo fabric
//                cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_maxdurability", getMaxDamage(), "Maximum durability for this wand").getInt();
                break;
            case HARD_RF:
                needsxp = 0;
//                setMaxDamage(0);
                // @todo fabric
                needsrf = hard_maxrf / hard_usages;
                maxrf = hard_maxrf;
                cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_needsxp", needsxp, "How much levels this wand should consume on usage").getInt();
                cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_needsrf", needsrf, "How much RF this wand should consume on usage").getInt();
                cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_maxrf", maxrf, "Maximum RF this wand can hold").getInt();
                // @todo fabric
//                cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_maxdurability", getMaxDamage(), "Maximum durability for this wand").getInt();
                break;
        }

        lootRarity = cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_lootRarity", lootRarity, "How rare should this wand be in chests? Lower is more rare (0 is not in chests)").getInt();
    }

    //------------------------------------------------------------------------------

    protected boolean checkUsage(ItemStack stack, PlayerEntity player, float difficultyScale) {
        if (player.isCreative()) {
            return true;
        }
        if (needsxp > 0) {
            int experience = Tools.getPlayerXP(player) - (int)(needsxp * difficultyScale);
            if (experience <= 0) {
                Tools.error(player, "Not enough experience!");
                return false;
            }
        }
        // @todo fabric
//        if (isDamageable()) {
//            if (stack.getItemDamage() >= stack.getMaxDamage()) {
//                Tools.error(player, "This wand can no longer be used!");
//                return false;
//            }
//        }
        if (needsrf > 0) {
            if (getEnergyStored(stack) < (int)(needsrf * difficultyScale)) {
                Tools.error(player, "Not enough energy to use this wand!");
                return false;
            }
        }
        return true;
    }

    protected void registerUsage(ItemStack stack, PlayerEntity player, float difficultyScale) {
        if (player.isCreative()) {
            return;
        }
        if (needsxp > 0) {
            Tools.addPlayerXP(player, -(int) (needsxp * difficultyScale));
        }
        // @todo fabric
//        if (isDamageable()) {
//            stack.damageItem(1, player);
//        }
        if (needsrf > 0) {
            extractEnergy(stack, (int) (needsrf * difficultyScale), false);
        }
    }

    public void toggleMode(PlayerEntity player, ItemStack stack) {
    }

    public void toggleSubMode(PlayerEntity player, ItemStack stack) {
    }

    //------------------------------------------------------------------------------


    public static List<GenericWand> getWands() {
        return wands;
    }

    //------------------------------------------------------------------------------

    public static void setupConfig(Configuration cfg) {
        for (GenericWand wand : wands) {
            wand.initConfig(cfg);
        }
    }

    //------------------------------------------------------------------------------

    public static void setupChestLoot(LootPool main) {
        for (GenericWand wand : wands) {
            wand.setupChestLootInt(main);
        }
    }

    private void setupChestLootInt(LootPool main) {
        if (lootRarity > 0) {
            String entryName = NotEnoughWands.MODID + ":" + Registry.ITEM.getId(this).getPath();
            // @todo fabric
//            main.addEntry(new LootEntryItem(this, lootRarity, 0, new LootFunction[0], new LootCondition[0], entryName));
        }
    }

    //------------------------------------------------------------------------------

    public void renderOverlay(PlayerEntity player, ItemStack wand, float partialTicks) {

    }

    protected static void renderOutlines(PlayerEntity p, Set<BlockPos> coordinates, int r, int g, int b, float partialTicks) {
        BlockOutlineRenderer.renderOutlines(p, coordinates, r, g, b, partialTicks);
    }

    protected void showModeKeyDescription(List<TextComponent> list, String suffix) {
        String keyDescription = KeyBindings.wandModifier != null ? KeyBindings.wandModifier.getName() : "unknown";
        list.add(new StringTextComponent("Mode key (" + keyDescription + ") to " + suffix));
    }

    protected void showSubModeKeyDescription(List<TextComponent> list, String suffix) {
        String keyDescription = KeyBindings.wandSubMode != null ? KeyBindings.wandSubMode.getName() : "unknown";
        list.add(new StringTextComponent("Sub-mode key (" + keyDescription + ") to " + suffix));
    }

    //------------------------------------------------------------------------------

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        if (maxrf <= 0) {
            return 0;
        }

        if (container.getTag() == null || !container.getTag().containsKey("Energy")) {
            return 0;
        }
        int energy = container.getTag().getInt("Energy");
        int energyExtracted = Math.min(energy, Math.min(this.needsrf, maxExtract));

        if (!simulate) {
            energy -= energyExtracted;
            container.getTag().putInt("Energy", energy);
        }
        return energyExtracted;
    }

    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        if (maxrf <= 0) {
            return 0;
        }

        if (container.getTag() == null) {
            container.setTag(new CompoundTag());
        }
        int energy = container.getTag().getInt("Energy");
        int energyReceived = Math.min(maxrf - energy, Math.min(this.maxrf, maxReceive));

        if (!simulate) {
            energy += energyReceived;
            container.getTag().putInt("Energy", energy);
        }
        return energyReceived;
    }

    @Override
    public int getEnergyStored(ItemStack container) {
        if (container.getTag() == null || !container.getTag().containsKey("Energy")) {
            return 0;
        }
        return container.getTag().getInt("Energy");
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return maxrf;
    }
}
