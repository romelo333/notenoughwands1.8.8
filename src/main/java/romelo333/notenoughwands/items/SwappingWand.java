package romelo333.notenoughwands.items;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipOptions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.block.BlockItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import romelo333.notenoughwands.Config;
import romelo333.notenoughwands.Configuration;
import romelo333.notenoughwands.ProtectedBlocks;
import romelo333.notenoughwands.mcjtylib.BlockTools;
import romelo333.notenoughwands.varia.Tools;

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
        super(100);
        setup("swapping_wand").xpUsage(1).loot(5);
    }

    @Override
    public void initConfig(Configuration cfg) {
        super.initConfig(cfg, 2000, 100000, 500, 200000, 200, 500000);
        hardnessDistance = (float) cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_hardnessDistance", hardnessDistance, "How far away the hardness can be to allow swapping (100 means basically everything allowed)").getDouble();
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

    @Override
    public void addInformation(ItemStack stack, World player, List<TextComponent> list, TooltipOptions b) {
        super.addInformation(stack, player, list, b);
        CompoundTag compound = stack.getTag();
        if (compound == null) {
            list.add(new StringTextComponent(TextFormat.RED + "No selected block"));
        } else {
            if (isSwappingWithOffHand(stack)) {
                list.add(new StringTextComponent(TextFormat.GREEN + "Will swap with block in offhand"));
            } else {
                String id = compound.getString("block");
                Block block = Registry.BLOCK.get(new Identifier(id));
                if (block != Blocks.AIR) {
                    String name = Tools.getBlockName(block);
                    list.add(new StringTextComponent(TextFormat.GREEN + "Selected block: " + name));
                    list.add(new StringTextComponent(TextFormat.GREEN + "Mode: " + descriptions[compound.getInt("mode")]));
                }
            }
        }
        list.add(new StringTextComponent("Sneak right click to select a block."));
        list.add(new StringTextComponent("Right click in empty air to select 'offhand' mode."));
        list.add(new StringTextComponent("Right click on block to replace."));
        showModeKeyDescription(list, "switch mode");
    }

    private static boolean isSwappingWithOffHand(ItemStack stack) {
        CompoundTag compound = stack.getTag();
        if (compound == null) {
            return false;
        }
        return compound.containsKey("offhand");
    }

    private static void enableSwappingWithOffHand(ItemStack stack) {
        CompoundTag compound = stack.getTag();
        if (compound == null) {
            compound = new CompoundTag();
            stack.setTag(compound);
        }
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
    public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand) {
        ItemStack heldItem = playerIn.getStackInHand(hand);
        if (!heldItem.isEmpty()) {
            if (isSwappingWithOffHand(heldItem)) {
                disableSwappingWithOffHand(heldItem);
                if (worldIn.isRemote) {
                    Tools.notify(playerIn, "Switched to swapping with selected block");
                }
            } else {
                enableSwappingWithOffHand(heldItem);
                if (worldIn.isRemote) {
                    Tools.notify(playerIn, "Switched to swapping with block in offhand");
                }
            }
        }
        return super.use(worldIn, playerIn, hand);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemStack stack = context.getItemStack();
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getPos();
        Direction side = context.getFacing();
        if (!world.isRemote) {
            if (player.isSneaking()) {
                selectBlock(stack, player, world, pos);
            } else {
                placeBlock(stack, player, world, pos, side);
            }
        }
        return ActionResult.SUCCESS;
    }

    private void placeBlock(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction side) {
        if (!checkUsage(stack, player, 1.0f)) {
            return;
        }

        CompoundTag tagCompound = stack.getTag();
        if (tagCompound == null) {
            Tools.error(player, "First select a block by sneaking");
            return;
        }

        Block block;
        float hardness;

        if (isSwappingWithOffHand(stack)) {
            ItemStack off = player.getOffHandStack();
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
            // @todo fabric: durability?
//            meta = itemBlock.getDurability(off);
            BlockState s = block.getDefaultState();
            hardness = s.getHardness(world, pos);
        } else {
            String id = tagCompound.getString("block");
            block = Registry.BLOCK.get(new Identifier(id));
            hardness = tagCompound.getFloat("hardness");
        }

        BlockState oldState = world.getBlockState(pos);
        Block oldblock = oldState.getBlock();

        double cost = BlackListSettings.getBlacklistCost(oldblock);
        if (cost <= 0.001f) {
            Tools.error(player, "It is illegal to swap this block");
            return;
        }

        // @todo fabric
//        int oldmeta = oldblock.getMetaFromState(oldState);
//        float blockHardness = oldblock.getBlockHardness(oldState, world, pos);
        float blockHardness = oldblock.getHardness(oldState, world, pos);

        if (block == oldblock) {
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
                if (!player.isCreative()) {
                    ItemStack oldblockItem = oldblock.getPickStack(world, pos, oldState); // @todo fabric: getPickBlock(oldState, null, world, pos, player);
                    Tools.giveItem(world, player, pos, oldblockItem);
                }
                Tools.playSound(world, block.getSoundGroup(oldState).getStepSound(), coordinate.getX(), coordinate.getY(), coordinate.getZ(), 1.0f, 1.0f);
//                BlockSnapshot blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(world, coordinate);
                world.setBlockState(coordinate, Blocks.AIR.getDefaultState(), 3);   // @todo fabric
                BlockTools.placeStackAt(player, consumed, world, coordinate, null);

                // @todo fabric
//                if (ForgeEventFactory.onPlayerBlockPlace(player, blocksnapshot, Direction.UP, EnumHand.MAIN_HAND).isCanceled()) {
//                    blocksnapshot.restore(true, false);
//                    if (!player.capabilities.isCreativeMode) {
//                        Tools.giveItem(world, player, player.getPosition(), consumed);
//                    }
//                }

                player.container.sendContentUpdates();
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
        ItemStack item = block.getPickStack(world, pos, state); // @todo fabric: block.getPickBlock(state, null, world, pos, player);
        CompoundTag tagCompound = Tools.getTagCompound(stack);
        String name = Tools.getBlockName(block);
        if (name == null) {
            Tools.error(player, "You cannot select this block!");
        } else {
            double cost = BlackListSettings.getBlacklistCost(block);
            if (cost <= 0.001f) {
                Tools.error(player, "It is illegal to swap this block");
                return;
            }

            String id = Registry.BLOCK.getId(block).toString();
            tagCompound.putString("block", id);
            float hardness = block.getHardness(state, world, pos);
            tagCompound.putFloat("hardness", hardness);
            Tools.notify(player, "Selected block: " + name);
        }
    }

    @Override
    public void renderOverlay(PlayerEntity player, ItemStack wand, float partialTicks) {
        HitResult mouseOver = MinecraftClient.getInstance().hitResult;
        if (mouseOver != null && mouseOver.getBlockPos() != null && mouseOver.side != null) {
            BlockState state = player.getEntityWorld().getBlockState(mouseOver.getBlockPos());
            Block block = state.getBlock();
            if (block != null && block.getMaterial(state) != Material.AIR) {
//                int meta = block.getMetaFromState(state);

                String wandId = Tools.getTagCompound(wand).getString("block");
                Block wandBlock = Registry.BLOCK.get(new Identifier(wandId));
                if (wandBlock == block) {
                    return;
                }

                Set<BlockPos> coordinates = findSuitableBlocks(wand, player.getEntityWorld(), mouseOver.side, mouseOver.getBlockPos(), block);
                renderOutlines(player, coordinates, 200, 230, 180, partialTicks);
            }
        }
    }

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

    private void checkAndAddBlock(World world, int x, int y, int z, Block centerBlock, Set<BlockPos> coordinates) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() == centerBlock) {
            coordinates.add(pos);
        }
    }

    private int getMode(ItemStack stack) {
        return Tools.getTagCompound(stack).getInt("mode");
    }
}
