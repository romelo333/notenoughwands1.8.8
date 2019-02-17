package romelo333.notenoughwands.items;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipOptions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import romelo333.notenoughwands.Configuration;
import romelo333.notenoughwands.mcjtylib.BlockTools;
import romelo333.notenoughwands.varia.Tools;

import java.util.*;
import java.util.List;

public class BuildingWand extends GenericWand {

    public static final int MODE_FIRST = 0;
    public static final int MODE_9 = 0;
    public static final int MODE_9ROW = 1;
    public static final int MODE_25 = 2;
    public static final int MODE_25ROW = 3;
    public static final int MODE_SINGLE = 4;
    public static final int MODE_LAST = MODE_SINGLE;

    public static final String[] descriptions = new String[] {
            "9 blocks", "9 blocks row", "25 blocks", "25 blocks row", "single"
    };

    public static final int[] amount = new int[] { 9, 9, 25, 25, 1 };

    public BuildingWand() {
        super(100);
        setup("building_wand").xpUsage(1).loot(3);
    }

    @Override
    protected void initConfig(Configuration cfg) {
        super.initConfig(cfg, 2000, 100000, 500, 200000, 200, 500000);
    }


    @Override
    public void buildTooltip(ItemStack stack, World player, List<TextComponent> list, TooltipOptions b) {
        super.buildTooltip(stack, player, list, b);
        CompoundTag compound = stack.getTag();
        if (compound != null) {
            int cnt = (compound.containsKey("undo1") ? 1 : 0) + (compound.containsKey("undo2") ? 1 : 0);
            list.add(new StringTextComponent(TextFormat.GREEN + "Has " + cnt + " undo states"));
            int mode = compound.getInt("mode");
            if (mode == MODE_9ROW || mode == MODE_25ROW) {
                int submode = getSubMode(stack);
                list.add(new StringTextComponent(TextFormat.GREEN + "Mode: " + descriptions[mode] + (submode == 1 ? " [Rotated]" : "")));
            } else {
                list.add(new StringTextComponent(TextFormat.GREEN + "Mode: " + descriptions[mode]));
            }
        }
        list.add(new StringTextComponent("Right click to extend blocks in that direction."));
        list.add(new StringTextComponent("Sneak right click on such a block to undo one of"));
        list.add(new StringTextComponent("the last two operations."));

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
        Tools.notify(player, "Switched to " + descriptions[mode] + " mode");
        Tools.getTagCompound(stack).putInt("mode", mode);
    }

    @Override
    public void toggleSubMode(PlayerEntity player, ItemStack stack) {
        int submode = getSubMode(stack);
        submode = submode == 1 ? 0 : 1;
        Tools.notify(player, "Switched orientation");
        Tools.getTagCompound(stack).putInt("submode", submode);
    }

    private int getMode(ItemStack stack) {
        return Tools.getTagCompound(stack).getInt("mode");
    }

    private int getSubMode(ItemStack stack) {
        return Tools.getTagCompound(stack).getInt("submode");
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        ItemStack stack = context.getItemStack();
        if (!world.isClient) {
            if (player.isSneaking()) {
                undoPlaceBlock(stack, player, world, pos);
            } else {
                placeBlock(stack, player, world, pos, context.getFacing());
            }
        }
        return ActionResult.SUCCESS;
    }

    private void placeBlock(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction side) {
        if (!checkUsage(stack, player, 1.0f)) {
            return;
        }
        boolean notenough = false;
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();


        Set<BlockPos> coordinates = findSuitableBlocks(stack, world, side, pos, block);
        Set<BlockPos> undo = new HashSet<BlockPos>();
        for (BlockPos coordinate : coordinates) {
            if (!checkUsage(stack, player, 1.0f)) {
                break;
            }
            ItemStack consumed = Tools.consumeInventoryItem(Item.getItemFromBlock(block), player.inventory, player);
            if (!consumed.isEmpty()) {
                Tools.playSound(world, block.getSoundGroup(blockState).getStepSound(), coordinate.getX(), coordinate.getY(), coordinate.getZ(), 1.0f, 1.0f);
//                IBlockState state = block.getStateFromMeta(meta);
//                world.setBlockState(coordinate, state, 2);
//                BlockSnapshot blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(world, coordinate);
                BlockTools.placeStackAt(player, consumed, world, coordinate, null);
//                if (ForgeEventFactory.onPlayerBlockPlace(player, blocksnapshot, Direction.UP, EnumHand.MAIN_HAND).isCanceled()) {
//                    blocksnapshot.restore(true, false);
//                    if (!player.capabilities.isCreativeMode) {
//                        Tools.giveItem(world, player, player.getPosition(), consumed);
//                    }
//                }
                player.container.sendContentUpdates();
                registerUsage(stack, player, 1.0f);
                undo.add(coordinate);
            } else {
                notenough = true;
            }
        }
        if (notenough) {
            Tools.error(player, "You don't have the right block");
        }

        registerUndo(stack, block, world, undo);
    }

