package romelo333.notenoughwands.modules.buildingwands.items;


import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.varia.ComponentFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.EventHooks;
import romelo333.notenoughwands.modules.buildingwands.BuildingWandsConfiguration;
import romelo333.notenoughwands.modules.buildingwands.BuildingWandsModule;
import romelo333.notenoughwands.modules.buildingwands.data.MovingWandData;
import romelo333.notenoughwands.modules.wands.Items.GenericWand;
import romelo333.notenoughwands.varia.Tools;

import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.*;

public class MovingWand extends GenericWand {

    public MovingWand() {
        super();
        this.usageFactor(1.5f);
    }

    private final TooltipBuilder tooltipBuilder = new TooltipBuilder()
            .info(key("message.notenoughwands.shiftmessage"))
            .infoShift(header(), gold());

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, list, flagIn);
        tooltipBuilder.makeTooltip(mcjty.lib.varia.Tools.getId(this), stack, list, flagIn);
        list.add(getBlockDescription(stack));
    }

    private Component getBlockDescription(ItemStack stack) {
        if (!hasBlock(stack)) {
            return ComponentFactory.literal("Wand is empty").withStyle(ChatFormatting.RED);
        } else {
            BlockState state = stack.getOrDefault(BuildingWandsModule.MOVINGWAND_DATA, MovingWandData.DEFAULT).state();
            Component name = Tools.getBlockName(state.getBlock());
            return ComponentFactory.literal("Block: ").append(name).withStyle(ChatFormatting.GREEN);
        }
    }

    private boolean hasBlock(ItemStack stack) {
        return !stack.getOrDefault(BuildingWandsModule.MOVINGWAND_DATA, MovingWandData.DEFAULT).state().isAir();
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            if (hasBlock(stack)) {
                Vec3 lookVec = player.getLookAngle();
                Vec3 start = new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
                int distance = BuildingWandsConfiguration.placeDistance.get();
                Vec3 end = start.add(lookVec.x * distance, lookVec.y * distance, lookVec.z * distance);
                ClipContext context = new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player);
                BlockHitResult position = world.clip(context);
                if (position == null) {
                    place(stack, world, new BlockPos((int) end.x, (int) end.y, (int) end.z), null, player);
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
            if (hasBlock(stack)) {
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
        if (!world.isEmptyBlock(pp) && !old.canBeReplaced()) {
            Tools.error(player, "Something is in the way!");
            return;
        }

        MovingWandData data = stack.getOrDefault(BuildingWandsModule.MOVINGWAND_DATA, MovingWandData.DEFAULT);
        BlockState blockState = data.state();

        BlockSnapshot blocksnapshot = BlockSnapshot.create(world.dimension(), world, pp);

        world.setBlock(pp, blockState, 0);
        // Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        if (EventHooks.onBlockPlace(player, blocksnapshot, Direction.UP)) {
//            blocksnapshot.restore(true, false);   // @todo 1.21 check
            blocksnapshot.restore(0);
            return;
        }

        if (!data.tag().isEmpty()) {
            CompoundTag tc = data.tag();

            //TODO moved pp from setBlockEntity to loadStatic
//            BlockEntity tileEntity = BlockEntity.loadStatic(pp, blockState, tc);
            BlockEntity tileEntity = world.getBlockEntity(pp);
            if (tileEntity != null) {
                tileEntity.loadWithComponents(tc, world.registryAccess());
//                world.getChunk(pp).setBlockEntity(tileEntity);
                tileEntity.setChanged();
                world.sendBlockUpdated(pp, blockState, blockState, Block.UPDATE_ALL);
            }
        }

        stack.set(BuildingWandsModule.MOVINGWAND_DATA, MovingWandData.DEFAULT);
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
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity != null) {
                CompoundTag tc = tileEntity.saveWithoutMetadata(world.registryAccess());
                world.removeBlockEntity(pos);
                stack.set(BuildingWandsModule.MOVINGWAND_DATA, new MovingWandData(state, tc));
            } else {
                stack.set(BuildingWandsModule.MOVINGWAND_DATA, new MovingWandData(state, new CompoundTag()));
            }
            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());

            Tools.notify(player, ComponentFactory.literal("You took: ").append(name));
            registerUsage(stack, player, (float) cost);
        }
    }
}
