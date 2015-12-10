package romelo333.notenoughwands.Items;


import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import romelo333.notenoughwands.varia.Tools;

import java.util.List;
import java.util.Random;

public class AccelerationWand extends GenericWand {

    public static final int MODE_FIRST = 0;
    public static final int MODE_20 = 0;
    public static final int MODE_50 = 1;
    public static final int MODE_100 = 2;
    public static final int MODE_LAST = MODE_100;

    public static final String[] descriptions = new String[] {
            "fast", "faster", "fastest"
    };

    public static final int[] amount = new int[] { 20, 50, 100};
    public static final float[] cost = new float[] { 1.0f, 2.0f, 5.0f};

    public AccelerationWand() {
        setup("acceleration_wand").xpUsage(5).availability(AVAILABILITY_ADVANCED).loot(2);
    }

    private Random random = new Random();

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        super.addInformation(stack, player, list, b);
        list.add(EnumChatFormatting.GREEN + "Mode: " + descriptions[getMode(stack)]);
        list.add("Right click on block to speed up ticks.");
        list.add("Mode key (default '=') to change speed.");
    }


    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            int mode = getMode(stack);

            if (!checkUsage(stack, player, cost[mode])) {
                return true;
            }
            TileEntity tileEntity = world.getTileEntity(pos);
            for (int i = 0; i < amount[mode]/(tileEntity == null ? 5 : 1); i ++){
                if (tileEntity == null){
                    block.updateTick(world, pos, state, random);
                } else if (tileEntity instanceof ITickable) {
                    ((ITickable)tileEntity).update();
                }

            }

            registerUsage(stack, player, cost[mode]);
        }
        return true;
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

    @Override
    protected void setupCraftingInt(Item wandcore) {
        GameRegistry.addRecipe(new ItemStack(this), "gg ", "gw ", "  w", 'g', new ItemStack(Items.dye, 1, 15), 'w', wandcore);
    }

}
