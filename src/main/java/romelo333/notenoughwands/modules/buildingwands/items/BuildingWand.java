package romelo333.notenoughwands.modules.buildingwands.items;


import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.varia.LevelTools;
import mcjty.lib.varia.SoundTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.ItemHandlerHelper;
import romelo333.notenoughwands.modules.wands.Items.GenericWand;
import romelo333.notenoughwands.varia.Tools;

;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.ItemHandlerHelper;
import romelo333.notenoughwands.modules.wands.Items.GenericWand;
import romelo333.notenoughwands.varia.Tools;

import java.util.*;

import static mcjty.lib.builder.TooltipBuilder.*;

public class BuildingWand extends GenericWand {

    public static final int MODE_FIRST = 0;
    public static final int MODE_9 = 0;
    public static final int MODE_9ROW = 1;
    public static final int MODE_25 = 2;
    public static final int MODE_25ROW = 3;
    public static final int MODE_SINGLE = 4;
    public static final int MODE_LAST = MODE_SINGLE;

    public static final String[] DESCRIPTIONS = new String[] {
            "9 blocks", "9 blocks row", "25 blocks", "25 blocks row", "single"
    };


    private final TooltipBuilder tooltipBuilder = new TooltipBuilder()
            .info(key("message.notenoughwands.shiftmessage"))
            .infoShift(header(), gold(),
                    parameter("undo", stack -> countUndoStates(stack) > 0, stack -> Integer.toString(countUndoStates(stack))),
                    parameter("mode", stack -> DESCRIPTIONS[getMode(stack)]),
                    parameter("submode", stack -> getSubMode(stack) == 1, stack -> getSubMode(stack) == 1 ? "Rotated" : ""));

    public static final int[] amount = new int[] { 9, 9, 25, 25, 1 };

    public BuildingWand() {
        this.usageFactor(1.0f);
    }

