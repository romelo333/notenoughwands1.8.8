package romelo333.notenoughwands.Items;


import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import romelo333.notenoughwands.ModBlocks;
import romelo333.notenoughwands.setup.Configuration;

import javax.annotation.Nullable;
import java.util.List;

public class IlluminationWand extends GenericWand {
    public IlluminationWand() {
        setup().xpUsage(3).loot(6);
    }

    @Override
    protected void initConfig(Configuration cfg) {
        super.initConfig(cfg, 200, 100000, 100, 200000, 50, 500000);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> list, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, list, flagIn);
        // @todo 1.15 tooltip
        list.add(new StringTextComponent("Right click on block to spawn light."));
        list.add(new StringTextComponent("Right click on light to remove it again."));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        Direction side = context.getFace();
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            Block block = world.getBlockState(pos).getBlock();
            if (block == ModBlocks.lightBlock) {
                BlockSnapshot blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(world, pos);
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                if (ForgeEventFactory.onBlockPlace(player, blocksnapshot, Direction.UP)) {
                    blocksnapshot.restore(true, false);
                }
                return ActionResultType.SUCCESS;
            }

            BlockPos offset = pos.offset(side);
            if (!world.isAirBlock(offset)) {
                return ActionResultType.SUCCESS;
            }

            if (!checkUsage(stack, player, 1.0f)) {
                return ActionResultType.SUCCESS;
            }

            BlockSnapshot blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(world, offset);
            world.setBlockState(offset, ModBlocks.lightBlock.getDefaultState(), 3);
            if (ForgeEventFactory.onBlockPlace(player, blocksnapshot, Direction.UP)) {
                blocksnapshot.restore(true, false);
            } else {
                registerUsage(stack, player, 1.0f);
            }
        }
        return ActionResultType.SUCCESS;
    }
}
