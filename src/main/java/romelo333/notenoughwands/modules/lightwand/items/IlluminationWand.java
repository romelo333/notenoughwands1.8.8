package romelo333.notenoughwands.modules.lightwand.items;


import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.varia.Tools;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.ForgeEventFactory;
import romelo333.notenoughwands.modules.lightwand.LightModule;
import romelo333.notenoughwands.modules.wands.Items.GenericWand;

import javax.annotation.Nullable;
import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.*;

public class IlluminationWand extends GenericWand {

    public IlluminationWand() {
        super();
        this.usageFactor(1);
    }

    private final TooltipBuilder tooltipBuilder = new TooltipBuilder()
            .info(key("message.notenoughwands.shiftmessage"))
            .infoShift(header(), gold());


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> list, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, list, flagIn);
        tooltipBuilder.makeTooltip(Tools.getId(this), stack, list, flagIn);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        Level world = context.getLevel();
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
                return InteractionResult.SUCCESS;
            }

            BlockPos offset = pos.relative(side);
            if (!world.isEmptyBlock(offset)) {
                return InteractionResult.SUCCESS;
            }

            if (!checkUsage(stack, player, 1.0f)) {
                return InteractionResult.SUCCESS;
            }

            BlockSnapshot blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.create(world.dimension(), world, offset);
            world.setBlock(offset, LightModule.LIGHT.get().defaultBlockState(), 3);
            if (ForgeEventFactory.onBlockPlace(player, blocksnapshot, Direction.UP)) {
                blocksnapshot.restore(true, false);
            } else {
                registerUsage(stack, player, 1.0f);
            }
        }
        return InteractionResult.SUCCESS;
    }
}
