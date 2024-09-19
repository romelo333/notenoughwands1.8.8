package romelo333.notenoughwands.modules.wands.Items;


import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.varia.ComponentFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.FakePlayer;
import romelo333.notenoughwands.modules.wands.WandsConfiguration;
import romelo333.notenoughwands.modules.wands.WandsModule;
import romelo333.notenoughwands.modules.wands.data.AccelerationWandData;
import romelo333.notenoughwands.varia.Tools;

import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.*;

public class AccelerationWand extends GenericWand {

    private final TooltipBuilder tooltipBuilder = new TooltipBuilder()
            .info(key("message.notenoughwands.shiftmessage"))
            .infoShift(header(), gold(),
                    parameter("mode", stack -> getMode(stack).getDescription()));

    public AccelerationWand() {
        this.usageFactor(3.0f);
    }

    private final RandomSource random = RandomSource.create();

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, list, flagIn);
        tooltipBuilder.makeTooltip(mcjty.lib.varia.Tools.getId(this), stack, list, flagIn);

        showModeKeyDescription(list, "change speed");

        if (Math.abs(WandsConfiguration.fakePlayerFactor.get() -1.0f) >= 0.01) {
            if (WandsConfiguration.fakePlayerFactor.get() < 0) {
                list.add(ComponentFactory.literal(ChatFormatting.RED + "Usage in a machine has been disabled in config!"));
            } else if (WandsConfiguration.fakePlayerFactor.get() > 1) {
                list.add(ComponentFactory.literal(ChatFormatting.YELLOW + "Usage in a machine will cost more!"));
            }
        }
        if (WandsConfiguration.fakePlayerFactor.get() >= 0.0 && WandsConfiguration.lessEffectiveForFakePlayer.get()) {
            list.add(ComponentFactory.literal(ChatFormatting.YELLOW + "Usage in a machine will be less effective!"));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            AccelerationWandData.Mode mode = getMode(stack);

            float cost = mode.getCost();
            int amount = mode.getAmount();

            if (player instanceof FakePlayer) {
                if (WandsConfiguration.fakePlayerFactor.get() < 0) {
                    // Blocked by usage in a machine
                    return InteractionResult.FAIL;
                }
                cost *= WandsConfiguration.fakePlayerFactor.get();

                if (WandsConfiguration.lessEffectiveForFakePlayer.get()) {
                    amount /= 2;
                }
            }

            if (!checkUsage(stack, player, cost)) {
                return InteractionResult.FAIL;
            }
            BlockEntity tileEntity = world.getBlockEntity(pos);
            for (int i = 0; i < amount /(tileEntity == null ? 5 : 1); i ++){
                if (tileEntity == null) {
                    state.tick((ServerLevel) world, pos, random);
                } else if (state.getBlock() instanceof EntityBlock entityBlock) {
                    BlockEntityTicker<BlockEntity> ticker = entityBlock.getTicker(world, state, (BlockEntityType<BlockEntity>) tileEntity.getType());
                    if (ticker != null) {
                        ticker.tick(world, pos, state, tileEntity);
                    }
                }

            }

            registerUsage(stack, player, cost);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void toggleMode(Player player, ItemStack stack) {
        AccelerationWandData.Mode mode = getMode(stack).next();
        Tools.notify(player, ComponentFactory.literal("Switched to " + mode.getDescription() + " mode"));
        stack.update(WandsModule.ACCELERATIONWAND_DATA, AccelerationWandData.DEFAULT, data -> data.withMode(mode));
    }

    private AccelerationWandData.Mode getMode(ItemStack stack) {
        return stack.getOrDefault(WandsModule.ACCELERATIONWAND_DATA, AccelerationWandData.DEFAULT).mode();
    }
}
