package romelo333.notenoughwands.Items;


import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import romelo333.notenoughwands.ModBlocks;

import java.util.List;

public class IlluminationWand extends GenericWand {
    public IlluminationWand() {
        setup("illumination_wand").xpUsage(3).loot(6);
    }

    @Override
    protected void initConfig(Configuration cfg) {
        super.initConfig(cfg, 200, 100000, 100, 200000, 50, 500000);
    }

    @Override
    public void addInformation(ItemStack stack, World player, List list, ITooltipFlag b) {
        super.addInformation(stack, player, list, b);
        list.add("Right click on block to spawn light.");
        list.add("Right click on light to remove it again.");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            Block block = world.getBlockState(pos).getBlock();
            if (block == ModBlocks.lightBlock) {
                BlockSnapshot blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(world, pos);
                world.setBlockToAir(pos);
                if (ForgeEventFactory.onPlayerBlockPlace(player, blocksnapshot, EnumFacing.UP, EnumHand.MAIN_HAND).isCanceled()) {
                    blocksnapshot.restore(true, false);
                }
                return EnumActionResult.SUCCESS;
            }

            BlockPos offset = pos.offset(side);
            if (!world.isAirBlock(offset)) {
                return EnumActionResult.SUCCESS;
            }

            if (!checkUsage(stack, player, 1.0f)) {
                return EnumActionResult.SUCCESS;
            }

            BlockSnapshot blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(world, offset);
            world.setBlockState(offset, ModBlocks.lightBlock.getDefaultState(), 3);
            if (ForgeEventFactory.onPlayerBlockPlace(player, blocksnapshot, EnumFacing.UP, EnumHand.MAIN_HAND).isCanceled()) {
                blocksnapshot.restore(true, false);
            } else {
                registerUsage(stack, player, 1.0f);
            }
        }
        return EnumActionResult.SUCCESS;
    }
}
