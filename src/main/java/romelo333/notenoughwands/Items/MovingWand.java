package romelo333.notenoughwands.Items;


import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.registry.GameRegistry;
import romelo333.notenoughwands.Config;
import romelo333.notenoughwands.varia.Tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovingWand extends GenericWand {
    private float maxHardness = 50;
    private int placeDistance = 4;

    public Map<String,Double> blacklisted = new HashMap<String, Double>();

    public MovingWand() {
        setup("moving_wand").xpUsage(3).availability(AVAILABILITY_NORMAL).loot(5);
    }

    @Override
    public void initConfig(Configuration cfg) {
        super.initConfig(cfg);
        maxHardness = (float) cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_maxHardness", maxHardness, "Max hardness this block can move.)").getDouble();
        placeDistance = cfg.get(Config.CATEGORY_WANDS, getUnlocalizedName() + "_placeDistance", placeDistance, "Distance at which to place blocks in 'in-air' mode").getInt();

        ConfigCategory category = cfg.getCategory(Config.CATEGORY_MOVINGBLACKLIST);
        if (category.isEmpty()) {
            // Initialize with defaults
            blacklist(cfg, "tile.shieldBlock");
            blacklist(cfg, "tile.shieldBlock2");
            blacklist(cfg, "tile.shieldBlock3");
            blacklist(cfg, "tile.solidShieldBlock");
            blacklist(cfg, "tile.invisibleShieldBlock");
            setCost(cfg, "tile.mobSpawner", 5.0);
            setCost(cfg, "tile.blockAiry", 20.0);
        } else {
            for (Map.Entry<String, Property> entry : category.entrySet()) {
                blacklisted.put(entry.getKey(), entry.getValue().getDouble());
            }
        }
    }

    private void blacklist(Configuration cfg, String name) {
        setCost(cfg, name, -1.0);
    }

    private void setCost(Configuration cfg, String name, double cost) {
        cfg.get(Config.CATEGORY_MOVINGBLACKLIST, name, cost);
        blacklisted.put(name, cost);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b) {
        super.addInformation(stack, player, list, b);
        NBTTagCompound compound = stack.getTagCompound();
        if (!hasBlock(compound)) {
            list.add(EnumChatFormatting.RED + "Wand is empty.");
        } else {
            int id = compound.getInteger("block");
            Block block = (Block) Block.blockRegistry.getObjectById(id);
            int meta = compound.getInteger("meta");
            String name = Tools.getBlockName(block, meta);
            list.add(EnumChatFormatting.GREEN + "Block: " + name);
        }
        list.add("Right click to take a block.");
        list.add("Right click again on block to place it down.");
    }

    private boolean hasBlock(NBTTagCompound compound) {
        return compound != null && compound.hasKey("block");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            NBTTagCompound compound = stack.getTagCompound();
            if (hasBlock(compound)) {
                Vec3 lookVec = player.getLookVec();
                Vec3 start = new Vec3(player.posX, player.posY + player.getEyeHeight(), player.posZ);
                int distance = this.placeDistance;
                Vec3 end = start.addVector(lookVec.xCoord * distance, lookVec.yCoord * distance, lookVec.zCoord * distance);
                MovingObjectPosition position = world.rayTraceBlocks(start, end);
                if (position == null) {
                    place(stack, world, new BlockPos(end), null);
                }
            }
        }
        return stack;
    }


    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            NBTTagCompound compound = stack.getTagCompound();
            if (hasBlock(compound)) {
                place(stack, world, pos, side);
            } else {
                pickup(stack, player, world, pos);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        return true;
    }

    private void place(ItemStack stack, World world, BlockPos pos, EnumFacing side) {
        BlockPos pp = pos.offset(side);
        NBTTagCompound tagCompound = stack.getTagCompound();
        int id = tagCompound.getInteger("block");
        Block block = Block.blockRegistry.getObjectById(id);
        int meta = tagCompound.getInteger("meta");

        world.setBlockState(pp, block.getStateFromMeta(meta), 3);
        if (tagCompound.hasKey("tedata")) {
            NBTTagCompound tc = (NBTTagCompound) tagCompound.getTag("tedata");
            TileEntity tileEntity = world.getTileEntity(pp);
            if (tileEntity != null) {
                tc.setInteger("x", pp.getX());
                tc.setInteger("y", pp.getY());
                tc.setInteger("z", pp.getZ());
                tileEntity.readFromNBT(tc);
                tileEntity.markDirty();
                world.markBlockForUpdate(pp);
            }
        }

        tagCompound.removeTag("block");
        tagCompound.removeTag("tedata");
        tagCompound.removeTag("meta");
        stack.setTagCompound(tagCompound);
    }

    private void pickup(ItemStack stack, EntityPlayer player, World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        int meta = block.getMetaFromState(state);
        double cost = checkPickup(player, world, pos, block, maxHardness, blacklisted);
        if (cost < 0.0) {
            return;
        }

        if (!checkUsage(stack, player, (float) cost)) {
            return;
        }

        NBTTagCompound tagCompound = Tools.getTagCompound(stack);
        String name = Tools.getBlockName(block, meta);
        if (name == null) {
            Tools.error(player, "You cannot select this block!");
        } else {
            int id = Block.blockRegistry.getIDForObject(block);
            tagCompound.setInteger("block", id);
            tagCompound.setInteger("meta", meta);

            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity != null) {
                NBTTagCompound tc = new NBTTagCompound();
                tileEntity.writeToNBT(tc);
                world.removeTileEntity(pos);
                tc.removeTag("x");
                tc.removeTag("y");
                tc.removeTag("z");
                tagCompound.setTag("tedata", tc);
            }
            world.setBlockToAir(pos);

            Tools.notify(player, "You took: " + name);
            registerUsage(stack, player, (float) cost);
        }
    }

    @Override
    protected void setupCraftingInt(Item wandcore) {
        GameRegistry.addRecipe(new ItemStack(this), "re ", "ew ", "  w", 'r', Items.redstone, 'e', Items.ender_pearl, 'w', wandcore);
    }
}
