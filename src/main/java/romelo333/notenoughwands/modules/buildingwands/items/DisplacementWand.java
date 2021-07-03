package romelo333.notenoughwands.modules.buildingwands.items;


import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.varia.SoundTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import romelo333.notenoughwands.modules.buildingwands.BuildingWandsConfiguration;
import romelo333.notenoughwands.modules.wands.Items.GenericWand;
import romelo333.notenoughwands.varia.Tools;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static mcjty.lib.builder.TooltipBuilder.*;

public class DisplacementWand extends GenericWand {

    public static final int MODE_FIRST = 0;
    public static final int MODE_3X3 = 0;
    public static final int MODE_5X5 = 1;
    public static final int MODE_7X7 = 2;
    public static final int MODE_SINGLE = 3;
    public static final int MODE_LAST = MODE_SINGLE;

    public static final String[] DESCRIPTIONS = new String[] {
            "3x3", "5x5", "7x7", "single"
    };

    private final TooltipBuilder tooltipBuilder = new TooltipBuilder()
            .info(key("message.notenoughwands.shiftmessage"))
            .infoShift(header(), gold(),
                    parameter("mode", stack -> DESCRIPTIONS[getMode(stack)]));


    public DisplacementWand() {
        this.usageFactor(1.0f);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> list, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, list, flagIn);
        tooltipBuilder.makeTooltip(getRegistryName(), stack, list, flagIn);

        showModeKeyDescription(list, "switch mode");
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

    private int getMode(ItemStack stack) {
        return stack.getOrCreateTag().getInt("mode");
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        World world = context.getLevel();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        Direction side = context.getClickedFace();
        if (!world.isClientSide) {
            if (player.isShiftKeyDown()) {
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
            BlockPos otherC = coordinate.relative(direction);
            BlockState otherState = world.getBlockState(otherC);
            if (otherState.getMaterial().isReplaceable()) { // @todo 1.15 check if this is right?
                double cost = GenericWand.checkPickup(player, world, otherC, state, BuildingWandsConfiguration.maxHardness.get());
                if (cost >= 0.0) {
                    cnt++;
                    SoundTools.playSound(world, block.getSoundType(state).getStepSound(), coordinate.getX(), coordinate.getY(), coordinate.getZ(), 1.0f, 1.0f);
                    TileEntity tileEntity = world.getBlockEntity(coordinate);
                    CompoundNBT tc = null;
                    if (tileEntity != null) {
                        tc = new CompoundNBT();
                        tileEntity.save(tc);
                        world.removeBlockEntity(coordinate);
                    }

                    world.setBlockAndUpdate(coordinate, Blocks.AIR.defaultBlockState());

                    BlockSnapshot blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.create(world.dimension(), world, otherC);
                    BlockState blockState = block.defaultBlockState();    // @todo 1.15 blockstate
                    world.setBlock(otherC, blockState, 3);
                    if (ForgeEventFactory.onBlockPlace(player, blocksnapshot, Direction.UP)) {
                        blocksnapshot.restore(true, false);
                        world.setBlock(coordinate, blockState, 3);
                        // Make sure we restore the tileentity at the original spot
                        otherC = coordinate;
                    }

                    if (tc != null) {
                        tc.putInt("x", otherC.getX());
                        tc.putInt("y", otherC.getY());
                        tc.putInt("z", otherC.getZ());
                        tileEntity = TileEntity.loadStatic(blockState, tc);
                        if (tileEntity != null) {
                            world.getChunk(otherC).setBlockEntity(otherC, tileEntity);
                            tileEntity.setChanged();
                            world.sendBlockUpdated(otherC, blockState, blockState, 3); // @todo 1.15 constants
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
        RayTraceResult mouseOver = Minecraft.getInstance().hitResult;

        if (mouseOver instanceof BlockRayTraceResult) {
            BlockRayTraceResult br = (BlockRayTraceResult) mouseOver;

            World world = player.getCommandSenderWorld();
            BlockPos blockPos = br.getBlockPos();
            BlockState state = world.getBlockState(blockPos);
            if (!state.isAir(world, blockPos)) {
                Set<BlockPos> coordinates = findSuitableBlocks(wand, world, br.getDirection(), blockPos);
                renderOutlines(evt, player, coordinates, 200, 230, 180);
            }
        }
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
        if (!world.isEmptyBlock(pos)) {
            coordinates.add(pos);
        }
    }
}
