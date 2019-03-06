package romelo333.notenoughwands.Items;

import mcjty.lib.client.BlockOutlineRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import romelo333.notenoughwands.Config;
import romelo333.notenoughwands.KeyBindings;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.ProtectedBlocks;
import romelo333.notenoughwands.varia.ItemCapabilityProvider;
import romelo333.notenoughwands.varia.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//import net.minecraft.client.entity.EntityClientPlayerMP;

public class GenericWand extends Item implements IEnergyItem {
    protected int needsxp = 0;
    protected int needsrf = 0;
    protected int maxrf = 0;

    protected int lootRarity = 10;

    private static List<GenericWand> wands = new ArrayList<>();

    @SideOnly(Side.CLIENT)
    public void registerModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
        return new ItemCapabilityProvider(stack, this);
    }


    // Check if a given block can be picked up.
    public static double checkPickup(EntityPlayer player, World world, BlockPos pos, Block block, float maxHardness) {
        IBlockState state = world.getBlockState(pos);
        float hardness = block.getBlockHardness(state, world, pos);
        if (hardness < 0 || hardness > maxHardness){
            Tools.error(player, "This block is to hard to take!");
            return -1.0f;
        }
        if (!block.canEntityDestroy(state, world, pos, player)){
            Tools.error(player, "You are not allowed to take this block!");
            return -1.0f;
        }
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
    public void addInformation(ItemStack stack, World player, List<String> list, ITooltipFlag b) {
        super.addInformation(stack, player, list, b);
        if (needsrf > 0) {
            list.add(TextFormatting.GREEN+"Energy: " + getEnergyStored(stack) + " / " + getMaxEnergyStored(stack));
        }
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        if (needsrf > 0 && Config.showDurabilityBarForRF) {
            return true;
        }
        return super.showDurabilityBar(stack);
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        if (needsrf > 0 && Config.showDurabilityBarForRF) {
            int max = getMaxEnergyStored(stack);
            return (max - getEnergyStored(stack)) / (double) max;
        }
        return super.getDurabilityForDisplay(stack);
    }

    protected GenericWand setup(String name) {
        setMaxStackSize(1);
        setNoRepair();
        setUnlocalizedName(NotEnoughWands.MODID + "." + name);
        setRegistryName(name);
        setCreativeTab(NotEnoughWands.setup.getTab());
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

    GenericWand durabilityUsage(int maxdurability) {
        setMaxDamage(maxdurability);
        return this;
    }

    GenericWand loot(int rarity) {
        lootRarity = rarity;
        return this;
    }

    protected String getConfigPrefix() {
        return getRegistryName().getResourcePath();
    }

    protected void initConfig(Configuration cfg) {

    }

    public void initConfig(Configuration cfg, int easy_usages, int easy_maxrf, int normal_usages, int normal_maxrf, int hard_usages, int hard_maxrf) {
        switch (Config.wandUsage) {
            case DEFAULT:
                needsxp = cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_needsxp", needsxp, "How much levels this wand should consume on usage").getInt();
                needsrf = cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_needsrf", needsrf, "How much RF this wand should consume on usage").getInt();
                maxrf = cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_maxrf", maxrf, "Maximum RF this wand can hold").getInt();
                setMaxDamage(cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_maxdurability", getMaxDamage(), "Maximum durability for this wand").getInt());
                break;
            case EASY_RF:
                needsxp = 0;
                setMaxDamage(0);
                needsrf = easy_maxrf / easy_usages;
                maxrf = easy_maxrf;
                cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_needsxp", needsxp, "How much levels this wand should consume on usage").getInt();
                cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_needsrf", needsrf, "How much RF this wand should consume on usage").getInt();
                cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_maxrf", maxrf, "Maximum RF this wand can hold").getInt();
                cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_maxdurability", getMaxDamage(), "Maximum durability for this wand").getInt();
                break;
            case NORMAL_RF:
                needsxp = 0;
                setMaxDamage(0);
                needsrf = normal_maxrf / normal_usages;
                maxrf = normal_maxrf;
                cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_needsxp", needsxp, "How much levels this wand should consume on usage").getInt();
                cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_needsrf", needsrf, "How much RF this wand should consume on usage").getInt();
                cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_maxrf", maxrf, "Maximum RF this wand can hold").getInt();
                cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_maxdurability", getMaxDamage(), "Maximum durability for this wand").getInt();
                break;
            case HARD_RF:
                needsxp = 0;
                setMaxDamage(0);
                needsrf = hard_maxrf / hard_usages;
                maxrf = hard_maxrf;
                cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_needsxp", needsxp, "How much levels this wand should consume on usage").getInt();
                cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_needsrf", needsrf, "How much RF this wand should consume on usage").getInt();
                cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_maxrf", maxrf, "Maximum RF this wand can hold").getInt();
                cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_maxdurability", getMaxDamage(), "Maximum durability for this wand").getInt();
                break;
        }

        lootRarity = cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_lootRarity", lootRarity, "How rare should this wand be in chests? Lower is more rare (0 is not in chests)").getInt();
    }

    //------------------------------------------------------------------------------

    protected boolean checkUsage(ItemStack stack, EntityPlayer player, float difficultyScale) {
        if (player.capabilities.isCreativeMode) {
            return true;
        }
        if (needsxp > 0) {
            int experience = Tools.getPlayerXP(player) - (int)(needsxp * difficultyScale);
            if (experience <= 0) {
                Tools.error(player, "Not enough experience!");
                return false;
            }
        }
        if (isDamageable()) {
            if (stack.getItemDamage() >= stack.getMaxDamage()) {
                Tools.error(player, "This wand can no longer be used!");
                return false;
            }
        }
        if (needsrf > 0) {
            if (getEnergyStored(stack) < (int)(needsrf * difficultyScale)) {
                Tools.error(player, "Not enough energy to use this wand!");
                return false;
            }
        }
        return true;
    }

    protected void registerUsage(ItemStack stack, EntityPlayer player, float difficultyScale) {
        if (player.capabilities.isCreativeMode) {
            return;
        }
        if (needsxp > 0) {
            Tools.addPlayerXP(player, -(int) (needsxp * difficultyScale));
        }
        if (isDamageable()) {
            stack.damageItem(1, player);
        }
        if (needsrf > 0) {
            extractEnergy(stack, (int) (needsrf * difficultyScale), false);
        }
    }

    public void toggleMode(EntityPlayer player, ItemStack stack) {
    }

    public void toggleSubMode(EntityPlayer player, ItemStack stack) {
    }

    //------------------------------------------------------------------------------


    public static List<GenericWand> getWands() {
        return wands;
    }

    @SideOnly(Side.CLIENT)
    public static void setupModels() {
        for (GenericWand wand : wands) {
            wand.registerModel();
        }
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
            String entryName = NotEnoughWands.MODID + ":" + getRegistryName().getResourcePath();
            main.addEntry(new LootEntryItem(this, lootRarity, 0, new LootFunction[0], new LootCondition[0], entryName));
        }
    }

    //------------------------------------------------------------------------------

    @SideOnly(Side.CLIENT)
    public void renderOverlay(RenderWorldLastEvent evt, EntityPlayerSP player, ItemStack wand) {

    }

    protected static void renderOutlines(RenderWorldLastEvent evt, EntityPlayerSP p, Set<BlockPos> coordinates, int r, int g, int b) {
        BlockOutlineRenderer.renderOutlines(p, coordinates, r, g, b, evt.getPartialTicks());
    }

    protected void showModeKeyDescription(List<String> list, String suffix) {
        String keyDescription = KeyBindings.wandModifier != null ? KeyBindings.wandModifier.getDisplayName() : "unknown";
        list.add("Mode key (" + keyDescription + ") to " + suffix);
    }

    protected void showSubModeKeyDescription(List<String> list, String suffix) {
        String keyDescription = KeyBindings.wandSubMode != null ? KeyBindings.wandSubMode.getDisplayName() : "unknown";
        list.add("Sub-mode key (" + keyDescription + ") to " + suffix);
    }

    //------------------------------------------------------------------------------

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        if (maxrf <= 0) {
            return 0;
        }

        if (container.getTagCompound() == null || !container.getTagCompound().hasKey("Energy")) {
            return 0;
        }
        int energy = container.getTagCompound().getInteger("Energy");
        int energyExtracted = Math.min(energy, Math.min(this.needsrf, maxExtract));

        if (!simulate) {
            energy -= energyExtracted;
            container.getTagCompound().setInteger("Energy", energy);
        }
        return energyExtracted;
    }

    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        if (maxrf <= 0) {
            return 0;
        }

        if (container.getTagCompound() == null) {
            container.setTagCompound(new NBTTagCompound());
        }
        int energy = container.getTagCompound().getInteger("Energy");
        int energyReceived = Math.min(maxrf - energy, Math.min(this.maxrf, maxReceive));

        if (!simulate) {
            energy += energyReceived;
            container.getTagCompound().setInteger("Energy", energy);
        }
        return energyReceived;
    }

    @Override
    public int getEnergyStored(ItemStack container) {
        if (container.getTagCompound() == null || !container.getTagCompound().hasKey("Energy")) {
            return 0;
        }
        return container.getTagCompound().getInteger("Energy");
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return maxrf;
    }
}
