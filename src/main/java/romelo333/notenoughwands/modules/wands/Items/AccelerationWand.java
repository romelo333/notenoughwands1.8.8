package romelo333.notenoughwands.modules.wands.Items;


import mcjty.lib.builder.TooltipBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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

    private Random random = new Random();

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flagIn) {
        super.addInformation(stack, world, list, flagIn);
        tooltipBuilder.makeTooltip(getRegistryName(), stack, list, flagIn);

        showModeKeyDescription(list, "change speed");

        if (Math.abs(WandsConfiguration.fakePlayerFactor.get() -1.0f) >= 0.01) {
            if (WandsConfiguration.fakePlayerFactor.get() < 0) {
                list.add(new StringTextComponent(TextFormatting.RED + "Usage in a machine has been disabled in config!"));
            } else if (WandsConfiguration.fakePlayerFactor.get() > 1) {
                list.add(new StringTextComponent(TextFormatting.YELLOW + "Usage in a machine will cost more!"));
            }
        }
        if (WandsConfiguration.fakePlayerFactor.get() >= 0.0 && WandsConfiguration.lessEffectiveForFakePlayer.get()) {
            list.add(new StringTextComponent(TextFormatting.YELLOW + "Usage in a machine will be less effective!"));
        }
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            int mode = getMode(stack);

            float cost = AccelerationWand.cost[mode];
            int amount = AccelerationWand.amount[mode];

            if (player instanceof FakePlayer) {
                if (WandsConfiguration.fakePlayerFactor.get() < 0) {
                    // Blocked by usage in a machine
                    return ActionResultType.FAIL;
                }
                cost *= WandsConfiguration.fakePlayerFactor.get();

                if (WandsConfiguration.lessEffectiveForFakePlayer.get()) {
                    amount /= 2;
                }
            }

            if (!checkUsage(stack, player, cost)) {
                return ActionResultType.FAIL;
            }
            TileEntity tileEntity = world.getTileEntity(pos);
            for (int i = 0; i < amount /(tileEntity == null ? 5 : 1); i ++){
                if (tileEntity == null){
                    block.tick(state, (ServerWorld) world, pos, random);
                } else if (tileEntity instanceof ITickableTileEntity) {
                    ((ITickableTileEntity)tileEntity).tick();
                }

            }

            registerUsage(stack, player, cost);
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void toggleMode(PlayerEntity player, ItemStack stack) {
        int mode = getMode(stack);
        mode++;
        if (mode > MODE_LAST) {
            mode = MODE_FIRST;
        }
        Tools.notify(player, new StringTextComponent("Switched to " + DESCRIPTIONS[mode] + " mode"));
        stack.getOrCreateTag().putInt("mode", mode);
    }

    private int getMode(ItemStack stack) {
        return stack.getOrCreateTag().getInt("mode");
    }
}