    private void registerUndo(ItemStack stack, Block block, World world, Set<BlockPos> undo) {
        CompoundTag undoTag = new CompoundTag();
        undoTag.putString("block", Registry.BLOCK.getId(block).toString());
        undoTag.putInt("dimension", world.dimension.getType().getRawId());
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
        CompoundTag wandTag = Tools.getTagCompound(stack);
        if (wandTag.containsKey("undo1")) {
            wandTag.put("undo2", wandTag.getTag("undo1"));
        }
        wandTag.put("undo1", undoTag);
    }

    private void undoPlaceBlock(ItemStack stack, PlayerEntity player, World world, BlockPos pos) {
        CompoundTag wandTag = Tools.getTagCompound(stack);
        CompoundTag undoTag1 = (CompoundTag) wandTag.getTag("undo1");
        CompoundTag undoTag2 = (CompoundTag) wandTag.getTag("undo2");

        Set<BlockPos> undo1 = checkUndo(player, world, undoTag1);
        Set<BlockPos> undo2 = checkUndo(player, world, undoTag2);
        if (undo1 == null && undo2 == null) {
            Tools.error(player, "Nothing to undo!");
            return;
        }

        if (undo1 != null && undo1.contains(pos)) {
            performUndo(stack, player, world, pos, undoTag1, undo1);
            if (wandTag.containsKey("undo2")) {
                wandTag.put("undo1", wandTag.getTag("undo2"));
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

    private void performUndo(ItemStack stack, PlayerEntity player, World world, BlockPos pos, CompoundTag undoTag, Set<BlockPos> undo) {
        Block block = Registry.BLOCK.get(new Identifier(undoTag.getString("block")));

        int cnt = 0;
        for (BlockPos coordinate : undo) {
            BlockState testState = world.getBlockState(coordinate);
            Block testBlock = testState.getBlock();
            if (testBlock == block) {
                Tools.playSound(world, block.getSoundGroup(testState).getStepSound(), coordinate.getX(), coordinate.getY(), coordinate.getZ(), 1.0f, 1.0f);

//                BlockSnapshot blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(world, coordinate);
                world.setBlockState(coordinate, Blocks.AIR.getDefaultState(), 3);   // @todo fabric
//                if (ForgeEventFactory.onPlayerBlockPlace(player, blocksnapshot, Direction.UP, EnumHand.MAIN_HAND).isCanceled()) {
//                    blocksnapshot.restore(true, false);
//                } else {
                    cnt++;
//                }
            }
        }
        if (cnt > 0) {
            if (!player.isCreative()) {
                Tools.giveItem(world, player, block, cnt, pos);
                player.container.sendContentUpdates();
            }
        }
    }

    private Set<BlockPos> checkUndo(PlayerEntity player, World world, CompoundTag undoTag) {
        if (undoTag == null) {
            return null;
        }
        int dimension = undoTag.getInt("dimension");
        if (dimension != world.dimension.getType().getRawId()) {
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
    public void renderOverlay(PlayerEntity player, ItemStack wand, float partialTicks) {
        HitResult mouseOver = MinecraftClient.getInstance().hitResult;
        if (mouseOver != null && mouseOver.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockResult = (BlockHitResult) mouseOver;
            if (blockResult.getSide() != null && blockResult.getBlockPos() != null) {
                World world = player.getEntityWorld();
                BlockPos blockPos = blockResult.getBlockPos();
                if (blockPos == null) {
                    return;
                }
                BlockState blockState = world.getBlockState(blockPos);
                Block block = blockState.getBlock();
                if (block != null && block.getMaterial(blockState) != Material.AIR) {
                    Set<BlockPos> coordinates;

                    if (player.isSneaking()) {
                        CompoundTag wandTag = Tools.getTagCompound(wand);
                        CompoundTag undoTag1 = (CompoundTag) wandTag.getTag("undo1");
                        CompoundTag undoTag2 = (CompoundTag) wandTag.getTag("undo2");

                        Set<BlockPos> undo1 = checkUndo(player, world, undoTag1);
                        Set<BlockPos> undo2 = checkUndo(player, world, undoTag2);
                        if (undo1 == null && undo2 == null) {
                            return;
                        }

                        if (undo1 != null && undo1.contains(blockPos)) {
                            coordinates = undo1;
                            renderOutlines(player, coordinates, 240, 30, 0, partialTicks);
                        } else if (undo2 != null && undo2.contains(blockPos)) {
                            coordinates = undo2;
                            renderOutlines(player, coordinates, 240, 30, 0, partialTicks);
                        }
                    } else {
                        coordinates = findSuitableBlocks(wand, world, blockResult.getSide(), blockPos, block);
                        renderOutlines(player, coordinates, 50, 250, 180, partialTicks);
                    }
                }
            }
        }
    }

    private Set<BlockPos> findSuitableBlocks(ItemStack stack, World world, Direction sideHit, BlockPos pos, Block block) {
        Set<BlockPos> coordinates = new HashSet<>();
        Set<BlockPos> done = new HashSet<>();
        Deque<BlockPos> todo = new ArrayDeque<>();
        todo.addLast(pos);
        findSuitableBlocks(world, coordinates, done, todo, sideHit, block, amount[getMode(stack)],
                getMode(stack) == MODE_9ROW || getMode(stack) == MODE_25ROW, getSubMode(stack));

        return coordinates;
    }

    private void findSuitableBlocks(World world, Set<BlockPos> coordinates, Set<BlockPos> done, Deque<BlockPos> todo, Direction direction, Block block, int maxAmount,
                                    boolean rowMode, int rotated) {

        Direction dirA = null;
        Direction dirB = null;
        if (rowMode) {
            BlockPos base = todo.getFirst();
            BlockPos offset = base.offset(direction);
            dirA = rotated == 1 ? dir2(direction) : dir1(direction);
            dirB = dirA.getOpposite();
            if (!isSuitable(world, block, base.offset(dirA), offset.offset(dirA)) ||
                !isSuitable(world, block, base.offset(dirB), offset.offset(dirB))) {
                dirA = rotated == 1 ? dir3(direction) : dir2(direction);
                dirB = dirA.getOpposite();
                if (!isSuitable(world, block, base.offset(dirA), offset.offset(dirA)) ||
                        !isSuitable(world, block, base.offset(dirB), offset.offset(dirB))) {
                    dirA = rotated == 1 ? dir1(direction) : dir3(direction);
                    dirB = dirA.getOpposite();
                }
            }
        }

        while (!todo.isEmpty() && coordinates.size() < maxAmount) {
            BlockPos base = todo.pollFirst();
            if (!done.contains(base)) {
                done.add(base);
                BlockPos offset = base.offset(direction);
                if (isSuitable(world, block, base, offset)) {
                    coordinates.add(offset);
                    if (rowMode) {
                        todo.addLast(base.offset(dirA));
                        todo.addLast(base.offset(dirB));
                    } else {
                        todo.addLast(base.offset(dir1(direction)));
                        todo.addLast(base.offset(dir1(direction).getOpposite()));
                        todo.addLast(base.offset(dir2(direction)));
                        todo.addLast(base.offset(dir2(direction).getOpposite()));
                        todo.addLast(base.offset(dir1(direction)).offset(dir2(direction)));
                        todo.addLast(base.offset(dir1(direction)).offset(dir2(direction).getOpposite()));
                        todo.addLast(base.offset(dir1(direction).getOpposite()).offset(dir2(direction)));
                        todo.addLast(base.offset(dir1(direction).getOpposite()).offset(dir2(direction).getOpposite()));
                    }
                }
            }
        }
    }

    private boolean isSuitable(World world, Block block, BlockPos base, BlockPos offset) {
        BlockState destState = world.getBlockState(offset);
        Block destBlock = destState.getBlock();
        if (destBlock == null) {
            destBlock = Blocks.AIR;
        }
        BlockState baseState = world.getBlockState(base);
        return baseState.getBlock() == block && destBlock.isAir(destState);// @todo fabric:  && destBlock.isReplaceable(world, offset);
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
