package romelo333.notenoughwands.modules.wands.Items;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import romelo333.notenoughwands.setup.Configuration;
import romelo333.notenoughwands.varia.Tools;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DisplacementWand extends GenericWand {

    private float maxHardness = 50;

    public static final int MODE_FIRST = 0;
    public static final int MODE_3X3 = 0;
    public static final int MODE_5X5 = 1;
    public static final int MODE_7X7 = 2;
    public static final int MODE_SINGLE = 3;
    public static final int MODE_LAST = MODE_SINGLE;

    public static final String[] descriptions = new String[] {
            "3x3", "5x5", "7x7", "single"
    };

    public static final int[] amount = new int[] { 9, 9, 25, 1 };

    public DisplacementWand() {
        setup().loot(3).usageFactory(1.0f);
    }

    @Override
    public void initConfig(Configuration cfg) {
        // @todo 1.15 config
//        maxHardness = (float) cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_maxHardness", maxHardness, "Max hardness this block can move.)").getDouble();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> list, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, list, flagIn);
        // @todo 1.15 tooltips
        list.add(new StringTextComponent(TextFormatting.GREEN + "Mode: " + descriptions[getMode(stack)]));
        list.add(new StringTextComponent("Right click to push blocks forward."));
        list.add(new StringTextComponent("Sneak right click to pull blocks."));
        showModeKeyDescription(list, "switch mode");
    }

    @Override
    public void toggleMode(PlayerEntity player, ItemStack stack) {
        int mode = getMode(stack);
        mode++;
        if (mode > MODE_LAST) {
            mode = MODE_FIRST;
        }
        Tools.notify(player, "Switched to " + descriptions[mode] + " mode");
        stack.getOrCreateTag().putInt("mode", mode);
    }

    private int getMode(ItemStack stack) {
        return stack.getOrCreateTag().getInt("mode");
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getPos();
        Direction side = context.getFace();
        if (!world.isRemote) {
            if (player.isSneaking()) {
                pullBlocks(stack, player, world, pos, side);
            } else {
                pushBlocks(stack, player, world, pos, side);
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    private void pullBlocks(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction side) {
        if (!checkUsage(stack, player, 1.0f)) {
            return;
        }
        Set<BlockPos> coordinates = findSuitableBlocks(stack, world, side, pos);
        int cnt = moveBlocks(player, world, coordinates, side);
        if (cnt > 0) {
            registerUsage(stack, player, 1.0f);
        }
    }

    private void pushBlocks(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction side) {
        if (!checkUsage(stack, player, 1.0f)) {
            return;
        }
        Set<BlockPos> coordinates = findSuitableBlocks(stack, world, side, pos);
        int cnt = moveBlocks(player, world, coordinates, side.getOpposite());
        if (cnt > 0) {
            registerUsage(stack, player, 1.0f);
        }
    }

    private int moveBlocks(PlayerEntity player, World world, Set<BlockPos> coordinates, Direction direction) {
        int cnt = 0;
        for (BlockPos coordinate : coordinates) {
            BlockState state = world.getBlockState(coordinate);
            Block block = state.getBlock();
            BlockPos otherC = coordinate.offset(direction);
            BlockState otherState = world.getBlockState(otherC);// @todo 1.15 better support for blockstates
            Block otherBlock = otherState.getBlock();
            if (true) {
//            if (otherBlock.isReplaceable(world, otherC)) {// @todo 1.15
                double cost = GenericWand.checkPickup(player, world, otherC, block, maxHardness);
                if (cost >= 0.0) {
                    cnt++;
                    Tools.playSound(world, block.getSoundType(state).getStepSound(), coordinate.getX(), coordinate.getY(), coordinate.getZ(), 1.0f, 1.0f);
                    TileEntity tileEntity = world.getTileEntity(coordinate);
                    CompoundNBT tc = null;
                    if (tileEntity != null) {
                        tc = new CompoundNBT();
                        tileEntity.write(tc);
                        world.removeTileEntity(coordinate);
                    }

                    world.setBlockState(coordinate, Blocks.AIR.getDefaultState());

                    BlockSnapshot blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(world, otherC);
                    BlockState blockState = block.getDefaultState();    // @todo 1.15 blockstate
                    world.setBlockState(otherC, blockState, 3);
                    if (ForgeEventFactory.onBlockPlace(player, blocksnapshot, Direction.UP)) {
                        blocksnapshot.restore(true, false);
                        world.setBlockState(coordinate, blockState, 3);
                        // Make sure we restore the tileentity at the original spot
                        otherC = coordinate;
                    }

                    if (tc != null) {
                        tc.putInt("x", otherC.getX());
                        tc.putInt("y", otherC.getY());
                        tc.putInt("z", otherC.getZ());
                        tileEntity = TileEntity.create(tc);
                        if (tileEntity != null) {
                            world.getChunk(otherC).addTileEntity(otherC, tileEntity);
                            tileEntity.markDirty();
                            world.notifyBlockUpdate(otherC, blockState, blockState, 3); // @todo 1.15 constants
                        }

//                        tileEntity = world.getTileEntity(otherC);
//                        if (tileEntity != null) {
//                            tc.putInt("x", otherC.getX());
//                            tc.putInt("y", otherC.getY());
//                            tc.putInt("z", otherC.getZ());
//                            tileEntity.readFromNBT(tc);
//                            tileEntity.markDirty();
//                            world.notifyBlockUpdate(otherC, blockState, blockState, 3);
//                        }
                    }
                }
            }
        }
        return cnt;
    }

    @Override
    public void renderOverlay(RenderWorldLastEvent evt, PlayerEntity player, ItemStack wand) {
        // @todo 1.15
//        RayTraceResult mouseOver = Minecraft.getMinecraft().objectMouseOver;
//        if (mouseOver != null && mouseOver.getBlockPos() != null && mouseOver.sideHit != null) {
//            World world = player.getEntityWorld();
//            BlockPos blockPos = mouseOver.getBlockPos();
//            IBlockState state = world.getBlockState(blockPos);
//            Block block = state.getBlock();
//            if (block != null && block.getMaterial(state) != Material.AIR) {
//                Set<BlockPos> coordinates = findSuitableBlocks(wand, world, mouseOver.sideHit, blockPos);
//                renderOutlines(evt, player, coordinates, 200, 230, 180);
//            }
//        }
    }

    private Set<BlockPos> findSuitableBlocks(ItemStack stack, World world, Direction sideHit, BlockPos pos) {
        Set<BlockPos> coordinates = new HashSet<>();
        int mode = getMode(stack);
        int dim = 0;
        switch (mode) {
            case MODE_SINGLE:
                coordinates.add(pos);
                return coordinates;
            case MODE_3X3:
                dim = 1;
                break;
            case MODE_5X5:
                dim = 2;
                break;
            case MODE_7X7:
                dim = 3;
                break;
        }
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        switch (sideHit) {
            case UP:
            case DOWN:
                for (int dx = x - dim; dx <= x + dim; dx++) {
                    for (int dz = z - dim; dz <= z + dim; dz++) {
                        checkAndAddBlock(world, dx, y, dz, coordinates);
                    }
                }
                break;
            case SOUTH:
            case NORTH:
                for (int dx = x - dim; dx <= x + dim; dx++) {
                    for (int dy = y - dim; dy <= y + dim; dy++) {
                        checkAndAddBlock(world, dx, dy, z, coordinates);
                    }
                }
                break;
            case EAST:
            case WEST:
                for (int dy = y - dim; dy <= y + dim; dy++) {
                    for (int dz = z - dim; dz <= z + dim; dz++) {
                        checkAndAddBlock(world, x, dy, dz, coordinates);
                    }
                }
                break;
        }

        return coordinates;
    }

    private void checkAndAddBlock(World world, int x, int y, int z, Set<BlockPos> coordinates) {
        BlockPos pos = new BlockPos(x, y, z);
        if (!world.isAirBlock(pos)) {
            coordinates.add(pos);
        }
    }
}
