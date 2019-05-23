package romelo333.notenoughwands.items;


import net.minecraft.ChatFormat;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import romelo333.notenoughwands.Config;
import romelo333.notenoughwands.Configuration;
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
        super(100);
        setup("displacement_wand").xpUsage(1).loot(3);
    }

    @Override
    public void initConfig(Configuration cfg) {
        super.initConfig(cfg, 2000, 100000, 500, 200000, 200, 500000);
        maxHardness = (float) cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_maxHardness", maxHardness, "Max hardness this block can move.)").getDouble();
    }

    @Override
    public void buildTooltip(ItemStack stack, World player, List<Component> list, TooltipContext b) {
        super.buildTooltip(stack, player, list, b);
        list.add(new TextComponent(ChatFormat.GREEN + "Mode: " + descriptions[getMode(stack)]));
        list.add(new TextComponent("Right click to push blocks forward."));
        list.add(new TextComponent("Sneak right click to pull blocks."));
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
        Tools.getTagCompound(stack).putInt("mode", mode);
    }

    private int getMode(ItemStack stack) {
        return Tools.getTagCompound(stack).getInt("mode");
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getBlockPos();
        Direction side = context.getFacing();
        ItemStack stack = context.getItemStack();
        if (!world.isClient) {
            if (player.isSneaking()) {
                pullBlocks(stack, player, world, pos, side);
            } else {
                pushBlocks(stack, player, world, pos, side);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
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
            BlockState otherState = world.getBlockState(otherC);
            Block otherBlock = otherState.getBlock();
            if (true) { // @todo fabricotherBlock.isReplaceable(world, otherC)) {
                double cost = GenericWand.checkPickup(player, world, otherC, block, maxHardness);
                if (cost >= 0.0) {
                    cnt++;
                    Tools.playSound(world, block.getSoundGroup(state).getStepSound(), coordinate.getX(), coordinate.getY(), coordinate.getZ(), 1.0f, 1.0f);
                    BlockEntity tileEntity = world.getBlockEntity(coordinate);
                    CompoundTag tc = null;
                    if (tileEntity != null) {
                        tc = new CompoundTag();
                        tileEntity.toTag(tc);
                        world.removeBlockEntity(coordinate);
                    }
                    world.setBlockState(coordinate, Blocks.AIR.getDefaultState(), 3);   // @todo fabric

                    BlockState blockState = block.getDefaultState();
                    world.setBlockState(otherC, blockState, 3);
                    if (tc != null) {
                        tc.putInt("x", otherC.getX());
                        tc.putInt("y", otherC.getY());
                        tc.putInt("z", otherC.getZ());
                        tileEntity = BlockEntity.createFromTag(tc);
                        if (tileEntity != null) {
                            Chunk chunk = world.getChunk(otherC);
                            ((WorldChunk) chunk).addBlockEntity(tileEntity);
                            tileEntity.markDirty();
                            world.updateListeners(otherC, blockState, blockState, 3);
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

    @Override
    public void renderOverlay(PlayerEntity player, ItemStack wand, float partialTicks) {
        HitResult mouseOver = MinecraftClient.getInstance().hitResult;
        if (mouseOver != null && mouseOver.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) mouseOver;
            if (blockHit.getBlockPos() != null && blockHit.getSide() != null) {
                World world = player.getEntityWorld();
                BlockPos blockPos = blockHit.getBlockPos();
                BlockState state = world.getBlockState(blockPos);
                Block block = state.getBlock();
                if (block != null && block.getMaterial(state) != Material.AIR) {
                    Set<BlockPos> coordinates = findSuitableBlocks(wand, world, blockHit.getSide(), blockPos);
                    renderOutlines(player, coordinates, 200, 230, 180, partialTicks);
                }
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
        if (!world.isAir(pos)) {
            coordinates.add(pos);
        }
    }
}
