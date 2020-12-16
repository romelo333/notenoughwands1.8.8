package romelo333.notenoughwands.modules.wands.Items;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.modules.buildingwands.BlackListSettings;
import romelo333.notenoughwands.modules.protectionwand.ProtectedBlocks;
import romelo333.notenoughwands.modules.wands.WandUsage;
import romelo333.notenoughwands.modules.wands.WandsConfiguration;
import romelo333.notenoughwands.varia.ClientTools;
import romelo333.notenoughwands.varia.IEnergyItem;
import romelo333.notenoughwands.varia.ItemCapabilityProvider;
import romelo333.notenoughwands.varia.Tools;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GenericWand extends Item implements IEnergyItem {

    protected float usageFactor = 1.0f;

    private static List<GenericWand> wands = new ArrayList<>();

    public GenericWand() {
        super(new Item.Properties().group(NotEnoughWands.setup.getTab())
                .setNoRepair()
                .maxStackSize(1)
//                .maxDamage(666) // @todo 1.15
//                .defaultMaxDamage(666) // @todo 1.15
        );
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new ItemCapabilityProvider(stack, this);
    }


    // Check if a given block can be picked up.
    public static double checkPickup(PlayerEntity player, World world, BlockPos pos, BlockState state, double maxHardness) {
        float hardness = state.getBlockHardness(world, pos);
        if (hardness < 0 || hardness > maxHardness){
            Tools.error(player, "This block is to hard to take!");
            return -1.0f;
        }
        if (!state.canEntityDestroy(world, pos, player)){
            Tools.error(player, "You are not allowed to take this block!");
            return -1.0f;
        }
        ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
        if (protectedBlocks.isProtected(world, pos)) {
            Tools.error(player, "This block is protected. You cannot take it!");
            return -1.0f;
        }

        double cost = BlackListSettings.getBlacklistCost(state);
        if (cost <= 0.001f) {
            Tools.error(player, "It is illegal to take this block");
            return -1.0f;
        }

        return cost;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (needsPower()) {
            tooltip.add(new StringTextComponent("Energy: " + getEnergyStored(stack) + " / " + getMaxEnergyStored(stack)).applyTextStyle(TextFormatting.GREEN));
        }
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        if (needsPower() && WandsConfiguration.showDurabilityBarForRF.get()) {
            return true;
        }
        return super.showDurabilityBar(stack);
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        if (needsPower() && WandsConfiguration.showDurabilityBarForRF.get()) {
            int max = getMaxEnergyStored(stack);
            return (max - getEnergyStored(stack)) / (double) max;
        }
        return super.getDurabilityForDisplay(stack);
    }

    public GenericWand setup() {
        // @todo 1.15
        wands.add(this);
        return this;
    }

    public GenericWand usageFactor(float usageFactor) {
        this.usageFactor = usageFactor;
        return this;
    }

//    public void initConfig(Configuration cfg, int easy_usages, int easy_maxrf, int normal_usages, int normal_maxrf, int hard_usages, int hard_maxrf) {
//        switch (WandsConfiguration.wandUsage) {
//            case DEFAULT:
//                needsxp = cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_needsxp", needsxp, "How much levels this wand should consume on usage").getInt();
//                needsrf = cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_needsrf", needsrf, "How much RF this wand should consume on usage").getInt();
//                maxrf = cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_maxrf", maxrf, "Maximum RF this wand can hold").getInt();
//                // @todo 1.15
////                setMaxDamage(cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_maxdurability", getMaxDamage(), "Maximum durability for this wand").getInt());
//                break;
//            case EASY_RF:
//                needsxp = 0;
////                setMaxDamage(0); @todo 1.15
//                needsrf = easy_maxrf / easy_usages;
//                maxrf = easy_maxrf;
//                cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_needsxp", needsxp, "How much levels this wand should consume on usage").getInt();
//                cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_needsrf", needsrf, "How much RF this wand should consume on usage").getInt();
//                cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_maxrf", maxrf, "Maximum RF this wand can hold").getInt();
//                cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_maxdurability", getMaxDamage(), "Maximum durability for this wand").getInt();
//                break;
//            case NORMAL_RF:
//                needsxp = 0;
////                setMaxDamage(0);  @todo 1.15
//                needsrf = normal_maxrf / normal_usages;
//                maxrf = normal_maxrf;
//                cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_needsxp", needsxp, "How much levels this wand should consume on usage").getInt();
//                cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_needsrf", needsrf, "How much RF this wand should consume on usage").getInt();
//                cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_maxrf", maxrf, "Maximum RF this wand can hold").getInt();
//                cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_maxdurability", getMaxDamage(), "Maximum durability for this wand").getInt();
//                break;
//            case HARD_RF:
//                needsxp = 0;
////                setMaxDamage(0);  @todo 1.15
//                needsrf = hard_maxrf / hard_usages;
//                maxrf = hard_maxrf;
//                cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_needsxp", needsxp, "How much levels this wand should consume on usage").getInt();
//                cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_needsrf", needsrf, "How much RF this wand should consume on usage").getInt();
//                cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_maxrf", maxrf, "Maximum RF this wand can hold").getInt();
//                cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_maxdurability", getMaxDamage(), "Maximum durability for this wand").getInt();
//                break;
//        }
//
//        lootRarity = cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_lootRarity", lootRarity, "How rare should this wand be in chests? Lower is more rare (0 is not in chests)").getInt();
//    }

    //------------------------------------------------------------------------------

    protected boolean checkUsage(ItemStack wandStack, PlayerEntity player, float difficultyScale) {
        if (player.abilities.isCreativeMode) {
            return true;
        }
        if (needsXP()) {
            int needsxp = calculateXP();
            int experience = Tools.getPlayerXP(player) - (int)(needsxp * difficultyScale);
            if (experience <= 0) {
                Tools.error(player, "Not enough experience!");
                return false;
            }
        }
//        if (needsDamage()) {
//            if (wandStack.getDamage() >= wandStack.getMaxDamage()) {
//                Tools.error(player, "This wand can no longer be used!");
//                return false;
//            }
//        }
        if (needsPower()) {
            int needsrf = calculatePower();
            if (getEnergyStored(wandStack) < (int)(needsrf * difficultyScale)) {
                Tools.error(player, "Not enough energy to use this wand!");
                return false;
            }
        }
        return true;
    }

    protected void registerUsage(ItemStack stack, PlayerEntity player, float difficultyScale) {
        if (player.abilities.isCreativeMode) {
            return;
        }
        if (needsXP()) {
            Tools.addPlayerXP(player, -(int) (calculateXP() * difficultyScale));
        }
//        if (isDamageable()) {
//            stack.damageItem(1, player, playerEntity -> {});
//        }
        if (needsPower()) {
            extractEnergy(stack, (int) (calculatePower() * difficultyScale), false);
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

    public void renderOverlay(RenderWorldLastEvent evt, PlayerEntity player, ItemStack wand) {

    }

    protected static void renderOutlines(RenderWorldLastEvent evt, PlayerEntity p, Set<BlockPos> coordinates, int r, int g, int b) {
        MatrixStack matrixStack = evt.getMatrixStack();
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        ClientTools.renderOutlines(matrixStack, buffer, coordinates, r, g, b);
//        BlockOutlineRenderer.renderOutlines(p, coordinates, r, g, b, evt.getPartialTicks());
    }

    protected void showModeKeyDescription(List<ITextComponent> list, String suffix) {
        // @todo 1.15
//        String keyDescription = KeyBindings.wandModifier != null ? KeyBindings.wandModifier.getDisplayName() : "unknown";
//        list.add("Mode key (" + keyDescription + ") to " + suffix);
    }

    protected void showSubModeKeyDescription(List<ITextComponent> list, String suffix) {
        // @todo 1.15
//        String keyDescription = KeyBindings.wandSubMode != null ? KeyBindings.wandSubMode.getDisplayName() : "unknown";
//        list.add("Sub-mode key (" + keyDescription + ") to " + suffix);
    }

    //------------------------------------------------------------------------------

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        if (!needsPower()) {
            return 0;
        }

        if (container.getTag() == null || !container.getTag().contains("Energy")) {
            return 0;
        }
        int energy = container.getTag().getInt("Energy");
        int energyExtracted = Math.min(energy, Math.min(calculatePower(), maxExtract));

        if (!simulate) {
            energy -= energyExtracted;
            container.getTag().putInt("Energy", energy);
        }
        return energyExtracted;
    }

    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        if (!needsPower()) {
            return 0;
        }

        container.getOrCreateTag();
        int energy = container.getTag().getInt("Energy");
        int maxrf = calculateMaxPower();
        int energyReceived = Math.min(maxrf - energy, Math.min(maxrf, maxReceive));

        if (!simulate) {
            energy += energyReceived;
            container.getTag().putInt("Energy", energy);
        }
        return energyReceived;
    }

    @Override
    public int getEnergyStored(ItemStack container) {
        if (container.getTag() == null || !container.getTag().contains("Energy")) {
            return 0;
        }
        return container.getTag().getInt("Energy");
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return calculateMaxPower();
    }

    private int calculatePower() {
        return (int) (100 * usageFactor);       // @todo 1.15 balance
    }

    private int calculateMaxPower() {
        return (int) (10000 * usageFactor);       // @todo 1.15 balance
    }

    private int calculateXP() {
        return (int) (10 * usageFactor);        // @todo 1.15 balance
    }

    private boolean needsPower() {
        return WandsConfiguration.wandUsage.get().needsPower();
    }

    private boolean needsDamage() {
        return WandsConfiguration.wandUsage.get() == WandUsage.DURABILITY;
    }

    private boolean needsXP() {
        return WandsConfiguration.wandUsage.get() == WandUsage.XP;
    }

}