    private int countUndoStates(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundNBT compound = stack.getTag();
            return (compound.contains("undo1") ? 1 : 0) + (compound.contains("undo2") ? 1 : 0);
        } else {
            return 0;
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, World world, List<ITextComponent> list, ITooltipFlag flags) {
        super.appendHoverText(itemStack, world, list, flags);
        tooltipBuilder.makeTooltip(getRegistryName(), itemStack, list, flags);

        showModeKeyDescription(list, "switch mode");
        showSubModeKeyDescription(list, "change orientation");
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

    @Override
    public void toggleSubMode(PlayerEntity player, ItemStack stack) {
        int submode = getSubMode(stack);
        submode = submode == 1 ? 0 : 1;
        Tools.notify(player, new StringTextComponent("Switched orientation"));
        stack.getOrCreateTag().putInt("submode", submode);
    }

    private int getMode(ItemStack stack) {
        return stack.getOrCreateTag().getInt("mode");
    }

    private int getSubMode(ItemStack stack) {
        return stack.getOrCreateTag().getInt("submode");
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();
        World world = context.getLevel();
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
        return ActionResultType.SUCCESS;
    }

    private void placeBlock(ItemStack wandStack, PlayerEntity player, World world, BlockPos pos, Direction side) {
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
            RayTraceResult result = new BlockRayTraceResult(new Vector3d(0, 0, 0), Direction.UP, coordinate, false);
            ItemStack pickBlock = blockState.getPickBlock(result, world, coordinate, player);
            ItemStack consumed = Tools.consumeInventoryItem(pickBlock, player.inventory, player);
            if (!consumed.isEmpty()) {
                SoundTools.playSound(world, blockState.getSoundType().getStepSound(), coordinate.getX(), coordinate.getY(), coordinate.getZ(), 1.0f, 1.0f);
                //                IBlockState state = block.getStateFromMeta(meta);
//                world.setBlockState(coordinate, state, 2);
                BlockSnapshot blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.create(world.dimension(), world, coordinate);
                Tools.placeStackAt(player, consumed, world, coordinate, null);
                if (ForgeEventFactory.onBlockPlace(player, blocksnapshot, Direction.UP)) {
                    blocksnapshot.restore(true, false);
                    if (!player.abilities.instabuild) {
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

    private void registerUndo(ItemStack stack, BlockState state, World world, Set<BlockPos> undo) {
        CompoundNBT undoTag = new CompoundNBT();

        undoTag.put("block", NBTUtil.writeBlockState(state));
        undoTag.putString("dimension", world.dimension().location().toString());
        int[] undoX = new int[undo.size()];
        int[] undoY = new int[undo.size()];
        int[] undoZ = new int[undo.size()];
        int idx = 0;
        for (BlockPos coordinate : undo) {
            undoX[idx] = coordinate.getX();
            undoY[idx] = coordinate.getY();
            undoZ[idx] = coordinate.getZ();
            idx++;
        }

        undoTag.putIntArray("x", undoX);
        undoTag.putIntArray("y", undoY);
        undoTag.putIntArray("z", undoZ);
        CompoundNBT wandTag = stack.getOrCreateTag();
        if (wandTag.contains("undo1")) {
            wandTag.put("undo2", wandTag.get("undo1"));
        }
        wandTag.put("undo1", undoTag);
    }

    private void undoPlaceBlock(ItemStack stack, PlayerEntity player, World world, BlockPos pos) {
        CompoundNBT wandTag = stack.getOrCreateTag();
        CompoundNBT undoTag1 = (CompoundNBT) wandTag.get("undo1");
        CompoundNBT undoTag2 = (CompoundNBT) wandTag.get("undo2");

        Set<BlockPos> undo1 = checkUndo(player, world, undoTag1);
        Set<BlockPos> undo2 = checkUndo(player, world, undoTag2);
        if (undo1 == null && undo2 == null) {
            Tools.error(player, "Nothing to undo!");
            return;
        }

        if (undo1 != null && undo1.contains(pos)) {
            performUndo(stack, player, world, pos, undoTag1, undo1);
            if (wandTag.contains("undo2")) {
                wandTag.put("undo1", wandTag.get("undo2"));
                wandTag.remove("undo2");
            } else {
                wandTag.remove("undo1");
            }
            return;
        }
        if (undo2 != null && undo2.contains(pos)) {
            performUndo(stack, player, world, pos, undoTag2, undo2);
            wandTag.remove("undo2");
            return;
        }

        Tools.error(player, "Select at least one block of the area you want to undo!");
    }

    private void performUndo(ItemStack stack, PlayerEntity player, World world, BlockPos pos, CompoundNBT undoTag, Set<BlockPos> undo) {
        BlockState state = NBTUtil.readBlockState(undoTag.getCompound("block"));

        int cnt = 0;
        for (BlockPos coordinate : undo) {
            BlockState testState = world.getBlockState(coordinate);
            if (testState == state) {
                SoundTools.playSound(world, state.getSoundType().getStepSound(), coordinate.getX(), coordinate.getY(), coordinate.getZ(), 1.0f, 1.0f);

                BlockSnapshot blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.create(world.dimension(), world, coordinate);
                world.setBlockAndUpdate(coordinate, Blocks.AIR.defaultBlockState());
                if (ForgeEventFactory.onBlockPlace(player, blocksnapshot, Direction.UP)) {
                    blocksnapshot.restore(true, false);
                } else {
                    cnt++;
                }
            }
        }
        if (cnt > 0) {
            if (!player.abilities.instabuild) {
                RayTraceResult result = new BlockRayTraceResult(new Vector3d(0, 0, 0), Direction.UP, pos, false);
                ItemStack itemStack = state.getPickBlock(result, world, pos, player);
                itemStack.setCount(cnt);
                ItemHandlerHelper.giveItemToPlayer(player, itemStack);
                player.containerMenu.broadcastChanges();
            }
        }
    }

    private Set<BlockPos> checkUndo(PlayerEntity player, World world, CompoundNBT undoTag) {
        if (undoTag == null) {
            return null;
        }
        String dimension = undoTag.getString("dimension");
        RegistryKey<World> dim = LevelTools.getId(new ResourceLocation(dimension));
        if (!Objects.equals(dim, world.dimension())) {
            Tools.error(player, "Select at least one block of the area you want to undo!");
            return null;
        }

        int[] undoX = undoTag.getIntArray("x");
        int[] undoY = undoTag.getIntArray("y");
        int[] undoZ = undoTag.getIntArray("z");
        Set<BlockPos> undo = new HashSet<BlockPos>();
        for (int i = 0 ; i < undoX.length ; i++) {
            undo.add(new BlockPos(undoX[i], undoY[i], undoZ[i]));
        }
        return undo;
    }


    @Override
    public void renderOverlay(RenderWorldLastEvent evt, PlayerEntity player, ItemStack wand) {
        RayTraceResult mouseOver = Minecraft.getInstance().hitResult;
        if (!(mouseOver instanceof BlockRayTraceResult)) {
            return;
        }

        BlockRayTraceResult btrace = (BlockRayTraceResult) mouseOver;

        if (btrace.getDirection() != null && btrace.getBlockPos() != null) {
            World world = player.getCommandSenderWorld();
            BlockPos blockPos = btrace.getBlockPos();
            if (blockPos == null) {
                return;
            }
            BlockState blockState = world.getBlockState(blockPos);
            Block block = blockState.getBlock();
            if (block != null && blockState.getMaterial() != Material.AIR) {
                Set<BlockPos> coordinates;

                if (player.isShiftKeyDown()) {
                    CompoundNBT wandTag = wand.getOrCreateTag();
                    CompoundNBT undoTag1 = (CompoundNBT) wandTag.get("undo1");
                    CompoundNBT undoTag2 = (CompoundNBT) wandTag.get("undo2");

                    Set<BlockPos> undo1 = checkUndo(player, world, undoTag1);
                    Set<BlockPos> undo2 = checkUndo(player, world, undoTag2);
                    if (undo1 == null && undo2 == null) {
                        return;
                    }

                    if (undo1 != null && undo1.contains(blockPos)) {
                        coordinates = undo1;
                        renderOutlines(evt, player, coordinates, 240, 30, 0);
                    } else if (undo2 != null && undo2.contains(blockPos)) {
                        coordinates = undo2;
                        renderOutlines(evt, player, coordinates, 240, 30, 0);
                    }
                } else {
                    coordinates = findSuitableBlocks(wand, world, btrace.getDirection(), blockPos, blockState);
                    renderOutlines(evt, player, coordinates, 50, 250, 180);
                }
            }
        }
    }

    private Set<BlockPos> findSuitableBlocks(ItemStack stack, World world, Direction sideHit, BlockPos pos, BlockState state) {
        Set<BlockPos> coordinates = new HashSet<>();
        Set<BlockPos> done = new HashSet<>();
        Deque<BlockPos> todo = new ArrayDeque<>();
        todo.addLast(pos);
        findSuitableBlocks(world, coordinates, done, todo, sideHit, state, amount[getMode(stack)],
                getMode(stack) == MODE_9ROW || getMode(stack) == MODE_25ROW, getSubMode(stack));

        return coordinates;
    }

    private void findSuitableBlocks(World world, Set<BlockPos> coordinates, Set<BlockPos> done, Deque<BlockPos> todo, Direction direction,
                                    BlockState state, int maxAmount, boolean rowMode, int rotated) {

        Direction dirA = null;
        Direction dirB = null;
        if (rowMode) {
            BlockPos base = todo.getFirst();
            BlockPos offset = base.relative(direction);
            dirA = rotated == 1 ? dir2(direction) : dir1(direction);
            dirB = dirA.getOpposite();
            if (!isSuitable(world, state, base.relative(dirA), offset.relative(dirA)) ||
                !isSuitable(world, state, base.relative(dirB), offset.relative(dirB))) {
                dirA = rotated == 1 ? dir3(direction) : dir2(direction);
                dirB = dirA.getOpposite();
                if (!isSuitable(world, state, base.relative(dirA), offset.relative(dirA)) ||
                        !isSuitable(world, state, base.relative(dirB), offset.relative(dirB))) {
                    dirA = rotated == 1 ? dir1(direction) : dir3(direction);
                    dirB = dirA.getOpposite();
                }
            }
        }

        while (!todo.isEmpty() && coordinates.size() < maxAmount) {
            BlockPos base = todo.pollFirst();
            if (!done.contains(base)) {
                done.add(base);
                BlockPos offset = base.relative(direction);
                if (isSuitable(world, state, base, offset)) {
                    coordinates.add(offset);
                    if (rowMode) {
                        todo.addLast(base.relative(dirA));
                        todo.addLast(base.relative(dirB));
                    } else {
                        todo.addLast(base.relative(dir1(direction)));
                        todo.addLast(base.relative(dir1(direction).getOpposite()));
                        todo.addLast(base.relative(dir2(direction)));
                        todo.addLast(base.relative(dir2(direction).getOpposite()));
                        todo.addLast(base.relative(dir1(direction)).relative(dir2(direction)));
                        todo.addLast(base.relative(dir1(direction)).relative(dir2(direction).getOpposite()));
                        todo.addLast(base.relative(dir1(direction).getOpposite()).relative(dir2(direction)));
                        todo.addLast(base.relative(dir1(direction).getOpposite()).relative(dir2(direction).getOpposite()));
                    }
                }
            }
        }
    }

    private boolean isSuitable(World world, BlockState state, BlockPos base, BlockPos offset) {
        BlockState destState = world.getBlockState(offset);
        BlockState baseState = world.getBlockState(base);
        return baseState == state && destState.getMaterial().isReplaceable();// @todo 1.15 check replacable?
    }

    private Direction dir1(Direction direction) {
        switch (direction) {
            case DOWN:
            case UP:
                return Direction.EAST;
            case NORTH:
            case SOUTH:
                return Direction.EAST;
            case WEST:
            case EAST:
                return Direction.DOWN;
        }
        return null;
    }

    private Direction dir2(Direction direction) {
        switch (direction) {
            case DOWN:
            case UP:
                return Direction.SOUTH;
            case NORTH:
            case SOUTH:
                return Direction.DOWN;
            case WEST:
            case EAST:
                return Direction.SOUTH;
        }
        return null;
    }

    private Direction dir3(Direction direction) {
        switch (direction) {
            case DOWN:
            case UP:
                return Direction.SOUTH;
            case NORTH:
            case SOUTH:
                return Direction.WEST;
            case WEST:
            case EAST:
                return Direction.SOUTH;
        }
        return null;
    }

}
