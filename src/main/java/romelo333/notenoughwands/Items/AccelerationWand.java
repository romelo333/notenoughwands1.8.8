package romelo333.notenoughwands.Items;


import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.FakePlayer;
import romelo333.notenoughwands.Config;
import romelo333.notenoughwands.varia.Tools;

import java.util.List;
import java.util.Random;

public class AccelerationWand extends GenericWand {

    public static final int MODE_FIRST = 0;
    public static final int MODE_20 = 0;
    public static final int MODE_50 = 1;
    public static final int MODE_100 = 2;
    public static final int MODE_LAST = MODE_100;

    private float fakePlayerFactor = 1.0f;
    private boolean lessEffectiveForFakePlayer = false;

    public static final String[] descriptions = new String[] {
            "fast", "faster", "fastest"
    };

    public static final int[] amount = new int[] { 20, 50, 100};
    public static final float[] cost = new float[] { 1.0f, 2.0f, 5.0f};

    public AccelerationWand() {
        setup("acceleration_wand").xpUsage(5).loot(2);
    }

    private Random random = new Random();

    @Override
    public void addInformation(ItemStack stack, World player, List list, ITooltipFlag b) {
        super.addInformation(stack, player, list, b);
        list.add(TextFormatting.GREEN + "Mode: " + descriptions[getMode(stack)]);
        list.add("Right click on block to speed up ticks.");
        showModeKeyDescription(list, "change speed");
        if (Math.abs(fakePlayerFactor-1.0f) >= 0.01) {
            if (fakePlayerFactor < 0) {
                list.add(TextFormatting.RED + "Usage in a machine has been disabled in config!");
            } else if (fakePlayerFactor > 1) {
                list.add(TextFormatting.YELLOW + "Usage in a machine will cost more!");
            }
        }
        if (fakePlayerFactor >= 0.0 && lessEffectiveForFakePlayer) {
            list.add(TextFormatting.YELLOW + "Usage in a machine will be less effective!");
        }
    }

    @Override
    public void initConfig(Configuration cfg) {
        super.initConfig(cfg, 500, 100000, 200, 200000, 100, 500000);
        fakePlayerFactor = (float) cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_fakePlayerFactor", fakePlayerFactor,
                "Factor to apply to the cost when this wand is used by a fake player (a machine). Set to -1 to disable its use this way").getDouble();
        lessEffectiveForFakePlayer =  cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_lessEffectiveForFakePlayer", lessEffectiveForFakePlayer,
                "If true this wand will be less effective for fake players").getBoolean();
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            int mode = getMode(stack);

            float cost = AccelerationWand.cost[mode];
            int amount = AccelerationWand.amount[mode];

            if (player instanceof FakePlayer) {
                if (fakePlayerFactor < 0) {
                    // Blocked by usage in a machine
                    return EnumActionResult.FAIL;
                }
                cost *= fakePlayerFactor;

                if (lessEffectiveForFakePlayer) {
                    amount /= 2;
                }
            }

            if (!checkUsage(stack, player, cost)) {
                return EnumActionResult.FAIL;
            }
            TileEntity tileEntity = world.getTileEntity(pos);
            for (int i = 0; i < amount /(tileEntity == null ? 5 : 1); i ++){
                if (tileEntity == null){
                    block.updateTick(world, pos, state, random);
                } else if (tileEntity instanceof ITickable) {
                    ((ITickable)tileEntity).update();
                }

            }

            registerUsage(stack, player, cost);
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public void toggleMode(EntityPlayer player, ItemStack stack) {
        int mode = getMode(stack);
        mode++;
        if (mode > MODE_LAST) {
            mode = MODE_FIRST;
        }
        Tools.notify(player, "Switched to " + descriptions[mode] + " mode");
        Tools.getTagCompound(stack).setInteger("mode", mode);
    }

    private int getMode(ItemStack stack) {
        return Tools.getTagCompound(stack).getInteger("mode");
    }
}
