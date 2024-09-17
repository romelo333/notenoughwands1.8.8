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
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import romelo333.notenoughwands.modules.buildingwands.BuildingWandsConfiguration;
import romelo333.notenoughwands.modules.buildingwands.BuildingWandsModule;
import romelo333.notenoughwands.modules.buildingwands.data.SwappingWandData;
import romelo333.notenoughwands.modules.protectionwand.ProtectedBlocks;
import romelo333.notenoughwands.modules.wands.Items.GenericWand;
import romelo333.notenoughwands.varia.Tools;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static mcjty.lib.builder.TooltipBuilder.*;

public class SwappingWand extends GenericWand {

    private final TooltipBuilder tooltipBuilder = new TooltipBuilder()
            .info(key("message.notenoughwands.shiftmessage"))
            .infoShift(header(), gold(),
                parameter("mode", stack -> getMode(stack).getDescription()));


    public SwappingWand() {
        super();
        this.usageFactor(1.0f);
    }

    @Override
    public void toggleMode(Player player, ItemStack stack) {
        SwappingWandData.Mode mode = getMode(stack).next();
        Tools.notify(player, ComponentFactory.literal("Switched to " + mode.getDescription() + " mode"));
        stack.update(BuildingWandsModule.SWAPPINGWAND_DATA, SwappingWandData.DEFAULT, data -> data.withMode(mode));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, list, flagIn);
        tooltipBuilder.makeTooltip(mcjty.lib.varia.Tools.getId(this), stack, list, flagIn);
        list.add(getBlockDescription(stack));

        showModeKeyDescription(list, "switch mode");
    }

    private Component getBlockDescription(ItemStack stack) {
        SwappingWandData data = stack.getOrDefault(BuildingWandsModule.SWAPPINGWAND_DATA, SwappingWandData.DEFAULT);
        if (data.state().isAir()) {
            return ComponentFactory.literal("No selected block").withStyle(ChatFormatting.RED);
        } else {
            if (isSwappingWithOffHand(stack)) {
                return ComponentFactory.literal("Will swap with block in offhand").withStyle(ChatFormatting.GREEN);
            } else {
                BlockState state = data.state();
                Component name = Tools.getBlockName(state.getBlock());
                return ComponentFactory.literal("Block: ").append(name).withStyle(ChatFormatting.GREEN);
            }
        }
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

        SwappingWandData data = stack.getOrDefault(BuildingWandsModule.SWAPPINGWAND_DATA, SwappingWandData.DEFAULT);
        if (data.state().isAir()) {
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
            blockState = data.state();
            hardness = data.hardness();
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
                BlockSnapshot blocksnapshot = BlockSnapshot.create(world.dimension(), world, coordinate);
                world.setBlockAndUpdate(coordinate, Blocks.AIR.defaultBlockState());
                Tools.placeStackAt(player, consumed, world, coordinate, null);

                if (EventHooks.onBlockPlace(player, blocksnapshot, Direction.UP)) {
//                    blocksnapshot.restore(true, false);   // @todo 1.21 check
                    blocksnapshot.restore(0);
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

    // @todo 1.20 correct event?
    @Override
    public void renderOverlay(RenderLevelStageEvent evt, Player player, ItemStack wand) {
        HitResult mouseOver = Minecraft.getInstance().hitResult;

        if (mouseOver instanceof BlockHitResult br) {

            Level world = player.getCommandSenderWorld();
            BlockPos blockPos = br.getBlockPos();
            BlockState state = world.getBlockState(blockPos);
            if (!state.isAir() && wand.getOrDefault(BuildingWandsModule.SWAPPINGWAND_DATA, SwappingWandData.DEFAULT).state() == state) {
                Set<BlockPos> coordinates = findSuitableBlocks(wand, world, br.getDirection(), blockPos, state);
                renderOutlines(evt, player, coordinates, 200, 230, 180);
            }
        }
    }

    private Set<BlockPos> findSuitableBlocks(ItemStack stack, Level world, Direction sideHit, BlockPos pos, BlockState centerState) {
        Set<BlockPos> coordinates = new HashSet<BlockPos>();
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
