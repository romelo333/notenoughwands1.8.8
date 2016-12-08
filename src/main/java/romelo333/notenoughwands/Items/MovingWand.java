package romelo333.notenoughwands.Items;


import mcjty.lib.tools.ItemStackTools;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
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
        maxHardness = (float) cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_maxHardness", maxHardness, "Max hardness this block can move.)").getDouble();
        placeDistance = cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_placeDistance", placeDistance, "Distance at which to place blocks in 'in-air' mode").getInt();

        ConfigCategory category = cfg.getCategory(Config.CATEGORY_MOVINGBLACKLIST);
        if (category.isEmpty()) {
            // Initialize with defaults
            blacklist(cfg, "rftools:shield_block1");
            blacklist(cfg, "rftools:shield_block2");
            blacklist(cfg, "rftools:shield_block3");
            blacklist(cfg, "rftools:shield_block4");
            blacklist(cfg, "rftools:notick_invisible_shield_block");
            blacklist(cfg, "rftools:invisible_shield_block");
            blacklist(cfg, "rftools:notick_shield_block");
            blacklist(cfg, "rftools:shield_block");
            blacklist(cfg, Blocks.BEDROCK.getRegistryName().toString());
            blacklist(cfg, Blocks.PORTAL.getRegistryName().toString());
            blacklist(cfg, Blocks.END_PORTAL.getRegistryName().toString());
            setCost(cfg, Blocks.MOB_SPAWNER.getRegistryName().toString(), 5.0);
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
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean b) {
        super.addInformation(stack, player, list, b);
        NBTTagCompound compound = stack.getTagCompound();
        if (!hasBlock(compound)) {
            list.add(TextFormatting.RED + "Wand is empty.");
        } else {
            int id = compound.getInteger("block");
            Block block = (Block) Block.REGISTRY.getObjectById(id);
            int meta = compound.getInteger("meta");
            String name = Tools.getBlockName(block, meta);
            list.add(TextFormatting.GREEN + "Block: " + name);
        }
        list.add("Right click to take a block.");
        list.add("Right click again on block to place it down.");
    }

    private boolean hasBlock(NBTTagCompound compound) {
        return compound != null && compound.hasKey("block");
    }

    @Override
    protected ActionResult<ItemStack> clOnItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            NBTTagCompound compound = stack.getTagCompound();
            if (hasBlock(compound)) {
                Vec3d lookVec = player.getLookVec();
                Vec3d start = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
                int distance = this.placeDistance;
                Vec3d end = start.addVector(lookVec.xCoord * distance, lookVec.yCoord * distance, lookVec.zCoord * distance);
                RayTraceResult position = world.rayTraceBlocks(start, end);
                if (position == null) {
                    place(stack, world, new BlockPos(end), null);
                }
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    protected EnumActionResult clOnItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            NBTTagCompound compound = stack.getTagCompound();
            if (hasBlock(compound)) {
                place(stack, world, pos, side);
            } else {
                pickup(stack, player, world, pos);
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    @Override
    protected EnumActionResult clOnItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return EnumActionResult.SUCCESS;
    }

    private void place(ItemStack stack, World world, BlockPos pos, EnumFacing side) {
        BlockPos pp = side == null ? pos : pos.offset(side);
        NBTTagCompound tagCompound = stack.getTagCompound();
        int id = tagCompound.getInteger("block");
        Block block = Block.REGISTRY.getObjectById(id);
        int meta = tagCompound.getInteger("meta");

        IBlockState blockState = block.getStateFromMeta(meta);
        world.setBlockState(pp, blockState, 3);
        if (tagCompound.hasKey("tedata")) {
            NBTTagCompound tc = (NBTTagCompound) tagCompound.getTag("tedata");
            tc.setInteger("x", pp.getX());
            tc.setInteger("y", pp.getY());
            tc.setInteger("z", pp.getZ());
            TileEntity tileEntity = TileEntity.create(world, tc);
            if (tileEntity != null) {
                world.getChunkFromBlockCoords(pp).addTileEntity(tileEntity);
                tileEntity.markDirty();
                world.notifyBlockUpdate(pp, blockState, blockState, 3);
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
        ItemStack s = block.getItem(world, pos, state);
        String name;
        if (ItemStackTools.isEmpty(s)) {
            name = Tools.getBlockName(block, meta);
        } else {
            name = s.getDisplayName();
        }
        if (name == null) {
            Tools.error(player, "You cannot select this block!");
        } else {
            int id = Block.REGISTRY.getIDForObject(block);
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
        GameRegistry.addRecipe(new ItemStack(this), "re ", "ew ", "  w", 'r', Items.REDSTONE, 'e', Items.ENDER_PEARL, 'w', wandcore);
    }
}
