package romelo333.notenoughwands.modules.buildingwands.items;


import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.varia.ComponentFactory;
import mcjty.lib.varia.SoundTools;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import romelo333.notenoughwands.modules.buildingwands.BuildingWandsModule;
import romelo333.notenoughwands.modules.buildingwands.data.BuildingWandData;
import romelo333.notenoughwands.modules.wands.Items.GenericWand;
import romelo333.notenoughwands.varia.ClientTools;
import romelo333.notenoughwands.varia.Tools;

import java.util.*;

import static mcjty.lib.builder.TooltipBuilder.*;
import static romelo333.notenoughwands.modules.buildingwands.data.BuildingWandData.Mode.MODE_25ROW;
import static romelo333.notenoughwands.modules.buildingwands.data.BuildingWandData.Mode.MODE_9ROW;

public class BuildingWand extends GenericWand {

    private final TooltipBuilder tooltipBuilder = new TooltipBuilder()
            .info(key("message.notenoughwands.shiftmessage"))
            .infoShift(header(), gold(),
                    parameter("undo", stack -> countUndoStates(stack) > 0, stack -> Integer.toString(countUndoStates(stack))),
                    parameter("mode", stack -> getMode(stack).getDescription()),
                    parameter("submode", stack -> getSubMode(stack) == BuildingWandData.OrientationMode.ROTATED, stack -> getSubMode(stack).getDescription()));

    public BuildingWand() {
        super();
        this.usageFactor(1.0f);
    }

