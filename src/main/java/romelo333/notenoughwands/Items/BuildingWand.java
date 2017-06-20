package romelo333.notenoughwands.Items;


import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import romelo333.notenoughwands.varia.Tools;

import java.util.*;

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
        setup("building_wand").xpUsage(1).availability(AVAILABILITY_NORMAL).loot(3);
    }

    @Override
    protected void initConfig(Configuration cfg) {
        super.initConfig(cfg, 2000, 100000, 500, 200000, 200, 500000);
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<String> list, ITooltipFlag b) {
        super.addInformation(stack, player, list, b);
        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null) {
            int cnt = (compound.hasKey("undo1") ? 1 : 0) + (compound.hasKey("undo2") ? 1 : 0);
            list.add(TextFormatting.GREEN + "Has " + cnt + " undo states");
            int mode = compound.getInteger("mode");
            if (mode == MODE_9ROW || mode == MODE_25ROW) {
                int submode = getSubMode(stack);
                list.add(TextFormatting.GREEN + "Mode: " + descriptions[mode] + (submode == 1 ? " [Rotated]" : ""));
            } else {
                list.add(TextFormatting.GREEN + "Mode: " + descriptions[mode]);
            }
        }
        list.add("Right click to extend blocks in that direction.");
        list.add("Sneak right click on such a block to undo one of");
        list.add("the last two operations.");

        showModeKeyDescription(list, "switch mode");
        showSubModeKeyDescription(list, "change orientation");
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

    @Override
    public void toggleSubMode(EntityPlayer player, ItemStack stack) {
        int submode = getSubMode(stack);
        submode = submode == 1 ? 0 : 1;
        Tools.notify(player, "Switched orientation");
        Tools.getTagCompound(stack).setInteger("submode", submode);
    }

    private int getMode(ItemStack stack) {
        return Tools.getTagCompound(stack).getInteger("mode");
    }

    private int getSubMode(ItemStack stack) {
        return Tools.getTagCompound(stack).getInteger("submode");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            if (player.isSneaking()) {
                undoPlaceBlock(stack, player, world, pos);
            } else {
                placeBlock(stack, player, world, pos, side);
            }
        }
        return EnumActionResult.SUCCESS;
    }

    private void placeBlock(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
        if (!checkUsage(stack, player, 1.0f)) {
            return;
        }
        boolean notenough = false;
        IBlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        int meta = block.getMetaFromState(blockState);


        Set<BlockPos> coordinates = findSuitableBlocks(stack, world, side, pos, block, meta);
        Set<BlockPos> undo = new HashSet<BlockPos>();
        for (BlockPos coordinate : coordinates) {
            if (!checkUsage(stack, player, 1.0f)) {
                break;
            }
            if (Tools.consumeInventoryItem(Item.getItemFromBlock(block), meta, player.inventory, player)) {
                Tools.playSound(world, block.getSoundType().getStepSound(), coordinate.getX(), coordinate.getY(), coordinate.getZ(), 1.0f, 1.0f);
                IBlockState state = block.getStateFromMeta(meta);
                world.setBlockState(coordinate, state, 2);
                player.openContainer.detectAndSendChanges();
                registerUsage(stack, player, 1.0f);
                undo.add(coordinate);
            } else {
                notenough = true;
            }
        }
        if (notenough) {
            Tools.error(player, "You don't have the right block");
        }

        registerUndo(stack, block, meta, world, undo);
    }

    private void registerUndo(ItemStack stack, Block block, int meta, World world, Set<BlockPos> undo) {
        NBTTagCompound undoTag = new NBTTagCompound();
        undoTag.setInteger("block", Block.REGISTRY.getIDForObject(block));
        undoTag.setInteger("meta", meta);
        undoTag.setInteger("dimension", world.provider.getDimension());
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

        undoTag.setIntArray("x", undoX);
        undoTag.setIntArray("y", undoY);
        undoTag.setIntArray("z", undoZ);
        NBTTagCompound wandTag = Tools.getTagCompound(stack);
        if (wandTag.hasKey("undo1")) {
            wandTag.setTag("undo2", wandTag.getTag("undo1"));
        }
        wandTag.setTag("undo1", undoTag);
    }

    private void undoPlaceBlock(ItemStack stack, EntityPlayer player, World world, BlockPos pos) {
        NBTTagCompound wandTag = Tools.getTagCompound(stack);
        NBTTagCompound undoTag1 = (NBTTagCompound) wandTag.getTag("undo1");
        NBTTagCompound undoTag2 = (NBTTagCompound) wandTag.getTag("undo2");

        Set<BlockPos> undo1 = checkUndo(player, world, undoTag1);
        Set<BlockPos> undo2 = checkUndo(player, world, undoTag2);
        if (undo1 == null && undo2 == null) {
            Tools.error(player, "Nothing to undo!");
            return;
        }

        if (undo1 != null && undo1.contains(pos)) {
            performUndo(stack, player, world, pos, undoTag1, undo1);
            if (wandTag.hasKey("undo2")) {
                wandTag.setTag("undo1", wandTag.getTag("undo2"));
                wandTag.removeTag("undo2");
            } else {
                wandTag.removeTag("undo1");
            }
            return;
        }
        if (undo2 != null && undo2.contains(pos)) {
            performUndo(stack, player, world, pos, undoTag2, undo2);
            wandTag.removeTag("undo2");
            return;
        }

        Tools.error(player, "Select at least one block of the area you want to undo!");
    }

    private void performUndo(ItemStack stack, EntityPlayer player, World world, BlockPos pos, NBTTagCompound undoTag, Set<BlockPos> undo) {
        Block block = Block.REGISTRY.getObjectById(undoTag.getInteger("block"));
        int meta = undoTag.getInteger("meta");

        int cnt = 0;
        for (BlockPos coordinate : undo) {
            IBlockState testState = world.getBlockState(coordinate);
            Block testBlock = testState.getBlock();
            int testMeta = testBlock.getMetaFromState(testState);
            if (testBlock == block && testMeta == meta) {
                Tools.playSound(world, block.getSoundType().getStepSound(), coordinate.getX(), coordinate.getY(), coordinate.getZ(), 1.0f, 1.0f);
                world.setBlockToAir(coordinate);
                cnt++;
            }
        }
        if (cnt > 0) {
            if (!player.capabilities.isCreativeMode) {
                Tools.giveItem(world, player, block, meta, cnt, pos);
                player.openContainer.detectAndSendChanges();
            }
        }
    }

    private Set<BlockPos> checkUndo(EntityPlayer player, World world, NBTTagCompound undoTag) {
        if (undoTag == null) {
            return null;
        }
        int dimension = undoTag.getInteger("dimension");
        if (dimension != world.provider.getDimension()) {
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


    @SideOnly(Side.CLIENT)
    @Override
    public void renderOverlay(RenderWorldLastEvent evt, EntityPlayerSP player, ItemStack wand) {
        RayTraceResult mouseOver = Minecraft.getMinecraft().objectMouseOver;
        if (mouseOver != null && mouseOver.sideHit != null && mouseOver.getBlockPos() != null) {
            World world = player.getEntityWorld();
            BlockPos blockPos = mouseOver.getBlockPos();
            if (blockPos == null) {
                return;
            }
            IBlockState blockState = world.getBlockState(blockPos);
            Block block = blockState.getBlock();
            if (block != null && block.getMaterial(blockState) != Material.AIR) {
                Set<BlockPos> coordinates;
                int meta = block.getMetaFromState(blockState);

                if (player.isSneaking()) {
                    NBTTagCompound wandTag = Tools.getTagCompound(wand);
                    NBTTagCompound undoTag1 = (NBTTagCompound) wandTag.getTag("undo1");
                    NBTTagCompound undoTag2 = (NBTTagCompound) wandTag.getTag("undo2");

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
                    coordinates = findSuitableBlocks(wand, world, mouseOver.sideHit, blockPos, block, meta);
                    renderOutlines(evt, player, coordinates, 50, 250, 180);
                }
            }
        }
    }

    private Set<BlockPos> findSuitableBlocks(ItemStack stack, World world, EnumFacing sideHit, BlockPos pos, Block block, int meta) {
        Set<BlockPos> coordinates = new HashSet<>();
        Set<BlockPos> done = new HashSet<>();
        Deque<BlockPos> todo = new ArrayDeque<>();
        todo.addLast(pos);
        findSuitableBlocks(world, coordinates, done, todo, sideHit, block, meta, amount[getMode(stack)],
                getMode(stack) == MODE_9ROW || getMode(stack) == MODE_25ROW, getSubMode(stack));

        return coordinates;
    }

    private void findSuitableBlocks(World world, Set<BlockPos> coordinates, Set<BlockPos> done, Deque<BlockPos> todo, EnumFacing direction, Block block, int meta, int maxAmount,
                                    boolean rowMode, int rotated) {

        EnumFacing dirA = null;
        EnumFacing dirB = null;
        if (rowMode) {
            BlockPos base = todo.getFirst();
            BlockPos offset = base.offset(direction);
            dirA = rotated == 1 ? dir2(direction) : dir1(direction);
            dirB = dirA.getOpposite();
            if (!isSuitable(world, block, meta, base.offset(dirA), offset.offset(dirA)) ||
                !isSuitable(world, block, meta, base.offset(dirB), offset.offset(dirB))) {
                dirA = rotated == 1 ? dir3(direction) : dir2(direction);
                dirB = dirA.getOpposite();
                if (!isSuitable(world, block, meta, base.offset(dirA), offset.offset(dirA)) ||
                        !isSuitable(world, block, meta, base.offset(dirB), offset.offset(dirB))) {
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
                if (isSuitable(world, block, meta, base, offset)) {
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

    private boolean isSuitable(World world, Block block, int meta, BlockPos base, BlockPos offset) {
        IBlockState destState = world.getBlockState(offset);
        Block destBlock = destState.getBlock();
        if (destBlock == null) {
            destBlock = Blocks.AIR;
        }
        IBlockState baseState = world.getBlockState(base);
        return baseState.getBlock() == block && baseState.getBlock().getMetaFromState(baseState) == meta &&
                destBlock.isReplaceable(world, offset);
    }

    private EnumFacing dir1(EnumFacing direction) {
        switch (direction) {
            case DOWN:
            case UP:
                return EnumFacing.EAST;
            case NORTH:
            case SOUTH:
                return EnumFacing.EAST;
            case WEST:
            case EAST:
                return EnumFacing.DOWN;
        }
        return null;
    }

    private EnumFacing dir2(EnumFacing direction) {
        switch (direction) {
            case DOWN:
            case UP:
                return EnumFacing.SOUTH;
            case NORTH:
            case SOUTH:
                return EnumFacing.DOWN;
            case WEST:
            case EAST:
                return EnumFacing.SOUTH;
        }
        return null;
    }

    private EnumFacing dir3(EnumFacing direction) {
        switch (direction) {
            case DOWN:
            case UP:
                return EnumFacing.SOUTH;
            case NORTH:
            case SOUTH:
                return EnumFacing.WEST;
            case WEST:
            case EAST:
                return EnumFacing.SOUTH;
        }
        return null;
    }


    @Override
    protected void setupCraftingInt(Item wandcore) {
    }

}
