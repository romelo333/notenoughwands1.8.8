package romelo333.notenoughwands.Items;


import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipOptions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import romelo333.notenoughwands.Config;
import romelo333.notenoughwands.Configuration;
import romelo333.notenoughwands.varia.Tools;

import java.util.List;

public class MovingWand extends GenericWand {
    private float maxHardness = 50;
    private int placeDistance = 4;

    public MovingWand() {
        setup("moving_wand").xpUsage(3).loot(5);
    }

    @Override
    public void initConfig(Configuration cfg) {
        super.initConfig(cfg, 200, 100000, 100, 200000, 50, 500000);
        maxHardness = (float) cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_maxHardness", maxHardness, "Max hardness this block can move.)").getDouble();
        placeDistance = cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_placeDistance", placeDistance, "Distance at which to place blocks in 'in-air' mode").getInt();
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<TextComponent> list, TooltipOptions b) {
        super.addInformation(stack, player, list, b);
        CompoundTag compound = stack.getTag();
        if (!hasBlock(compound)) {
            list.add(new StringTextComponent(TextFormat.RED + "Wand is empty."));
        } else {
            String id = compound.getString("block");
            Block block = Registry.BLOCK.get(new Identifier(id));
            String name = Tools.getBlockName(block, meta);
            list.add(new StringTextComponent(TextFormat.GREEN + "Block: " + name));
        }
        list.add(new StringTextComponent("Right click to take a block."));
        list.add(new StringTextComponent("Right click again on block to place it down."));
    }

    private boolean hasBlock(CompoundTag compound) {
        return compound != null && compound.containsKey("block");
    }


    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            NBTTagCompound compound = stack.getTagCompound();
            if (hasBlock(compound)) {
                Vec3d lookVec = player.getLookVec();
                Vec3d start = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
                int distance = this.placeDistance;
                Vec3d end = start.addVector(lookVec.x * distance, lookVec.y * distance, lookVec.z * distance);
                RayTraceResult position = world.rayTraceBlocks(start, end);
                if (position == null) {
                    place(stack, world, new BlockPos(end), null, player);
                }
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public EnumActionResult onItemUseFirst(PlayerEntity player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            NBTTagCompound compound = stack.getTagCompound();
            if (hasBlock(compound)) {
                place(stack, world, pos, side, player);
            } else {
                pickup(stack, player, world, pos);
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    @Override
    public EnumActionResult onItemUse(PlayerEntity player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return EnumActionResult.SUCCESS;
    }

    private void place(ItemStack stack, World world, BlockPos pos, EnumFacing side, PlayerEntity player) {
        BlockPos pp = side == null ? pos : pos.offset(side);

        // First check what's already there
        IBlockState old = world.getBlockState(pp);
        if (!world.isAirBlock(pp) && !old.getBlock().isReplaceable(world, pp)) {
            Tools.error(player, "Something is in the way!");
            return;
        }

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

    private void pickup(ItemStack stack, PlayerEntity player, World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        int meta = block.getMetaFromState(state);
        double cost = checkPickup(player, world, pos, block, maxHardness);
        if (cost < 0.0) {
            return;
        }

        if (!checkUsage(stack, player, (float) cost)) {
            return;
        }

        NBTTagCompound tagCompound = Tools.getTagCompound(stack);
//        ItemStack s = block.getItem(world, pos, state);
        ItemStack s = block.getPickBlock(state, null, world, pos, player);
        String name;
        if (s.isEmpty()) {
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
}
