package romelo333.notenoughwands.modules.buildingwands.items;


import mcjty.lib.builder.TooltipBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.ClipContext;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import romelo333.notenoughwands.modules.buildingwands.BuildingWandsConfiguration;
import romelo333.notenoughwands.modules.wands.Items.GenericWand;
import romelo333.notenoughwands.varia.Tools;

import static mcjty.lib.builder.TooltipBuilder.*;

public class MovingWand extends GenericWand {

    public MovingWand() {
        this.usageFactor(1.5f);
    }

    private final TooltipBuilder tooltipBuilder = new TooltipBuilder()
            .info(key("message.notenoughwands.shiftmessage"))
            .infoShift(header(), gold());

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flagIn) {
        super.appendHoverText(stack, world, list, flagIn);
        tooltipBuilder.makeTooltip(getRegistryName(), stack, list, flagIn);
        list.add(getBlockDescription(stack));
    }

    private Component getBlockDescription(ItemStack stack) {
        CompoundTag compound = stack.getTag();
        if (!hasBlock(compound)) {
            return new TextComponent("Wand is empty").withStyle(ChatFormatting.RED);
        } else {
            BlockState state = NbtUtils.readBlockState(compound.getCompound("block"));
            Component name = Tools.getBlockName(state.getBlock());
            return new TextComponent("Block: ").append(name).withStyle(ChatFormatting.GREEN);
        }
    }

    private boolean hasBlock(CompoundTag compound) {
        return compound != null && compound.contains("block");
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            CompoundTag compound = stack.getTag();
            if (hasBlock(compound)) {
                Vec3 lookVec = player.getLookAngle();
                Vec3 start = new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
                int distance = BuildingWandsConfiguration.placeDistance.get();
                Vec3 end = start.add(lookVec.x * distance, lookVec.y * distance, lookVec.z * distance);
                ClipContext context = new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null);
                BlockHitResult position = world.clip(context);
                if (position == null) {
                    place(stack, world, new BlockPos(end), null, player);
                }
            }
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction side = context.getClickedFace();
        if (!world.isClientSide) {
            CompoundTag compound = stack.getTag();
            if (hasBlock(compound)) {
                place(stack, world, pos, side, player);
            } else {
                pickup(stack, player, world, pos);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return InteractionResult.SUCCESS;
    }


    private void place(ItemStack stack, Level world, BlockPos pos, Direction side, Player player) {

        BlockPos pp = side == null ? pos : pos.relative(side);

        // First check what's already there
        BlockState old = world.getBlockState(pp);
        if (!world.isEmptyBlock(pp) && !old.getMaterial().isReplaceable()) {//@todo 1.15 check
            Tools.error(player, "Something is in the way!");
            return;
        }

        CompoundTag tagCompound = stack.getOrCreateTag();
        BlockState blockState = NbtUtils.readBlockState(tagCompound.getCompound("block"));

        BlockSnapshot blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.create(world.dimension(), world, pp);

        world.setBlock(pp, blockState, 0);//TODO flags
        // Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        if (ForgeEventFactory.onBlockPlace(player, blocksnapshot, Direction.UP)) {
            blocksnapshot.restore(true, false);
            return;
        }

        if (tagCompound.contains("tedata")) {
            CompoundTag tc = (CompoundTag) tagCompound.get("tedata");
            tc.putInt("x", pp.getX());
            tc.putInt("y", pp.getY());
            tc.putInt("z", pp.getZ());
            //TODO moved pp from setBlockEntity to loadStatic
            BlockEntity tileEntity = BlockEntity.loadStatic(pp, blockState, tc);
            if (tileEntity != null) {
                world.getChunk(pp).setBlockEntity(tileEntity);
                tileEntity.setChanged();
                world.sendBlockUpdated(pp, blockState, blockState, 3);
            }
        }

        tagCompound.remove("block");
        tagCompound.remove("tedata");
        tagCompound.remove("meta");
        stack.setTag(tagCompound);
    }

    private void pickup(ItemStack stack, Player player, Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        double cost = checkPickup(player, world, pos, state, BuildingWandsConfiguration.maxHardness.get());
        if (cost < 0.0) {
            return;
        }

        if (!checkUsage(stack, player, (float) cost)) {
            return;
        }

        CompoundTag tagCompound = stack.getOrCreateTag();
        ItemStack s = state.getBlock().getCloneItemStack(world, pos, state);
        Component name;
        if (s.isEmpty()) {
            name = Tools.getBlockName(state.getBlock());
        } else {
            name = s.getHoverName();
        }
        if (name == null) {
            Tools.error(player, "You cannot select this block!");
        } else {
            tagCompound.put("block", NbtUtils.writeBlockState(state));

            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity != null) {
                CompoundTag tc = new CompoundTag();
                //TODO commented save as it doesn't exist?
                //tileEntity.save(tc);
                world.removeBlockEntity(pos);
                tc.remove("x");
                tc.remove("y");
                tc.remove("z");
                tagCompound.put("tedata", tc);
            }
            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());

            Tools.notify(player, new TextComponent("You took: ").append(name));
            registerUsage(stack, player, (float) cost);
        }
    }
}
