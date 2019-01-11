package romelo333.notenoughwands.items;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipOptions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import romelo333.notenoughwands.Config;
import romelo333.notenoughwands.Configuration;
import romelo333.notenoughwands.varia.Tools;

import java.util.List;

public class MovingWand extends GenericWand {
    private float maxHardness = 50;
    private int placeDistance = 4;

    public MovingWand() {
        super(100);
        setup("moving_wand").xpUsage(3).loot(5);
    }

    @Override
    public void initConfig(Configuration cfg) {
        super.initConfig(cfg, 200, 100000, 100, 200000, 50, 500000);
        maxHardness = (float) cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_maxHardness", maxHardness, "Max hardness this block can move.)").getDouble();
        placeDistance = cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_placeDistance", placeDistance, "Distance at which to place blocks in 'in-air' mode").getInt();
    }

    @Override
    public void buildTooltip(ItemStack stack, World player, List<TextComponent> list, TooltipOptions b) {
        super.buildTooltip(stack, player, list, b);
        CompoundTag compound = stack.getTag();
        if (!hasBlock(compound)) {
            list.add(new StringTextComponent(TextFormat.RED + "Wand is empty."));
        } else {
            String id = compound.getString("block");
            Block block = Registry.BLOCK.get(new Identifier(id));
            String name = Tools.getBlockName(block);
            list.add(new StringTextComponent(TextFormat.GREEN + "Block: " + name));
        }
        list.add(new StringTextComponent("Right click to take a block."));
        list.add(new StringTextComponent("Right click again on block to place it down."));
    }

    private boolean hasBlock(CompoundTag compound) {
        return compound != null && compound.containsKey("block");
    }



    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!world.isClient) {
            CompoundTag compound = stack.getTag();
            if (hasBlock(compound)) {
                Vec3d lookVec = player.getRotationVec(0);   // @todo fabric: partialticks?
                Vec3d start = new Vec3d(player.x, player.y + player.getEyeHeight(), player.z);
                int distance = this.placeDistance;
                Vec3d end = start.add(lookVec.x * distance, lookVec.y * distance, lookVec.z * distance);
                HitResult position = world.rayTrace(start, end);
                if (position == null) {
                    place(stack, world, new BlockPos(end), null, player);
                }
            }
        }
        return new TypedActionResult<>(ActionResult.SUCCESS, stack);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemStack stack = context.getItemStack();
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getPos();
        Direction side = context.getFacing();
        if (!world.isClient) {
            CompoundTag compound = stack.getTag();
            if (hasBlock(compound)) {
                place(stack, world, pos, side, player);
            } else {
                pickup(stack, player, world, pos);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }


    // @todo fabric
//    @Override
//    public EnumActionResult onItemUse(PlayerEntity player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
//        return EnumActionResult.SUCCESS;
//    }

    private void place(ItemStack stack, World world, BlockPos pos, Direction side, PlayerEntity player) {
        BlockPos pp = side == null ? pos : pos.offset(side);

        // First check what's already there
        BlockState old = world.getBlockState(pp);
        if (!world.isAir(pp)) {// @todo fabric && !old.getBlock().isReplaceable(world, pp)) {
            Tools.error(player, "Something is in the way!");
            return;
        }

        CompoundTag tagCompound = stack.getTag();
        String id = tagCompound.getString("block");
        Block block = Registry.BLOCK.get(new Identifier(id));

        BlockState blockState = block.getDefaultState();
        world.setBlockState(pp, blockState, 3);
        if (tagCompound.containsKey("tedata")) {
            CompoundTag tc = (CompoundTag) tagCompound.getTag("tedata");
            tc.putInt("x", pp.getX());
            tc.putInt("y", pp.getY());
            tc.putInt("z", pp.getZ());
            BlockEntity tileEntity = BlockEntity.createFromTag(tc);
            if (tileEntity != null) {
                Chunk chunk = world.getChunk(pp);
                ((WorldChunk) chunk).addBlockEntity(tileEntity);
                tileEntity.markDirty();
                world.updateListeners(pp, blockState, blockState, 3);
            }
        }

        tagCompound.remove("block");
        tagCompound.remove("tedata");
        tagCompound.remove("meta");
        stack.setTag(tagCompound);
    }

    private void pickup(ItemStack stack, PlayerEntity player, World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        double cost = checkPickup(player, world, pos, block, maxHardness);
        if (cost < 0.0) {
            return;
        }

        if (!checkUsage(stack, player, (float) cost)) {
            return;
        }

        CompoundTag tagCompound = Tools.getTagCompound(stack);
//        ItemStack s = block.getItem(world, pos, state);
        ItemStack s = block.getPickStack(world, pos, state); // @todo fabric getPickStack(state, null, world, pos, player);
        String name;
        if (s.isEmpty()) {
            name = Tools.getBlockName(block);
        } else {
            name = s.getDisplayName().getFormattedText();
        }
        if (name == null) {
            Tools.error(player, "You cannot select this block!");
        } else {
            String id = Registry.BLOCK.getId(block).toString();
            tagCompound.putString("block", id);

            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity != null) {
                CompoundTag tc = new CompoundTag();
                tileEntity.toTag(tc);
                world.removeBlockEntity(pos);
                tc.remove("x");
                tc.remove("y");
                tc.remove("z");
                tagCompound.put("tedata", tc);
            }
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);  // @todo fabric: setBlockToAir

            Tools.notify(player, "You took: " + name);
            registerUsage(stack, player, (float) cost);
        }
    }
}
