package romelo333.notenoughwands.Items;


import mcjty.lib.varia.BlockTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;
import romelo333.notenoughwands.ConfigSetup;
import romelo333.notenoughwands.ProtectedBlocks;
import romelo333.notenoughwands.setup.Configuration;
import romelo333.notenoughwands.varia.Tools;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SwappingWand extends GenericWand {

    public static final int MODE_FIRST = 0;
    public static final int MODE_3X3 = 0;
    public static final int MODE_5X5 = 1;
    public static final int MODE_7X7 = 2;
    public static final int MODE_SINGLE = 3;
    public static final int MODE_LAST = MODE_SINGLE;

    private float hardnessDistance = 35.0f;

    public static final String[] descriptions = new String[] {
        "3x3", "5x5", "7x7", "single"
    };

    public SwappingWand() {
        setup().xpUsage(1).loot(5);
    }

    @Override
    public void initConfig(Configuration cfg) {
        super.initConfig(cfg, 2000, 100000, 500, 200000, 200, 500000);
        hardnessDistance = (float) cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_hardnessDistance", (int) hardnessDistance, "How far away the hardness can be to allow swapping (100 means basically everything allowed)").getDouble();
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

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flagIn) {
        super.addInformation(stack, world, list, flagIn);
        CompoundNBT compound = stack.getTag();
        if (compound == null) {
            // @todo 1.15 proper tooltips
            list.add(new StringTextComponent(TextFormatting.RED + "No selected block"));
        } else {
            if (isSwappingWithOffHand(stack)) {
                list.add(new StringTextComponent(TextFormatting.GREEN + "Will swap with block in offhand"));
            } else {
                // @todo 1.15 need to preserve blockstate?
                String id = compound.getString("block");
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(id));
                if (block != Blocks.AIR) {
                    String name = Tools.getBlockName(block);
                    list.add(new StringTextComponent(TextFormatting.GREEN + "Selected block: " + name));
                    list.add(new StringTextComponent(TextFormatting.GREEN + "Mode: " + descriptions[compound.getInt("mode")]));
                }
            }
        }
        list.add(new StringTextComponent("Sneak right click to select a block."));
        list.add(new StringTextComponent("Right click in empty air to select 'offhand' mode."));
        list.add(new StringTextComponent("Right click on block to replace."));
        showModeKeyDescription(list, "switch mode");
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
                    Tools.notify(player, "Switched to swapping with selected block");
                }
            } else {
                enableSwappingWithOffHand(heldItem);
                if (world.isRemote) {
                    Tools.notify(player, "Switched to swapping with block in offhand");
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

        Block block;
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
            block = itemBlock.getBlock();
            BlockState s = block.getDefaultState(); // @todo 1.15 is this right?
            hardness = s.getBlockHardness(world, pos);
        } else {
            String id = tagCompound.getString("block");
            block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(id));
            hardness = tagCompound.getFloat("hardness");
        }

        BlockState oldState = world.getBlockState(pos);
        Block oldblock = oldState.getBlock();

        double cost = BlackListSettings.getBlacklistCost(oldblock);
        if (cost <= 0.001f) {
            Tools.error(player, "It is illegal to swap this block");
            return;
        }

        float blockHardness = oldblock.getBlockHardness(oldState, world, pos);

        if (block == oldblock) {    // @todo 1.15 compare blockstates
            // The same, nothing happens.
            return;
        }

        if (blockHardness < -0.1f) {
            Tools.error(player, "This block cannot be swapped!");
            return;
        }

        if ((!player.isCreative()) && Math.abs(hardness-blockHardness) >= hardnessDistance) {
            Tools.error(player, "The hardness of this blocks differs too much to swap!");
            return;
        }

        ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
        if (protectedBlocks.isProtected(world, pos)) {
            Tools.error(player, "This block is protected. You cannot replace it!");
            return;
        }

        Set<BlockPos> coordinates = findSuitableBlocks(stack, world, side, pos, oldblock);
        boolean notenough = false;
        for (BlockPos coordinate : coordinates) {
            if (!checkUsage(stack, player, 1.0f)) {
                return;
            }
            ItemStack consumed = Tools.consumeInventoryItem(Item.getItemFromBlock(block), player.inventory, player);
            if (!consumed.isEmpty()) {
                if (!player.abilities.isCreativeMode) {
                    ItemStack oldblockItem = oldblock.getPickBlock(oldState, null, world, pos, player);
                    ItemHandlerHelper.giveItemToPlayer(player, oldblockItem);
                }
                Tools.playSound(world, block.getSoundType(block.getDefaultState()).getStepSound(), coordinate.getX(), coordinate.getY(), coordinate.getZ(), 1.0f, 1.0f);
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
        ItemStack item = block.getPickBlock(state, null, world, pos, player);
        CompoundNBT tagCompound = stack.getOrCreateTag();
        String name = Tools.getBlockName(block);    // @todo 1.15 need to do blockstate
        if (name == null) {
            Tools.error(player, "You cannot select this block!");
        } else {
            double cost = BlackListSettings.getBlacklistCost(block);
            if (cost <= 0.001f) {
                Tools.error(player, "It is illegal to swap this block");
                return;
            }

            tagCompound.putString("block", block.getRegistryName().toString()); // @todo 1.15 need to store blockstate
            float hardness = block.getBlockHardness(state, world, pos);
            tagCompound.putFloat("hardness", hardness);
            Tools.notify(player, "Selected block: " + name);
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
    private Set<BlockPos> findSuitableBlocks(ItemStack stack, World world, Direction sideHit, BlockPos pos, Block centerBlock) {
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
                        checkAndAddBlock(world, dx, y, dz, centerBlock, coordinates);
                    }
                }
                break;
            case SOUTH:
            case NORTH:
                for (int dx = x - dim; dx <= x + dim; dx++) {
                    for (int dy = y - dim; dy <= y + dim; dy++) {
                        checkAndAddBlock(world, dx, dy, z, centerBlock, coordinates);
                    }
                }
                break;
            case EAST:
            case WEST:
                for (int dy = y - dim; dy <= y + dim; dy++) {
                    for (int dz = z - dim; dz <= z + dim; dz++) {
                        checkAndAddBlock(world, x, dy, dz, centerBlock, coordinates);
                    }
                }
                break;
        }

        return coordinates;
    }

    // @todo 1.15 blockstate instead of block
    private void checkAndAddBlock(World world, int x, int y, int z, Block centerBlock, Set<BlockPos> coordinates) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() == centerBlock) {  // @todo 1.15 compare blockstate
            coordinates.add(pos);
        }
    }

    private int getMode(ItemStack stack) {
        return stack.getOrCreateTag().getInt("mode");
    }
}
