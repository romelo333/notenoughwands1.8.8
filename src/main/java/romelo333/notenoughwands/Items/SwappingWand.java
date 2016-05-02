package romelo333.notenoughwands.Items;


import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import romelo333.notenoughwands.Config;
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
        setup("swapping_wand").xpUsage(1).availability(AVAILABILITY_NORMAL).loot(5);
    }

    @Override
    public void initConfig(Configuration cfg) {
        super.initConfig(cfg);
        hardnessDistance = (float) cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_hardnessDistance", hardnessDistance, "How far away the hardness can be to allow swapping (100 means basically everything allowed)").getDouble();
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
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean b) {
        super.addInformation(stack, player, list, b);
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null) {
            list.add(TextFormatting.RED + "No selected block");
        } else {
            int id = compound.getInteger("block");
            Block block = Block.REGISTRY.getObjectById(id);
            int meta = compound.getInteger("meta");
            String name = Tools.getBlockName(block, meta);
            list.add(TextFormatting.GREEN + "Selected block: " + name);
            list.add(TextFormatting.GREEN + "Mode: " + descriptions[compound.getInteger("mode")]);
        }
        list.add("Sneak right click to select a block.");
        list.add("Right click on block to replace.");
        list.add("Mode key (default '=') to switch mode.");
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
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
        int id = tagCompound.getInteger("block");
        Block block = Block.REGISTRY.getObjectById(id);
        int meta = tagCompound.getInteger("meta");
        float hardness = tagCompound.getFloat("hardness");

        IBlockState oldState = world.getBlockState(pos);
        Block oldblock = oldState.getBlock();
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

        if (Math.abs(hardness-blockHardness) >= hardnessDistance) {
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
            if (Tools.consumeInventoryItem(Item.getItemFromBlock(block), meta, player.inventory, player)) {
                if (!player.capabilities.isCreativeMode) {
                    Tools.giveItem(world, player, oldblock, oldmeta, 1, pos);
                }
                Tools.playSound(world, block.getSoundType().getStepSound(), coordinate.getX(), coordinate.getY(), coordinate.getZ(), 1.0f, 1.0f);
                world.setBlockState(coordinate, block.getStateFromMeta(meta), 2);
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
        int meta = block.getMetaFromState(state);
        NBTTagCompound tagCompound = Tools.getTagCompound(stack);
        String name = Tools.getBlockName(block, meta);
        if (name == null) {
            Tools.error(player, "You cannot select this block!");
        } else {
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
            IBlockState state = player.worldObj.getBlockState(mouseOver.getBlockPos());
            Block block = state.getBlock();
            if (block != null && block.getMaterial(state) != Material.AIR) {
                int meta = block.getMetaFromState(state);

                int wandId = Tools.getTagCompound(wand).getInteger("block");
                Block wandBlock = Block.REGISTRY.getObjectById(wandId);
                int wandMeta = Tools.getTagCompound(wand).getInteger("meta");
                if (wandBlock == block && wandMeta == meta) {
                    return;
                }

                Set<BlockPos> coordinates = findSuitableBlocks(wand, player.worldObj, mouseOver.sideHit, mouseOver.getBlockPos(), block, meta);
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

    @Override
    protected void setupCraftingInt(Item wandcore) {
        GameRegistry.addRecipe(new ItemStack(this), "rg ", "gw ", "  w", 'r', Blocks.REDSTONE_BLOCK, 'g', Blocks.GLOWSTONE, 'w', wandcore);
    }
}
