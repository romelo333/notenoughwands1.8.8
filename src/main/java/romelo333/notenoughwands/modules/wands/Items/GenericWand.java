package romelo333.notenoughwands.modules.wands.Items;

import mcjty.lib.varia.ComponentFactory;
import mcjty.lib.varia.IEnergyItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import romelo333.notenoughwands.keys.KeyBindings;
import romelo333.notenoughwands.modules.buildingwands.BuildingWandsConfiguration;
import romelo333.notenoughwands.modules.protectionwand.ProtectedBlocks;
import romelo333.notenoughwands.modules.wands.WandUsage;
import romelo333.notenoughwands.modules.wands.WandsConfiguration;
import romelo333.notenoughwands.setup.Registration;
import romelo333.notenoughwands.varia.ClientTools.RenderContext;
import romelo333.notenoughwands.varia.ClientTools;
import romelo333.notenoughwands.varia.Tools;

import java.util.Set;
import java.util.function.Consumer;

public class GenericWand extends Item implements IEnergyItem {

    protected float usageFactor = 1.0f;

    public GenericWand(Properties properties) {
        super(properties.stacksTo(1));
    }

    // Check if a given block can be picked up.
    public static double checkPickup(Player player, Level world, BlockPos pos, BlockState state, double maxHardness) {
        float hardness = state.getDestroySpeed(world, pos);
        if (hardness < 0 || hardness > maxHardness){
            Tools.error(player, "This block is to hard to take!");
            return -1.0f;
        }
        if (!world.mayInteract(player, pos)){
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
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, display, tooltip, flagIn);
        if (needsPower()) {
            tooltip.accept(ComponentFactory.literal("Energy: " + getEnergyStored(stack) + " / " + getMaxEnergyStored(stack)).withStyle(ChatFormatting.GREEN));
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

    @Override
    public int getBarWidth(ItemStack stack) {
        if (needsPower() && WandsConfiguration.showDurabilityBarForRF.get()) {
            int max = getMaxEnergyStored(stack);
            return Math.round((float)getEnergyStored(stack) * 13.0F / (float)max);
        }
        return super.getBarWidth(stack);
    }

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
            if (wandStack.getDamageValue() >= calculateMaxDamage()) {
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

    // @todo 1.21 Neoforge
//    @Override
//    public boolean canBeDepleted() {
//        return needsDamage();
//    }

    protected void registerUsage(ItemStack stack, Player player, float difficultyScale) {
        if (player.isCreative()) {
            return;
        }
        if (needsXP()) {
            Tools.addPlayerXP(player, -(int) (calculateXP() * difficultyScale));
        }
        if (needsDamage()) {
            stack.set(DataComponents.MAX_DAMAGE, calculateMaxDamage());
            stack.hurtAndBreak(1, (ServerLevel) player.level(), (ServerPlayer) player, item -> {});
        }
        if (needsPower()) {
            int used = (int) (calculatePower() * difficultyScale);
            stack.set(Registration.ENERGY_COMPONENT.get(), Math.max(0, getEnergyStored(stack) - used));
        }
    }

    public void toggleMode(Player player, ItemStack stack) {
    }

    public void toggleSubMode(Player player, ItemStack stack) {
    }

    //------------------------------------------------------------------------------

    public void renderOverlay(RenderContext context, Player player, ItemStack wand) {

    }

    protected static void renderOutlines(RenderContext context, Set<BlockPos> coordinates, int r, int g, int b) {
        ClientTools.renderOutlines(context, coordinates, r, g, b);
    }

    protected void showModeKeyDescription(Consumer<Component> list, String suffix) {
        Component shortcut = KeyBindings.wandModifier == null
                ? Component.translatable("key.keyboard.unknown")
                : KeyBindings.wandModifier.getTranslatedKeyMessage();
        list.accept(ComponentFactory.literal("Mode key (").append(shortcut).append(") to ").append(suffix).withStyle(ChatFormatting.YELLOW));
    }

    protected void showSubModeKeyDescription(Consumer<Component> list, String suffix) {
        Component shortcut = KeyBindings.wandSubMode == null
                ? Component.translatable("key.keyboard.unknown")
                : KeyBindings.wandSubMode.getTranslatedKeyMessage();
        list.accept(ComponentFactory.literal("Sub-mode key (").append(shortcut).append(") to ").append(suffix).withStyle(ChatFormatting.YELLOW));
    }

    //------------------------------------------------------------------------------

    public int getEnergyStored(ItemStack container) {
        return container.getOrDefault(Registration.ENERGY_COMPONENT.get(), 0);
    }

    public int getMaxEnergyStored(ItemStack container) {
        return calculateMaxPower();
    }

    @Override
    public long receiveEnergyL(ItemStack stack, long amount, boolean simulate) {
        long moved = Math.min(amount, Math.max(0, calculateMaxPower() - getEnergyStored(stack)));
        if (moved > 0 && !simulate) {
            stack.set(Registration.ENERGY_COMPONENT.get(), getEnergyStored(stack) + (int) moved);
        }
        return moved;
    }

    @Override
    public long extractEnergyL(ItemStack stack, long amount, boolean simulate) {
        long moved = Math.min(amount, getEnergyStored(stack));
        if (moved > 0 && !simulate) {
            stack.set(Registration.ENERGY_COMPONENT.get(), getEnergyStored(stack) - (int) moved);
        }
        return moved;
    }

    @Override
    public long getEnergyStoredL(ItemStack stack) {
        return getEnergyStored(stack);
    }

    @Override
    public long getMaxEnergyStoredL(ItemStack stack) {
        return calculateMaxPower();
    }

    private int calculatePower() {
        return (int) (500 * usageFactor);       // @todo 1.15 balance
    }

    public int calculateMaxPower() {
        return (int) (100000 * usageFactor);       // @todo 1.15 balance
    }

    private int calculateMaxDamage() {
        return (int) (200 / usageFactor);       // @todo 1.15 balance
    }

    private int calculateXP() {
        return (int) (10 * usageFactor);        // @todo 1.15 balance
    }

    private boolean needsPower() {
        return WandsConfiguration.getWandUsage().needsPower();
    }

    private boolean needsDamage() {
        return WandsConfiguration.getWandUsage() == WandUsage.DURABILITY;
    }

    private boolean needsXP() {
        return WandsConfiguration.getWandUsage() == WandUsage.XP;
    }

}
