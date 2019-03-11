package romelo333.notenoughwands.Items;


import mcjty.lib.varia.BlockTools;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import romelo333.notenoughwands.ConfigSetup;
import romelo333.notenoughwands.ProtectedBlocks;
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
        setup("swapping_wand").xpUsage(1).loot(5);
    }

    @Override
    public void initConfig(Configuration cfg) {
        super.initConfig(cfg, 2000, 100000, 500, 200000, 200, 500000);
        hardnessDistance = (float) cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_hardnessDistance", hardnessDistance, "How far away the hardness can be to allow swapping (100 means basically everything allowed)").getDouble();
    }

    @Override
    public void toggleMode(EntityPlayer player, ItemStack stack) {
        int mode = getMode(stack);
        mode++;
        if (mode > MODE_LAST) {
            mode = MODE_FIRST;
        }
        Tools.notify(player, "Switched to " + descriptions[mode] + " mode");
        Tools.getTagCompound(stack).setInteger("mode", mode);
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<String> list, ITooltipFlag b) {
        super.addInformation(stack, player, list, b);
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null) {
            list.add(TextFormatting.RED + "No selected block");
        } else {
            if (isSwappingWithOffHand(stack)) {
                list.add(TextFormatting.GREEN + "Will swap with block in offhand");
            } else {
                int id = compound.getInteger("block");
                Block block = Block.REGISTRY.getObjectById(id);
                if (block != Blocks.AIR) {
                    int meta = compound.getInteger("meta");
                    String name = Tools.getBlockName(block, meta);
                    list.add(TextFormatting.GREEN + "Selected block: " + name);
                    list.add(TextFormatting.GREEN + "Mode: " + descriptions[compound.getInteger("mode")]);
                }
            }
        }
        list.add("Sneak right click to select a block.");
        list.add("Right click in empty air to select 'offhand' mode.");
        list.add("Right click on block to replace.");
        showModeKeyDescription(list, "switch mode");
    }

    private static boolean isSwappingWithOffHand(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null) {
            return false;
        }
        return compound.hasKey("offhand");
    }

    private static void enableSwappingWithOffHand(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null) {
            compound = new NBTTagCompound();
            stack.setTagCompound(compound);
        }
        compound.setBoolean("offhand", true);
    }

    private static void disableSwappingWithOffHand(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null) {
            return;
        }
        compound.removeTag("offhand");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
        ItemStack heldItem = playerIn.getHeldItem(hand);
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
        return super.onItemRightClick(worldIn, playerIn, hand);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            if (player.isSneaking()) {
                selectBlock(stack, player, world, pos);
            } else {
                placeBlock(stack, player, world, pos, side);
            }
        }
        return EnumActionResult.SUCCESS;
    }

    private void placeBlock(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
        if (!checkUsage(stack, player, 1.0f)) {
            return;
        }

        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            Tools.error(player, "First select a block by sneaking");
            return;
        }

        Block block;
        int meta;
        float hardness;

        if (isSwappingWithOffHand(stack)) {
            ItemStack off = player.getHeldItemOffhand();
            if (off.isEmpty()) {
                Tools.error(player, "You need to hold a block in your offhand!");
                return;
            }
            if (!(off.getItem() instanceof ItemBlock)) {
                Tools.error(player, "The item in your offhand cannot be placed!");
                return;
            }
            ItemBlock itemBlock = (ItemBlock) off.getItem();
            block = itemBlock.getBlock();
            meta = itemBlock.getDamage(off);
            IBlockState s = block.getStateFromMeta(meta);
            hardness = s.getBlockHardness(world, pos);
        } else {
            int id = tagCompound.getInteger("block");
            block = Block.REGISTRY.getObjectById(id);
            meta = tagCompound.getInteger("meta");
            hardness = tagCompound.getFloat("hardness");
        }

        IBlockState oldState = world.getBlockState(pos);
        Block oldblock = oldState.getBlock();

        double cost = BlackListSettings.getBlacklistCost(oldblock);
        if (cost <= 0.001f) {
            Tools.error(player, "It is illegal to swap this block");
            return;
        }

        int oldmeta = oldblock.getMetaFromState(oldState);
        float blockHardness = oldblock.getBlockHardness(oldState, world, pos);

        if (block == oldblock && meta == oldmeta) {
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

        Set<BlockPos> coordinates = findSuitableBlocks(stack, world, side, pos, oldblock, oldmeta);
        boolean notenough = false;
        for (BlockPos coordinate : coordinates) {
            if (!checkUsage(stack, player, 1.0f)) {
                return;
            }
            ItemStack consumed = Tools.consumeInventoryItem(Item.getItemFromBlock(block), meta, player.inventory, player);
            if (!consumed.isEmpty()) {
                if (!player.capabilities.isCreativeMode) {
                    ItemStack oldblockItem = oldblock.getPickBlock(oldState, null, world, pos, player);
                    Tools.giveItem(world, player, pos, oldblockItem);
                }
                Tools.playSound(world, block.getSoundType().getStepSound(), coordinate.getX(), coordinate.getY(), coordinate.getZ(), 1.0f, 1.0f);
                BlockSnapshot blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(world, coordinate);
                world.setBlockToAir(coordinate);
                BlockTools.placeStackAt(player, consumed, world, coordinate, null);

                if (ForgeEventFactory.onPlayerBlockPlace(player, blocksnapshot, EnumFacing.UP, EnumHand.MAIN_HAND).isCanceled()) {
                    blocksnapshot.restore(true, false);
                    if (!player.capabilities.isCreativeMode) {
                        Tools.giveItem(world, player, player.getPosition(), consumed);
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

    private void selectBlock(ItemStack stack, EntityPlayer player, World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        ItemStack item = block.getPickBlock(state, null, world, pos, player);
        int meta = item.getMetadata();
        NBTTagCompound tagCompound = Tools.getTagCompound(stack);
        String name = Tools.getBlockName(block, meta);
        if (name == null) {
            Tools.error(player, "You cannot select this block!");
        } else {
            double cost = BlackListSettings.getBlacklistCost(block);
            if (cost <= 0.001f) {
                Tools.error(player, "It is illegal to swap this block");
                return;
            }

            int id = Block.REGISTRY.getIDForObject(block);
            tagCompound.setInteger("block", id);
            tagCompound.setInteger("meta", meta);
            float hardness = block.getBlockHardness(state, world, pos);
            tagCompound.setFloat("hardness", hardness);
            Tools.notify(player, "Selected block: " + name);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderOverlay(RenderWorldLastEvent evt, EntityPlayerSP player, ItemStack wand) {
        RayTraceResult mouseOver = Minecraft.getMinecraft().objectMouseOver;
        if (mouseOver != null && mouseOver.getBlockPos() != null && mouseOver.sideHit != null) {
            IBlockState state = player.getEntityWorld().getBlockState(mouseOver.getBlockPos());
            Block block = state.getBlock();
            if (block != null && block.getMaterial(state) != Material.AIR) {
                int meta = block.getMetaFromState(state);

                int wandId = Tools.getTagCompound(wand).getInteger("block");
                Block wandBlock = Block.REGISTRY.getObjectById(wandId);
                int wandMeta = Tools.getTagCompound(wand).getInteger("meta");
                if (wandBlock == block && wandMeta == meta) {
                    return;
                }

                Set<BlockPos> coordinates = findSuitableBlocks(wand, player.getEntityWorld(), mouseOver.sideHit, mouseOver.getBlockPos(), block, meta);
                renderOutlines(evt, player, coordinates, 200, 230, 180);
            }
        }
    }

    private Set<BlockPos> findSuitableBlocks(ItemStack stack, World world, EnumFacing sideHit, BlockPos pos, Block centerBlock, int centerMeta) {
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
                        checkAndAddBlock(world, dx, y, dz, centerBlock, centerMeta, coordinates);
                    }
                }
                break;
            case SOUTH:
            case NORTH:
                for (int dx = x - dim; dx <= x + dim; dx++) {
                    for (int dy = y - dim; dy <= y + dim; dy++) {
                        checkAndAddBlock(world, dx, dy, z, centerBlock, centerMeta, coordinates);
                    }
                }
                break;
            case EAST:
            case WEST:
                for (int dy = y - dim; dy <= y + dim; dy++) {
                    for (int dz = z - dim; dz <= z + dim; dz++) {
                        checkAndAddBlock(world, x, dy, dz, centerBlock, centerMeta, coordinates);
                    }
                }
                break;
        }

        return coordinates;
    }

    private void checkAndAddBlock(World world, int x, int y, int z, Block centerBlock, int centerMeta, Set<BlockPos> coordinates) {
        BlockPos pos = new BlockPos(x, y, z);
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == centerBlock && state.getBlock().getMetaFromState(state) == centerMeta) {
            coordinates.add(pos);
        }
    }

    private int getMode(ItemStack stack) {
        return Tools.getTagCompound(stack).getInteger("mode");
    }
}
