package romelo333.notenoughwands.modules.wands.Items;


import mcjty.lib.builder.TooltipBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.FakePlayer;
import romelo333.notenoughwands.modules.wands.WandsConfiguration;
import romelo333.notenoughwands.varia.Tools;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static mcjty.lib.builder.TooltipBuilder.*;

public class AccelerationWand extends GenericWand {

    public static final int MODE_FIRST = 0;
    public static final int MODE_20 = 0;
    public static final int MODE_50 = 1;
    public static final int MODE_100 = 2;
    public static final int MODE_LAST = MODE_100;

    public static final String[] DESCRIPTIONS = new String[] {
            "fast", "faster", "fastest"
    };

    private final TooltipBuilder tooltipBuilder = new TooltipBuilder()
            .info(key("message.notenoughwands.shiftmessage"))
            .infoShift(header(), gold(),
                    parameter("mode", stack -> DESCRIPTIONS[getMode(stack)]));


    public static final int[] amount = new int[] { 20, 50, 100};
    public static final float[] cost = new float[] { 1.0f, 2.0f, 5.0f};

    public AccelerationWand() {
        this.usageFactor(3.0f);
    }

    private final Random random = new Random();

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flagIn) {
        super.appendHoverText(stack, world, list, flagIn);
        tooltipBuilder.makeTooltip(getRegistryName(), stack, list, flagIn);

        showModeKeyDescription(list, "change speed");

        if (Math.abs(WandsConfiguration.fakePlayerFactor.get() -1.0f) >= 0.01) {
            if (WandsConfiguration.fakePlayerFactor.get() < 0) {
                list.add(new TextComponent(ChatFormatting.RED + "Usage in a machine has been disabled in config!"));
            } else if (WandsConfiguration.fakePlayerFactor.get() > 1) {
                list.add(new TextComponent(ChatFormatting.YELLOW + "Usage in a machine will cost more!"));
            }
        }
        if (WandsConfiguration.fakePlayerFactor.get() >= 0.0 && WandsConfiguration.lessEffectiveForFakePlayer.get()) {
            list.add(new TextComponent(ChatFormatting.YELLOW + "Usage in a machine will be less effective!"));
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
            int mode = getMode(stack);

            float cost = AccelerationWand.cost[mode];
            int amount = AccelerationWand.amount[mode];

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
                if (tileEntity == null){
                    block.tick(state, (ServerLevel) world, pos, random);
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
        int mode = getMode(stack);
        mode++;
        if (mode > MODE_LAST) {
            mode = MODE_FIRST;
        }
        Tools.notify(player, new TextComponent("Switched to " + DESCRIPTIONS[mode] + " mode"));
        stack.getOrCreateTag().putInt("mode", mode);
    }

    private int getMode(ItemStack stack) {
        return stack.getOrCreateTag().getInt("mode");
    }
}
