package romelo333.notenoughwands.modules.buildingwands.items;


import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.varia.BlockTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.ItemHandlerHelper;
import romelo333.notenoughwands.modules.buildingwands.BlackListSettings;
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
        setup().loot(5).usageFactory(1.0f);
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

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flagIn) {
        super.addInformation(stack, world, list, flagIn);
        tooltipBuilder.makeTooltip(getRegistryName(), stack, list, flagIn);
        list.add(getBlockDescription(stack));

        // @todo 1.15
        showModeKeyDescription(list, "switch mode");
    }

    private ITextComponent getBlockDescription(ItemStack stack) {
        CompoundNBT compound = stack.getTag();
        if (compound == null) {
            return new StringTextComponent("No selected block").applyTextStyle(TextFormatting.RED);
        } else {
            if (isSwappingWithOffHand(stack)) {
                return new StringTextComponent("Will swap with block in offhand").applyTextStyle(TextFormatting.GREEN);
            } else {
                BlockState state = NBTUtil.readBlockState(compound.getCompound("block"));
                ITextComponent name = Tools.getBlockName(state.getBlock());
                return new StringTextComponent("Block: ").appendSibling(name).applyTextStyle(TextFormatting.GREEN);
            }
        }
    }

    private static boolean isSwappingWithOffHand(ItemStack stack) {
        CompoundNBT compound = stack.getTag();
        if (compound == null) {
            return false;
        }
        return compound.contains("offhand");
    }

    private static void enableSwappingWithOffHand(ItemStack stack) {
        CompoundNBT compound = stack.getOrCreateTag();
        compound.putBoolean("offhand", true);
    }

    private static void disableSwappingWithOffHand(ItemStack stack) {
        CompoundNBT compound = stack.getTag();
        if (compound == null) {
            return;
        }
        compound.remove("offhand");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack heldItem = player.getHeldItem(hand);
        if (!heldItem.isEmpty()) {
            if (isSwappingWithOffHand(heldItem)) {
                disableSwappingWithOffHand(heldItem);
                if (world.isRemote) {
                    Tools.notify(player, new StringTextComponent("Switched to swapping with selected block"));
                }
            } else {
                enableSwappingWithOffHand(heldItem);
                if (world.isRemote) {
                    Tools.notify(player, new StringTextComponent("Switched to swapping with block in offhand"));
                }
            }
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        Direction side = context.getFace();
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            if (player.isSneaking()) {
                selectBlock(stack, player, world, pos);
            } else {
                placeBlock(stack, player, world, pos, side);
            }
        }
        return ActionResultType.SUCCESS;
    }

    private void placeBlock(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction side) {
        if (!checkUsage(stack, player, 1.0f)) {
            return;
        }

        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound == null) {
            Tools.error(player, "First select a block by sneaking");
            return;
        }

        BlockState blockState;
        float hardness;

        if (isSwappingWithOffHand(stack)) {
            ItemStack off = player.getHeldItemOffhand();
            if (off.isEmpty()) {
                Tools.error(player, "You need to hold a block in your offhand!");
                return;
            }
            if (!(off.getItem() instanceof BlockItem)) {
                Tools.error(player, "The item in your offhand cannot be placed!");
                return;
            }
            BlockItem itemBlock = (BlockItem) off.getItem();
            blockState = itemBlock.getBlock().getDefaultState();    // @todo 1.15 is this right?
            hardness = blockState.getBlockHardness(world, pos);
        } else {
            blockState = NBTUtil.readBlockState(tagCompound.getCompound("block"));
            hardness = tagCompound.getFloat("hardness");
        }

        BlockState oldState = world.getBlockState(pos);
        Block oldblock = oldState.getBlock();

        double cost = BlackListSettings.getBlacklistCost(oldState);
        if (cost <= 0.001f) {
            Tools.error(player, "It is illegal to swap this block");
            return;
        }

        float blockHardness = oldState.getBlockHardness(world, pos);

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
            RayTraceResult result = new BlockRayTraceResult(new Vec3d(0, 0, 0), Direction.UP, coordinate, false);
            ItemStack pickBlock = blockState.getPickBlock(result, world, coordinate, player);
            ItemStack consumed = Tools.consumeInventoryItem(pickBlock, player.inventory, player);
            if (!consumed.isEmpty()) {
                if (!player.abilities.isCreativeMode) {
                    ItemStack oldblockItem = oldblock.getPickBlock(oldState, null, world, pos, player);
                    ItemHandlerHelper.giveItemToPlayer(player, oldblockItem);
                }
                Tools.playSound(world, blockState.getSoundType().getStepSound(), coordinate.getX(), coordinate.getY(), coordinate.getZ(), 1.0f, 1.0f);
                BlockSnapshot blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(world, coordinate);
                world.setBlockState(coordinate, Blocks.AIR.getDefaultState());
                BlockTools.placeStackAt(player, consumed, world, coordinate, null);

                if (ForgeEventFactory.onBlockPlace(player, blocksnapshot, Direction.UP)) {
                    blocksnapshot.restore(true, false);
                    if (!player.abilities.isCreativeMode) {
                        ItemHandlerHelper.giveItemToPlayer(player, consumed);
                    }
                }

                player.openContainer.detectAndSendChanges();
                registerUsage(stack, player, 1.0f);
            } else {
                notenough = true;
            }
        }
        if (notenough) {
            Tools.error(player, "You don't have the right block");
        }
    }

    private void selectBlock(ItemStack stack, PlayerEntity player, World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        ItemStack item = block.getPickBlock(state, null, world, pos, player);   // @todo 1.15 check?
        CompoundNBT tagCompound = stack.getOrCreateTag();
        ITextComponent name = Tools.getBlockName(block);
        if (name == null) {
            Tools.error(player, "You cannot select this block!");
        } else {
            double cost = BlackListSettings.getBlacklistCost(state);
            if (cost <= 0.001f) {
                Tools.error(player, "It is illegal to swap this block");
                return;
            }

            tagCompound.put("block", NBTUtil.writeBlockState(state));
            float hardness = state.getBlockHardness(world, pos);
            tagCompound.putFloat("hardness", hardness);
            Tools.notify(player, new StringTextComponent("Selected block: ").appendSibling(name));
        }
    }

    @Override
    public void renderOverlay(RenderWorldLastEvent evt, PlayerEntity player, ItemStack wand) {
        // @todo 1.15
//        RayTraceResult mouseOver = Minecraft.getMinecraft().objectMouseOver;
//        if (mouseOver != null && mouseOver.getBlockPos() != null && mouseOver.sideHit != null) {
//            BlockState state = player.getEntityWorld().getBlockState(mouseOver.getBlockPos());
//            Block block = state.getBlock();
//            if (block != null && block.getMaterial(state) != Material.AIR) {
//                int meta = block.getMetaFromState(state);
//
//                int wandId = Tools.getTagCompound(wand).getInt("block");
//                Block wandBlock = Block.REGISTRY.getObjectById(wandId);
//                int wandMeta = Tools.getTagCompound(wand).getInt("meta");
//                if (wandBlock == block && wandMeta == meta) {
//                    return;
//                }
//
//                Set<BlockPos> coordinates = findSuitableBlocks(wand, player.getEntityWorld(), mouseOver.sideHit, mouseOver.getBlockPos(), block, meta);
//                renderOutlines(evt, player, coordinates, 200, 230, 180);
//            }
//        }
    }

    // @todo 1.15 needs to do blockstate instead of block
    private Set<BlockPos> findSuitableBlocks(ItemStack stack, World world, Direction sideHit, BlockPos pos, BlockState centerState) {
        Set<BlockPos> coordinates = new HashSet<BlockPos>();
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

    private void checkAndAddBlock(World world, int x, int y, int z, BlockState centerBlock, Set<BlockPos> coordinates) {
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
