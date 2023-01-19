package romelo333.notenoughwands.modules.buildingwands.items;


import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.varia.ComponentFactory;
import mcjty.lib.varia.SoundTools;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.ItemHandlerHelper;
import romelo333.notenoughwands.modules.buildingwands.BuildingWandsConfiguration;
import romelo333.notenoughwands.modules.protectionwand.ProtectedBlocks;
import romelo333.notenoughwands.modules.wands.Items.GenericWand;
import romelo333.notenoughwands.varia.Tools;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static mcjty.lib.builder.TooltipBuilder.*;

public class SwappingWand extends GenericWand {

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


    public SwappingWand() {
        super();
        this.usageFactor(1.0f);
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

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flagIn) {
        super.appendHoverText(stack, world, list, flagIn);
        tooltipBuilder.makeTooltip(mcjty.lib.varia.Tools.getId(this), stack, list, flagIn);
        list.add(getBlockDescription(stack));

        showModeKeyDescription(list, "switch mode");
    }

    private Component getBlockDescription(ItemStack stack) {
        CompoundTag compound = stack.getTag();
        if (compound == null) {
            return ComponentFactory.literal("No selected block").withStyle(ChatFormatting.RED);
        } else {
            if (isSwappingWithOffHand(stack)) {
                return ComponentFactory.literal("Will swap with block in offhand").withStyle(ChatFormatting.GREEN);
            } else {
                BlockState state = NbtUtils.readBlockState(compound.getCompound("block"));
                Component name = Tools.getBlockName(state.getBlock());
                return ComponentFactory.literal("Block: ").append(name).withStyle(ChatFormatting.GREEN);
            }
        }
    }

    private static boolean isSwappingWithOffHand(ItemStack stack) {
        CompoundTag compound = stack.getTag();
        if (compound == null) {
            return false;
        }
        return compound.contains("offhand");
    }

    private static void enableSwappingWithOffHand(ItemStack stack) {
        CompoundTag compound = stack.getOrCreateTag();
        compound.putBoolean("offhand", true);
    }

