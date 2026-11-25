package romelo333.notenoughwands.modules.buildingwands.items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.varia.ComponentFactory;
import mcjty.lib.varia.SoundTools;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.EventHooks;
import romelo333.notenoughwands.modules.buildingwands.BuildingWandsConfiguration;
import romelo333.notenoughwands.modules.buildingwands.BuildingWandsModule;
import romelo333.notenoughwands.modules.buildingwands.data.SwappingWandData;
import romelo333.notenoughwands.modules.protectionwand.ProtectedBlocks;
import romelo333.notenoughwands.modules.wands.Items.GenericWand;
import romelo333.notenoughwands.varia.ClientTools.RenderContext;
import romelo333.notenoughwands.varia.ClientTools;
import romelo333.notenoughwands.varia.Tools;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;

import static mcjty.lib.builder.TooltipBuilder.*;

public class SwappingWand extends GenericWand {

    private final TooltipBuilder tooltipBuilder = new TooltipBuilder()
            .info(key("message.notenoughwands.shiftmessage"))
            .infoShift(header(), gold(),
                parameter("mode", stack -> getMode(stack).getDescription()));


    public SwappingWand(Properties properties) {
        super(properties);
        this.usageFactor(1.0f);
    }

    @Override
    public void toggleMode(Player player, ItemStack stack) {
        SwappingWandData.Mode mode = getMode(stack).next();
        Tools.notify(player, ComponentFactory.literal("Switched to " + mode.getDescription() + " mode"));
        stack.update(BuildingWandsModule.SWAPPINGWAND_DATA, SwappingWandData.DEFAULT, data -> data.withMode(mode));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> list, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, display, list, flagIn);
        Tools.appendTooltip(tooltipBuilder, mcjty.lib.varia.Tools.getId(this), stack, list, flagIn);
        list.accept(getBlockDescription(stack));

