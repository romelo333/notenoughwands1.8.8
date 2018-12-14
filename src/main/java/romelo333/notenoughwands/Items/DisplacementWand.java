package romelo333.notenoughwands.Items;


import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.PlayerEntitySP;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormat;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import romelo333.notenoughwands.Config;
import romelo333.notenoughwands.varia.Tools;

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
        setup("displacement_wand").xpUsage(1).loot(3);
    }

    @Override
    public void initConfig(Configuration cfg) {
        super.initConfig(cfg, 2000, 100000, 500, 200000, 200, 500000);
        maxHardness = (float) cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_maxHardness", maxHardness, "Max hardness this block can move.)").getDouble();
    }

    @Override
    public void addInformation(ItemStack stack, World player, List list, ITooltipFlag b) {
        super.addInformation(stack, player, list, b);
        list.add(TextFormat.GREEN + "Mode: " + descriptions[getMode(stack)]);
        list.add("Right click to push blocks forward.");
        list.add("Sneak right click to pull blocks.");
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
        Tools.getTagCompound(stack).setInteger("mode", mode);
    }

    private int getMode(ItemStack stack) {
        return Tools.getTagCompound(stack).getInteger("mode");
    }

    @Override
    public EnumActionResult onItemUseFirst(PlayerEntity player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            if (player.isSneaking()) {
                pullBlocks(stack, player, world, pos, side);
            } else {
                pushBlocks(stack, player, world, pos, side);
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    private void pullBlocks(ItemStack stack, PlayerEntity player, World world, BlockPos pos, EnumFacing side) {
        if (!checkUsage(stack, player, 1.0f)) {
            return;
        }
        Set<BlockPos> coordinates = findSuitableBlocks(stack, world, side, pos);
        int cnt = moveBlocks(player, world, coordinates, side);
        if (cnt > 0) {
            registerUsage(stack, player, 1.0f);
        }
    }

    private void pushBlocks(ItemStack stack, PlayerEntity player, World world, BlockPos pos, EnumFacing side) {
        if (!checkUsage(stack, player, 1.0f)) {
            return;
        }
        Set<BlockPos> coordinates = findSuitableBlocks(stack, world, side, pos);
        int cnt = moveBlocks(player, world, coordinates, side.getOpposite());
        if (cnt > 0) {
            registerUsage(stack, player, 1.0f);
        }
    }

    private int moveBlocks(PlayerEntity player, World world, Set<BlockPos> coordinates, EnumFacing direction) {
        int cnt = 0;
        for (BlockPos coordinate : coordinates) {
            IBlockState state = world.getBlockState(coordinate);
            Block block = state.getBlock();
            BlockPos otherC = coordinate.offset(direction);
            IBlockState otherState = world.getBlockState(otherC);
            Block otherBlock = otherState.getBlock();
            if (otherBlock.isReplaceable(world, otherC)) {
                double cost = GenericWand.checkPickup(player, world, otherC, block, maxHardness);
                if (cost >= 0.0) {
                    cnt++;
                    int meta = block.getMetaFromState(state);
                    Tools.playSound(world, block.getSoundType().getStepSound(), coordinate.getX(), coordinate.getY(), coordinate.getZ(), 1.0f, 1.0f);
                    TileEntity tileEntity = world.getTileEntity(coordinate);
                    NBTTagCompound tc = null;
                    if (tileEntity != null) {
                        tc = new NBTTagCompound();
                        tileEntity.writeToNBT(tc);
                        world.removeTileEntity(coordinate);
                    }
                    world.setBlockToAir(coordinate);

                    IBlockState blockState = block.getStateFromMeta(meta);
                    world.setBlockState(otherC, blockState, 3);
                    if (tc != null) {
                        tc.setInteger("x", otherC.getX());
                        tc.setInteger("y", otherC.getY());
                        tc.setInteger("z", otherC.getZ());
                        tileEntity = TileEntity.create(world, tc);
                        if (tileEntity != null) {
                            world.getChunkFromBlockCoords(otherC).addTileEntity(tileEntity);
                            tileEntity.markDirty();
                            world.notifyBlockUpdate(otherC, blockState, blockState, 3);
                        }

//                        tileEntity = world.getTileEntity(otherC);
//                        if (tileEntity != null) {
//                            tc.setInteger("x", otherC.getX());
//                            tc.setInteger("y", otherC.getY());
//                            tc.setInteger("z", otherC.getZ());
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

    @SideOnly(Side.CLIENT)
    @Override
    public void renderOverlay(RenderWorldLastEvent evt, PlayerEntitySP player, ItemStack wand) {
        RayTraceResult mouseOver = Minecraft.getMinecraft().objectMouseOver;
        if (mouseOver != null && mouseOver.getBlockPos() != null && mouseOver.sideHit != null) {
            World world = player.getEntityWorld();
            BlockPos blockPos = mouseOver.getBlockPos();
            IBlockState state = world.getBlockState(blockPos);
            Block block = state.getBlock();
            if (block != null && block.getMaterial(state) != Material.AIR) {
                Set<BlockPos> coordinates = findSuitableBlocks(wand, world, mouseOver.sideHit, blockPos);
                renderOutlines(evt, player, coordinates, 200, 230, 180);
            }
        }
    }

    private Set<BlockPos> findSuitableBlocks(ItemStack stack, World world, EnumFacing sideHit, BlockPos pos) {
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
