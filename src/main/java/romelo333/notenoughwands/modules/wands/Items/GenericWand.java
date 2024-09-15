package romelo333.notenoughwands.modules.wands.Items;

import com.mojang.blaze3d.vertex.PoseStack;
import mcjty.lib.varia.ComponentFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import romelo333.notenoughwands.keys.KeyBindings;
import romelo333.notenoughwands.modules.buildingwands.BuildingWandsConfiguration;
import romelo333.notenoughwands.modules.protectionwand.ProtectedBlocks;
import romelo333.notenoughwands.modules.wands.WandUsage;
import romelo333.notenoughwands.modules.wands.WandsConfiguration;
import romelo333.notenoughwands.setup.Registration;
import romelo333.notenoughwands.varia.ClientTools;
import romelo333.notenoughwands.varia.IEnergyItem;
import romelo333.notenoughwands.varia.ItemCapabilityProvider;
import romelo333.notenoughwands.varia.Tools;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class GenericWand extends Item implements IEnergyItem {

    protected float usageFactor = 1.0f;

    public GenericWand() {
        super(Registration.createStandardProperties()
                .setNoRepair()
                .stacksTo(1)
        );
    }

    // @todo 1.21
//    @Nullable
//    @Override
//    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
//        return new ItemCapabilityProvider(stack, this);
//    }


    // Check if a given block can be picked up.
    public static double checkPickup(Player player, Level world, BlockPos pos, BlockState state, double maxHardness) {
        float hardness = state.getDestroySpeed(world, pos);
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

        double cost = BuildingWandsConfiguration.getBlockCost(state);
        if (cost <= 0.001f) {
            Tools.error(player, "It is illegal to take this block");
            return -1.0f;
        }

        return cost;
    }


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        if (needsPower()) {
            tooltip.add(ComponentFactory.literal("Energy: " + getEnergyStored(stack) + " / " + getMaxEnergyStored(stack)).withStyle(ChatFormatting.GREEN));
        }
    }

    //TODO? getDurabilityBar(ItemStack)
    @Override
    public boolean isBarVisible(ItemStack pStack) {
        if (needsPower() && WandsConfiguration.showDurabilityBarForRF.get()) {
            return true;
        }
        return super.isBarVisible(pStack);
    }

    //TODO
    /*@Override
    public double getDurabilityForDisplay(ItemStack stack) {
        if (needsPower() && WandsConfiguration.showDurabilityBarForRF.get()) {
            int max = getMaxEnergyStored(stack);
            return (max - getEnergyStored(stack)) / (double) max;
        }
        return super.getDurabilityForDisplay(stack);
    }*/

    public GenericWand usageFactor(float usageFactor) {
        this.usageFactor = usageFactor;
        return this;
    }

    //------------------------------------------------------------------------------

    protected boolean checkUsage(ItemStack wandStack, Player player, float difficultyScale) {
        if (player.isCreative()) {
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
        if (needsDamage()) {
            if (wandStack.getDamageValue() >= wandStack.getMaxDamage()) {
                Tools.error(player, "This wand can no longer be used!");
                return false;
            }
        }
        if (needsPower()) {
            int needsrf = calculatePower();
            if (getEnergyStored(wandStack) < (int)(needsrf * difficultyScale)) {
                Tools.error(player, "Not enough energy to use this wand!");
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canBeDepleted() {
        return needsDamage();
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return calculateMaxDamage();
    }

    protected void registerUsage(ItemStack stack, Player player, float difficultyScale) {
        if (player.isCreative()) {
            return;
        }
        if (needsXP()) {
            Tools.addPlayerXP(player, -(int) (calculateXP() * difficultyScale));
        }
        if (needsDamage()) {
            stack.hurtAndBreak(1, player, playerEntity -> {});
        }
        if (needsPower()) {
            extractEnergy(stack, (int) (calculatePower() * difficultyScale), false);
        }
    }

    public void toggleMode(Player player, ItemStack stack) {
    }

    public void toggleSubMode(Player player, ItemStack stack) {
    }

    //------------------------------------------------------------------------------

    public void renderOverlay(RenderLevelStageEvent evt, Player player, ItemStack wand) {

    }

    protected static void renderOutlines(RenderLevelStageEvent evt, Player p, Set<BlockPos> coordinates, int r, int g, int b) {
        PoseStack matrixStack = evt.getPoseStack();
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        ClientTools.renderOutlines(matrixStack, buffer, coordinates, r, g, b);
    }

    protected void showModeKeyDescription(List<Component> list, String suffix) {
        String keyDescription = KeyBindings.wandModifier != null ? KeyBindings.wandModifier.getName() : "unknown";
        list.add(ComponentFactory.literal("Mode key (" + keyDescription + ") to " + suffix).withStyle(ChatFormatting.YELLOW));
    }

    protected void showSubModeKeyDescription(List<Component> list, String suffix) {
        String keyDescription = KeyBindings.wandSubMode != null ? KeyBindings.wandSubMode.getName() : "unknown";
        list.add(ComponentFactory.literal("Sub-mode key (" + keyDescription + ") to " + suffix).withStyle(ChatFormatting.YELLOW));
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
        return (int) (500 * usageFactor);       // @todo 1.15 balance
    }

    private int calculateMaxPower() {
        return (int) (100000 * usageFactor);       // @todo 1.15 balance
    }

    private int calculateMaxDamage() {
        return (int) (200 / usageFactor);       // @todo 1.15 balance
    }

    private int calculateXP() {
        return (int) (10 * usageFactor);        // @todo 1.15 balance
    }

    private boolean needsPower() {
        return WandsConfiguration.cachedWandUsage.needsPower();
    }

    private boolean needsDamage() {
        return WandsConfiguration.cachedWandUsage == WandUsage.DURABILITY;
    }

    private boolean needsXP() {
        return WandsConfiguration.cachedWandUsage == WandUsage.XP;
    }

}
