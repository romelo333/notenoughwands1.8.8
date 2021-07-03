package romelo333.notenoughwands.modules.lightwand.items;


import mcjty.lib.builder.TooltipBuilder;
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
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import romelo333.notenoughwands.modules.lightwand.LightModule;
import romelo333.notenoughwands.modules.wands.Items.GenericWand;

import javax.annotation.Nullable;
import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.*;

public class IlluminationWand extends GenericWand {

    public IlluminationWand() {
        this.usageFactor(1);
    }

    private final TooltipBuilder tooltipBuilder = new TooltipBuilder()
            .info(key("message.notenoughwands.shiftmessage"))
            .infoShift(header(), gold());


    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> list, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, list, flagIn);
        tooltipBuilder.makeTooltip(getRegistryName(), stack, list, flagIn);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction side = context.getClickedFace();
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            Block block = world.getBlockState(pos).getBlock();
            if (block == LightModule.LIGHT.get()) {
                BlockSnapshot blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.create(world.dimension(), world, pos);
                world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                if (ForgeEventFactory.onBlockPlace(player, blocksnapshot, Direction.UP)) {
                    blocksnapshot.restore(true, false);
                }
                return ActionResultType.SUCCESS;
            }

            BlockPos offset = pos.relative(side);
            if (!world.isEmptyBlock(offset)) {
                return ActionResultType.SUCCESS;
            }

            if (!checkUsage(stack, player, 1.0f)) {
                return ActionResultType.SUCCESS;
            }

            BlockSnapshot blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.create(world.dimension(), world, offset);
            world.setBlock(offset, LightModule.LIGHT.get().defaultBlockState(), 3);
            if (ForgeEventFactory.onBlockPlace(player, blocksnapshot, Direction.UP)) {
                blocksnapshot.restore(true, false);
            } else {
                registerUsage(stack, player, 1.0f);
            }
        }
        return ActionResultType.SUCCESS;
    }
}
