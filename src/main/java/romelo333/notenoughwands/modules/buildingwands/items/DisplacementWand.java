package romelo333.notenoughwands.modules.buildingwands.items;


import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.varia.ComponentFactory;
import mcjty.lib.varia.SoundTools;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.RenderLevelLastEvent;
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
        super();
        this.usageFactor(1.0f);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> list, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, list, flagIn);
        tooltipBuilder.makeTooltip(mcjty.lib.varia.Tools.getId(this), stack, list, flagIn);

        showModeKeyDescription(list, "switch mode");
    }

    @Override
    public void toggleMode(Player player, ItemStack stack) {
        int mode = getMode(stack);
        mode++;
        if (mode > MODE_LAST) {
            mode = MODE_FIRST;
        }
        Tools.notify(player, ComponentFactory.literal("Switched to " + DESCRIPTIONS[mode] + " mode"));
        stack.getOrCreateTag().putInt("mode", mode);
    }

    private int getMode(ItemStack stack) {
        return stack.getOrCreateTag().getInt("mode");
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level world = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        Direction side = context.getClickedFace();
        if (!world.isClientSide) {
            if (player.isShiftKeyDown()) {
                pullBlocks(stack, player, world, pos, side);
            } else {
                pushBlocks(stack, player, world, pos, side);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private void pullBlocks(ItemStack stack, Player player, Level world, BlockPos pos, Direction side) {
        if (!checkUsage(stack, player, 1.0f)) {
            return;
        }
        Set<BlockPos> coordinates = findSuitableBlocks(stack, world, side, pos);
        int cnt = moveBlocks(player, world, coordinates, side);
        if (cnt > 0) {
            registerUsage(stack, player, 1.0f);
        }
    }

    private void pushBlocks(ItemStack stack, Player player, Level world, BlockPos pos, Direction side) {
        if (!checkUsage(stack, player, 1.0f)) {
            return;
        }
        Set<BlockPos> coordinates = findSuitableBlocks(stack, world, side, pos);
        int cnt = moveBlocks(player, world, coordinates, side.getOpposite());
        if (cnt > 0) {
            registerUsage(stack, player, 1.0f);
        }
    }

    private int moveBlocks(Player player, Level world, Set<BlockPos> coordinates, Direction direction) {
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
                    BlockEntity tileEntity = world.getBlockEntity(coordinate);
                    CompoundTag tc = null;
                    if (tileEntity != null) {
                        tc = tileEntity.saveWithoutMetadata();
                        world.removeBlockEntity(coordinate);
                    }

                    world.setBlock(coordinate, Blocks.AIR.defaultBlockState(), 0);

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
                        //TODO otherC moved from setBlockEntity to loadStatic
                        tileEntity = world.getBlockEntity(otherC);
                        if (tileEntity != null) {
                            tileEntity.load(tc);
                            tileEntity.setChanged();
                            world.sendBlockUpdated(otherC, blockState, blockState, 3); // @todo 1.15 constants
                        }
                    }
                }
            }
        }
        return cnt;
    }

    @Override
    public void renderOverlay(RenderLevelLastEvent evt, Player player, ItemStack wand) {
        HitResult mouseOver = Minecraft.getInstance().hitResult;

        if (mouseOver instanceof BlockHitResult br) {

            Level world = player.getCommandSenderWorld();
            BlockPos blockPos = br.getBlockPos();
            BlockState state = world.getBlockState(blockPos);
            if (!state.isAir()) {
                Set<BlockPos> coordinates = findSuitableBlocks(wand, world, br.getDirection(), blockPos);
                renderOutlines(evt, player, coordinates, 200, 230, 180);
            }
        }
    }

    private Set<BlockPos> findSuitableBlocks(ItemStack stack, Level world, Direction sideHit, BlockPos pos) {
        Set<BlockPos> coordinates = new HashSet<>();
        int mode = getMode(stack);
        int dim = 0;
        switch (mode) {
            case MODE_SINGLE -> {
                coordinates.add(pos);
                return coordinates;
            }
            case MODE_3X3 -> dim = 1;
            case MODE_5X5 -> dim = 2;
            case MODE_7X7 -> dim = 3;
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

    private void checkAndAddBlock(Level world, int x, int y, int z, Set<BlockPos> coordinates) {
        BlockPos pos = new BlockPos(x, y, z);
        if (!world.isEmptyBlock(pos)) {
            coordinates.add(pos);
        }
    }
}