    private int countUndoStates(ItemStack stack) {
        return stack.getOrDefault(BuildingWandsModule.BUILDINGWAND_DATA, BuildingWandData.DEFAULT).undoStates().size();
    }


    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> list, TooltipFlag flags) {
        super.appendHoverText(itemStack, context, list, flags);
        tooltipBuilder.makeTooltip(mcjty.lib.varia.Tools.getId(this), itemStack, list, flags);

        showModeKeyDescription(list, "switch mode");
        showSubModeKeyDescription(list, "change orientation");
    }

    @Override
    public void toggleMode(Player player, ItemStack stack) {
        BuildingWandData.Mode mode = getMode(stack).next();
        Tools.notify(player, ComponentFactory.literal("Switched to " + mode.getDescription() + " mode"));
        stack.update(BuildingWandsModule.BUILDINGWAND_DATA, BuildingWandData.DEFAULT, data -> data.withMode(mode));
    }

    @Override
    public void toggleSubMode(Player player, ItemStack stack) {
        BuildingWandData.OrientationMode subMode = getSubMode(stack).next();
        stack.update(BuildingWandsModule.BUILDINGWAND_DATA, BuildingWandData.DEFAULT, data -> data.withOrientationMode(subMode));
    }

    private BuildingWandData.Mode getMode(ItemStack stack) {
        return stack.getOrDefault(BuildingWandsModule.BUILDINGWAND_DATA, BuildingWandData.DEFAULT).mode();
    }

    private BuildingWandData.OrientationMode getSubMode(ItemStack stack) {
        return stack.getOrDefault(BuildingWandsModule.BUILDINGWAND_DATA, BuildingWandData.DEFAULT).orientationMode();
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction side = context.getClickedFace();
        ItemStack wandStack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            if (player.isShiftKeyDown()) {
                undoPlaceBlock(wandStack, player, world, pos);
            } else {
                placeBlock(wandStack, player, world, pos, side);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private void placeBlock(ItemStack wandStack, Player player, Level world, BlockPos pos, Direction side) {
        if (!checkUsage(wandStack, player, 1.0f)) {
            return;
        }
        boolean notenough = false;
        BlockState blockState = world.getBlockState(pos);

        Set<BlockPos> coordinates = findSuitableBlocks(wandStack, world, side, pos, blockState);
        Set<BlockPos> undo = new HashSet<>();
        for (BlockPos coordinate : coordinates) {
            if (!checkUsage(wandStack, player, 1.0f)) {
                break;
            }
            ItemStack pickBlock = Tools.pickBlock(blockState, world, coordinate.relative(side.getOpposite()), player);
            ItemStack consumed = Tools.consumeInventoryItem(pickBlock, player.getInventory(), player);
            if (!consumed.isEmpty()) {
                SoundTools.playSound(world, blockState.getSoundType().getStepSound(), coordinate.getX(), coordinate.getY(), coordinate.getZ(), 1.0f, 1.0f);
                //                IBlockState state = block.getStateFromMeta(meta);
//                world.setBlockState(coordinate, state, 2);
                BlockSnapshot blocksnapshot = BlockSnapshot.create(world.dimension(), world, coordinate);
                Tools.placeStackAt(player, consumed, world, coordinate, null);
                if (EventHooks.onBlockPlace(player, blocksnapshot, Direction.UP)) {
//                    blocksnapshot.restore(true, false);
                    blocksnapshot.restore(0);   // @todo 1.21 check this
                    if (!player.isCreative()) {
                        Tools.giveItem(player, consumed);
                    }
                }
                player.containerMenu.broadcastChanges();
                registerUsage(wandStack, player, 1.0f);
                undo.add(coordinate);
            } else {
                notenough = true;
            }
        }
        if (notenough) {
            Tools.error(player, "You don't have the right block");
        }

        registerUndo(wandStack, blockState, world, undo);
    }

    private void registerUndo(ItemStack stack, BlockState state, Level world, Set<BlockPos> undo) {
        BuildingWandData.UndoState undoState = new BuildingWandData.UndoState(world.dimension(), state, undo);
        // Push the new undo state in front of the list and drop the last one if we have too many (MAX_UNDO)
        stack.update(BuildingWandsModule.BUILDINGWAND_DATA, BuildingWandData.DEFAULT, d -> d.pushUndoState(undoState));
    }

    private void undoPlaceBlock(ItemStack stack, Player player, Level world, BlockPos pos) {
        // Get the first undo state from the data and remove it
        BuildingWandData data = stack.getOrDefault(BuildingWandsModule.BUILDINGWAND_DATA, BuildingWandData.DEFAULT);
        if (data.undoStates().isEmpty()) {
            Tools.error(player, "Nothing to undo!");
            return;
        }
        int index = data.findUndoStateIndex(world.dimension(), pos);
        if (index == -1) {
            Tools.error(player, "Select at least one block of the area you want to undo!");
            return;
        }
        // Get the undo state and remove it
        BuildingWandData.UndoState undoState = data.undoStates().get(index);
        List<BuildingWandData.UndoState> newUndoStates = new ArrayList<>(data.undoStates());
        newUndoStates.remove(index);
        stack.update(BuildingWandsModule.BUILDINGWAND_DATA, BuildingWandData.DEFAULT, d -> new BuildingWandData(d.mode(), d.orientationMode(), newUndoStates));

        performUndo(player, world, pos, undoState);
    }

    private void performUndo(Player player, Level world, BlockPos pos, BuildingWandData.UndoState undoState) {
        BlockState state = undoState.state();

        int cnt = 0;
        for (BlockPos coordinate : undoState.positions()) {
            BlockState testState = world.getBlockState(coordinate);
            if (testState == state) {
                SoundTools.playSound(world, state.getSoundType().getStepSound(), coordinate.getX(), coordinate.getY(), coordinate.getZ(), 1.0f, 1.0f);

                BlockSnapshot blocksnapshot = BlockSnapshot.create(world.dimension(), world, coordinate);
                world.setBlockAndUpdate(coordinate, Blocks.AIR.defaultBlockState());
                if (EventHooks.onBlockPlace(player, blocksnapshot, Direction.UP)) {
//                    blocksnapshot.restore(true, false);
                    blocksnapshot.restore(0);  // @todo 1.21 check this
                } else {
                    cnt++;
                }
            }
        }
        if (cnt > 0) {
            if (!player.isCreative()) {
                HitResult result = new BlockHitResult(new Vec3(0, 0, 0), Direction.UP, pos, false);
                ItemStack itemStack = state.getCloneItemStack(result, world, pos, player);
                itemStack.setCount(cnt);
                ItemHandlerHelper.giveItemToPlayer(player, itemStack);
                player.containerMenu.broadcastChanges();
            }
        }
    }

    @Override
    public void renderOverlay(RenderLevelStageEvent evt, Player player, ItemStack wand) {
        if (!(Minecraft.getInstance().hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) {
            return;
        }

        Level world = player.level();
        BlockPos pos = hit.getBlockPos();
        BlockState state = world.getBlockState(pos);
        if (state.isAir()) {
            return;
        }
        if (player.isShiftKeyDown()) {
            BuildingWandData data = wand.getOrDefault(BuildingWandsModule.BUILDINGWAND_DATA, BuildingWandData.DEFAULT);
            int index = data.findUndoStateIndex(world.dimension(), pos);
            if (index >= 0) {
                renderOutlines(evt, player, data.undoStates().get(index).positions(), 240, 30, 0);
            }
            return;
        }

        Direction face = hit.getDirection();
        Direction sourceDirection = face.getOpposite();
        Set<BlockPos> coordinates = findSuitableBlocks(wand, world, face, pos, state);
        ClientTools.renderPlacementPreview(evt.getPoseStack(), player, coordinates,
                destination -> Tools.pickBlock(state, world, destination.relative(sourceDirection), player), false);
        renderOutlines(evt, player, coordinates, 50, 250, 180);
    }

    private Set<BlockPos> findSuitableBlocks(ItemStack stack, Level world, Direction sideHit, BlockPos pos, BlockState state) {
        BuildingWandData.Mode mode = getMode(stack);
        int maxAmount = mode.getAmount();
        boolean rowMode = mode == MODE_9ROW || mode == MODE_25ROW;
        boolean rotated = getSubMode(stack) == BuildingWandData.OrientationMode.ROTATED;
        Direction firstAxis = getFirstAxis(sideHit);
        Direction secondAxis = getSecondAxis(sideHit);
        if (rowMode) {
            Direction preferred = rotated ? secondAxis : firstAxis;
            Direction alternate = rotated ? firstAxis : secondAxis;
            Set<BlockPos> row = findRow(world, pos, sideHit, state, maxAmount, preferred);
            if (row.size() == maxAmount) {
                return row;
            }
            Set<BlockPos> otherRow = findRow(world, pos, sideHit, state, maxAmount, alternate);
            return otherRow.size() > row.size() ? otherRow : row;
        }

        Set<BlockPos> coordinates = new LinkedHashSet<>();
        Set<BlockPos> visited = new HashSet<>();
        Deque<BlockPos> pending = new ArrayDeque<>();
        pending.addLast(pos);
        Direction oppositeFirst = firstAxis.getOpposite();
        Direction oppositeSecond = secondAxis.getOpposite();
        while (!pending.isEmpty() && coordinates.size() < maxAmount) {
            BlockPos base = pending.removeFirst();
            BlockPos destination = base.relative(sideHit);
            if (visited.add(base) && isSuitable(world, state, base, destination)) {
                coordinates.add(destination);
                pending.addLast(base.relative(firstAxis));
                pending.addLast(base.relative(oppositeFirst));
                pending.addLast(base.relative(secondAxis));
                pending.addLast(base.relative(oppositeSecond));
                pending.addLast(base.relative(firstAxis).relative(secondAxis));
                pending.addLast(base.relative(firstAxis).relative(oppositeSecond));
                pending.addLast(base.relative(oppositeFirst).relative(secondAxis));
                pending.addLast(base.relative(oppositeFirst).relative(oppositeSecond));
            }
        }
        return coordinates;
    }

    private Set<BlockPos> findRow(Level world, BlockPos pos, Direction face, BlockState state, int maxAmount, Direction axis) {
        Set<BlockPos> row = new LinkedHashSet<>();
        BlockPos destination = pos.relative(face);
        if (!isSuitable(world, state, pos, destination)) {
            return row;
        }
        row.add(destination);

        Direction opposite = axis.getOpposite();
        int forward = countRow(world, pos, face, state, maxAmount - 1, axis);
        int backward = countRow(world, pos, face, state, maxAmount - 1, opposite);
        for (int distance = 1; row.size() < maxAmount && distance <= Math.max(forward, backward); distance++) {
            if (distance <= forward) {
                row.add(destination.relative(axis, distance));
            }
            if (distance <= backward && row.size() < maxAmount) {
                row.add(destination.relative(opposite, distance));
            }
        }
        return row;
    }

    private int countRow(Level world, BlockPos pos, Direction face, BlockState state, int limit, Direction axis) {
        int count = 0;
        while (count < limit) {
            pos = pos.relative(axis);
            if (!isSuitable(world, state, pos, pos.relative(face))) {
                break;
            }
            count++;
        }
        return count;
    }

    private boolean isSuitable(Level world, BlockState state, BlockPos base, BlockPos offset) {
        BlockState destState = world.getBlockState(offset);
        BlockState baseState = world.getBlockState(base);
        return baseState == state && destState.canBeReplaced();
    }

    private Direction getFirstAxis(Direction direction) {
        return switch (direction) {
            case DOWN, UP -> Direction.EAST;
            case NORTH, SOUTH -> Direction.EAST;
            case WEST, EAST -> Direction.DOWN;
        };
    }

    private Direction getSecondAxis(Direction direction) {
        return switch (direction) {
            case DOWN, UP -> Direction.SOUTH;
            case NORTH, SOUTH -> Direction.DOWN;
            case WEST, EAST -> Direction.SOUTH;
        };
    }

}