    private static void disableSwappingWithOffHand(ItemStack stack) {
        CompoundTag compound = stack.getTag();
        if (compound == null) {
            return;
        }
        compound.remove("offhand");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (!heldItem.isEmpty()) {
            if (isSwappingWithOffHand(heldItem)) {
                disableSwappingWithOffHand(heldItem);
                if (world.isClientSide) {
                    Tools.notify(player, ComponentFactory.literal("Switched to swapping with selected block"));
                }
            } else {
                enableSwappingWithOffHand(heldItem);
                if (world.isClientSide) {
                    Tools.notify(player, ComponentFactory.literal("Switched to swapping with block in offhand"));
                }
            }
        }
        return super.use(world, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction side = context.getClickedFace();
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            if (player.isShiftKeyDown()) {
                selectBlock(stack, player, world, pos);
            } else {
                placeBlock(stack, player, world, pos, side);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private void placeBlock(ItemStack stack, Player player, Level world, BlockPos pos, Direction side) {
        if (!checkUsage(stack, player, 1.0f)) {
            return;
        }

        CompoundTag tagCompound = stack.getTag();
        if (tagCompound == null) {
            Tools.error(player, "First select a block by sneaking");
            return;
        }

        BlockState blockState;
        float hardness;

        if (isSwappingWithOffHand(stack)) {
            ItemStack off = player.getOffhandItem();
            if (off.isEmpty()) {
                Tools.error(player, "You need to hold a block in your offhand!");
                return;
            }
            if (!(off.getItem() instanceof BlockItem itemBlock)) {
                Tools.error(player, "The item in your offhand cannot be placed!");
                return;
            }
            blockState = itemBlock.getBlock().defaultBlockState();    // @todo 1.15 is this right?
            hardness = blockState.getDestroySpeed(world, pos);
        } else {
            blockState = NbtUtils.readBlockState(tagCompound.getCompound("block"));
            hardness = tagCompound.getFloat("hardness");
        }

        BlockState oldState = world.getBlockState(pos);
        Block oldblock = oldState.getBlock();

        double cost = BuildingWandsConfiguration.getBlockCost(oldState);
        if (cost <= 0.001f) {
            Tools.error(player, "It is illegal to swap this block");
            return;
        }

        float blockHardness = oldState.getDestroySpeed(world, pos);

        if (blockState == oldState) {
            // The same, nothing happens.
            return;
        }

        if (blockHardness < -0.1f) {
            Tools.error(player, "This block cannot be swapped!");
            return;
        }

        if ((!player.isCreative()) && Math.abs(hardness-blockHardness) >= BuildingWandsConfiguration.hardnessDistance.get()) {
            Tools.error(player, "The hardness of this blocks differs too much to swap!");
            return;
        }

        ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
        if (protectedBlocks.isProtected(world, pos)) {
            Tools.error(player, "This block is protected. You cannot replace it!");
            return;
        }

        Set<BlockPos> coordinates = findSuitableBlocks(stack, world, side, pos, oldState);
        boolean notenough = false;
        for (BlockPos coordinate : coordinates) {
            if (!checkUsage(stack, player, 1.0f)) {
                return;
            }
            HitResult result = new BlockHitResult(new Vec3(0, 0, 0), Direction.UP, coordinate, false);
            ItemStack pickBlock = blockState.getCloneItemStack(result, world, coordinate, player);
            ItemStack consumed = Tools.consumeInventoryItem(pickBlock, player.getInventory(), player);
            if (!consumed.isEmpty()) {
                if (!player.isCreative()) {
                    ItemStack oldblockItem = oldblock.getCloneItemStack(oldState, null, world, pos, player);
                    ItemHandlerHelper.giveItemToPlayer(player, oldblockItem);
                }
                SoundTools.playSound(world, blockState.getSoundType().getStepSound(), coordinate.getX(), coordinate.getY(), coordinate.getZ(), 1.0f, 1.0f);
                BlockSnapshot blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.create(world.dimension(), world, coordinate);
                world.setBlockAndUpdate(coordinate, Blocks.AIR.defaultBlockState());
                Tools.placeStackAt(player, consumed, world, coordinate, null);

                if (ForgeEventFactory.onBlockPlace(player, blocksnapshot, Direction.UP)) {
                    blocksnapshot.restore(true, false);
                    if (!player.isCreative()) {
                        ItemHandlerHelper.giveItemToPlayer(player, consumed);
                    }
                }

                player.containerMenu.broadcastChanges();
                registerUsage(stack, player, 1.0f);
            } else {
                notenough = true;
            }
        }
        if (notenough) {
            Tools.error(player, "You don't have the right block");
        }
    }

    private void selectBlock(ItemStack stack, Player player, Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        ItemStack item = block.getCloneItemStack(state, null, world, pos, player);   // @todo 1.15 check?
        CompoundTag tagCompound = stack.getOrCreateTag();
        Component name = Tools.getBlockName(block);
        if (name == null) {
            Tools.error(player, "You cannot select this block!");
        } else {
            double cost = BuildingWandsConfiguration.getBlockCost(state);
            if (cost <= 0.001f) {
                Tools.error(player, "It is illegal to swap this block");
                return;
            }

            tagCompound.put("block", NbtUtils.writeBlockState(state));
            float hardness = state.getDestroySpeed(world, pos);
            tagCompound.putFloat("hardness", hardness);
            Tools.notify(player, ComponentFactory.literal("Selected block: ").append(name));
        }
    }

    @Override
    public void renderOverlay(RenderLevelLastEvent evt, Player player, ItemStack wand) {
        HitResult mouseOver = Minecraft.getInstance().hitResult;

        if (mouseOver instanceof BlockHitResult br) {

            Level world = player.getCommandSenderWorld();
            BlockPos blockPos = br.getBlockPos();
            BlockState state = world.getBlockState(blockPos);
            if (!state.isAir() && wand.hasTag()) {
                BlockState wandState = NbtUtils.readBlockState(wand.getTag().getCompound("block"));
                if (wandState == state) {
                    return;
                }
                Set<BlockPos> coordinates = findSuitableBlocks(wand, world, br.getDirection(), blockPos, state);
                renderOutlines(evt, player, coordinates, 200, 230, 180);
            }
        }
    }

    private Set<BlockPos> findSuitableBlocks(ItemStack stack, Level world, Direction sideHit, BlockPos pos, BlockState centerState) {
        Set<BlockPos> coordinates = new HashSet<BlockPos>();
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
                        checkAndAddBlock(world, dx, y, dz, centerState, coordinates);
                    }
                }
                break;
            case SOUTH:
            case NORTH:
                for (int dx = x - dim; dx <= x + dim; dx++) {
                    for (int dy = y - dim; dy <= y + dim; dy++) {
                        checkAndAddBlock(world, dx, dy, z, centerState, coordinates);
                    }
                }
                break;
            case EAST:
            case WEST:
                for (int dy = y - dim; dy <= y + dim; dy++) {
                    for (int dz = z - dim; dz <= z + dim; dz++) {
                        checkAndAddBlock(world, x, dy, dz, centerState, coordinates);
                    }
                }
                break;
        }

        return coordinates;
    }

    private void checkAndAddBlock(Level world, int x, int y, int z, BlockState centerBlock, Set<BlockPos> coordinates) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockState state = world.getBlockState(pos);
        if (state == centerBlock) {
            coordinates.add(pos);
        }
    }

    private int getMode(ItemStack stack) {
        return stack.getOrCreateTag().getInt("mode");
    }
}