        showModeKeyDescription(list, "switch mode");
    }

    private Component getBlockDescription(ItemStack stack) {
        if (isSwappingWithOffHand(stack)) {
            return ComponentFactory.literal("Will swap with block in offhand").withStyle(ChatFormatting.GREEN);
        }
        SwappingWandData data = stack.getOrDefault(BuildingWandsModule.SWAPPINGWAND_DATA, SwappingWandData.DEFAULT);
        BlockState state = data.state();
        if (state.isAir()) {
            return ComponentFactory.literal("No selected block").withStyle(ChatFormatting.RED);
        }
        return ComponentFactory.literal("Block: ").append(Tools.getBlockName(state.getBlock())).withStyle(ChatFormatting.GREEN);
    }

    private static boolean isSwappingWithOffHand(ItemStack stack) {
        return stack.getOrDefault(BuildingWandsModule.SWAPPINGWAND_DATA, SwappingWandData.DEFAULT).offhand();
    }

    private static void enableSwappingWithOffHand(ItemStack stack) {
        stack.update(BuildingWandsModule.SWAPPINGWAND_DATA, SwappingWandData.DEFAULT, data -> data.withOffhand(true));
    }

    private static void disableSwappingWithOffHand(ItemStack stack) {
        stack.update(BuildingWandsModule.SWAPPINGWAND_DATA, SwappingWandData.DEFAULT, data -> data.withOffhand(false));
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (!heldItem.isEmpty()) {
            if (isSwappingWithOffHand(heldItem)) {
                disableSwappingWithOffHand(heldItem);
                if (world.isClientSide()) {
                    Tools.notify(player, ComponentFactory.literal("Switched to swapping with selected block"));
                }
            } else {
                enableSwappingWithOffHand(heldItem);
                if (world.isClientSide()) {
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
        if (!world.isClientSide()) {
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

        BlockState blockState = getReplacementState(stack, player, world);
        if (blockState.isAir()) {
            if (!isSwappingWithOffHand(stack)) {
                Tools.error(player, "First select a block by sneaking");
            } else if (player.getOffhandItem().isEmpty()) {
                Tools.error(player, "You need to hold a block in your offhand!");
            } else {
                Tools.error(player, "The item in your offhand cannot be placed!");
            }
            return;
        }

        BlockState oldState = world.getBlockState(pos);
        if (blockState == oldState) {
            return;
        }
        String error = getSwapError(stack, player, world, pos, oldState, blockState);
        if (error != null) {
            Tools.error(player, error);
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
            if (protectedBlocks.isProtected(world, coordinate)) {
                continue;
            }
            if (!checkUsage(stack, player, 1.0f)) {
                return;
            }
            ItemStack pickBlock = Tools.pickBlock(blockState, world, coordinate, player);
            ItemStack consumed = Tools.consumeInventoryItem(pickBlock, player.getInventory(), player);
            if (!consumed.isEmpty()) {
                ItemStack replaced = player.isCreative() ? ItemStack.EMPTY : Tools.pickBlock(oldState, world, coordinate, player);
                ItemStack refund = consumed.copy();
                SoundTools.playSound(world, blockState.getSoundType().getStepSound(), coordinate.getX(), coordinate.getY(), coordinate.getZ(), 1.0f, 1.0f);
                BlockSnapshot blocksnapshot = BlockSnapshot.create(world.dimension(), world, coordinate);
                world.setBlockAndUpdate(coordinate, Blocks.AIR.defaultBlockState());
                Tools.placeStackAt(player, consumed, world, coordinate, null);

                if (EventHooks.onBlockPlace(player, blocksnapshot, Direction.UP)) {
//                    blocksnapshot.restore(true, false);   // @todo 1.21 check
                    blocksnapshot.restore(0);
                    if (!player.isCreative()) {
                        Tools.giveItem(player, refund);
                    }
                    continue;
                }

                Tools.giveItem(player, replaced);

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
//        ItemStack item = block.getCloneItemStack(state, null, world, pos, player);   // @todo 1.15 check?
        Component name = Tools.getBlockName(block);
        if (name == null) {
            Tools.error(player, "You cannot select this block!");
        } else {
            double cost = BuildingWandsConfiguration.getBlockCost(state);
            if (cost <= 0.001f) {
                Tools.error(player, "It is illegal to swap this block");
                return;
            }

            float hardness = state.getDestroySpeed(world, pos);
            stack.set(BuildingWandsModule.SWAPPINGWAND_DATA, new SwappingWandData(getMode(stack), state, false, hardness));
            Tools.notify(player, ComponentFactory.literal("Selected block: ").append(name));
        }
    }

    private BlockState getReplacementState(ItemStack stack, Player player, Level world) {
        if (isSwappingWithOffHand(stack)) {
            ItemStack offhand = player.getOffhandItem();
            return offhand.getItem() instanceof BlockItem item ? item.getBlock().defaultBlockState() : Blocks.AIR.defaultBlockState();
        }
        return stack.getOrDefault(BuildingWandsModule.SWAPPINGWAND_DATA, SwappingWandData.DEFAULT).state();
    }

    @Nullable
    private String getSwapError(ItemStack stack, Player player, Level world, BlockPos pos, BlockState oldState, BlockState replacement) {
        if (BuildingWandsConfiguration.getBlockCost(oldState) <= 0.001f) {
            return "It is illegal to swap this block";
        }
        float oldHardness = oldState.getDestroySpeed(world, pos);
        if (oldHardness < -0.1f) {
            return "This block cannot be swapped!";
        }
        if (!player.isCreative()) {
            float hardness = isSwappingWithOffHand(stack) ? replacement.getDestroySpeed(world, pos) : stack.getOrDefault(BuildingWandsModule.SWAPPINGWAND_DATA, SwappingWandData.DEFAULT).hardness();
            if (Math.abs(hardness - oldHardness) >= BuildingWandsConfiguration.hardnessDistance.get()) {
                return "The hardness of this blocks differs too much to swap!";
            }
        }
        return null;
    }

    @Override
    public void renderOverlay(RenderContext context, Player player, ItemStack wand) {
        if (player.isShiftKeyDown() || !(Minecraft.getInstance().hitResult instanceof BlockHitResult hit)
                || hit.getType() != HitResult.Type.BLOCK) {
            return;
        }

        Level world = player.level();
        BlockPos pos = hit.getBlockPos();
        BlockState state = world.getBlockState(pos);
        BlockState replacement = getReplacementState(wand, player, world);
        if (state.isAir() || replacement.isAir() || state == replacement
                || getSwapError(wand, player, world, pos, state, replacement) != null
                || ProtectedBlocks.isProtectedClientSide(world, pos)) {
            return;
        }
        Set<BlockPos> coordinates = findSuitableBlocks(wand, world, hit.getDirection(), pos, state);
        ClientTools.renderPlacementPreview(context, player, coordinates,
                destination -> Tools.pickBlock(replacement, world, destination, player), true);
        renderOutlines(context, coordinates, 200, 230, 180);
    }

    private Set<BlockPos> findSuitableBlocks(ItemStack stack, Level world, Direction sideHit, BlockPos pos, BlockState centerState) {
        Set<BlockPos> coordinates = new LinkedHashSet<>();
        SwappingWandData.Mode mode = getMode(stack);
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

    private SwappingWandData.Mode getMode(ItemStack stack) {
        return stack.getOrDefault(BuildingWandsModule.SWAPPINGWAND_DATA, SwappingWandData.DEFAULT).mode();
    }
}
