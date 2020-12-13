package romelo333.notenoughwands.modules.buildingwands.items;


import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.ForgeEventFactory;
import romelo333.notenoughwands.modules.buildingwands.BuildingWandsConfiguration;
import romelo333.notenoughwands.modules.wands.Items.GenericWand;
import romelo333.notenoughwands.varia.Tools;

import javax.annotation.Nullable;
import java.util.List;

public class MovingWand extends GenericWand {

    public MovingWand() {
        setup().loot(5).usageFactory(1.5f);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flagIn) {
        super.addInformation(stack, world, list, flagIn);
        CompoundNBT compound = stack.getTag();
        // @todo 1.15 better tooltip
        if (!hasBlock(compound)) {
            list.add(new StringTextComponent(TextFormatting.RED + "Wand is empty."));
        } else {
            BlockState state = NBTUtil.readBlockState(compound.getCompound("block"));
            ITextComponent name = Tools.getBlockName(state.getBlock());
            list.add(new StringTextComponent("Block:").appendSibling(name).applyTextStyle(TextFormatting.GREEN));
        }
        list.add(new StringTextComponent("Right click to take a block."));
        list.add(new StringTextComponent("Right click again on block to place it down."));
    }

    private boolean hasBlock(CompoundNBT compound) {
        return compound != null && compound.contains("block");
    }


    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            CompoundNBT compound = stack.getTag();
            if (hasBlock(compound)) {
                Vec3d lookVec = player.getLookVec();
                Vec3d start = new Vec3d(player.getPosX(), player.getPosY() + player.getEyeHeight(), player.getPosZ());
                int distance = BuildingWandsConfiguration.placeDistance.get();
                Vec3d end = start.add(lookVec.x * distance, lookVec.y * distance, lookVec.z * distance);
                RayTraceContext context = new RayTraceContext(start, end, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, null);
                BlockRayTraceResult position = world.rayTraceBlocks(context);
                if (position == null) {
                    place(stack, world, new BlockPos(end), null, player);
                }
            }
        }
        return ActionResult.resultSuccess(stack);
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        Direction side = context.getFace();
        if (!world.isRemote) {
            CompoundNBT compound = stack.getTag();
            if (hasBlock(compound)) {
                place(stack, world, pos, side, player);
            } else {
                pickup(stack, player, world, pos);
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        return ActionResultType.SUCCESS;
    }


    private void place(ItemStack stack, World world, BlockPos pos, Direction side, PlayerEntity player) {

        BlockPos pp = side == null ? pos : pos.offset(side);

        // First check what's already there
        BlockState old = world.getBlockState(pp);
        if (!world.isAirBlock(pp) && !old.getMaterial().isReplaceable()) {//@todo 1.15 check
            Tools.error(player, "Something is in the way!");
            return;
        }

        CompoundNBT tagCompound = stack.getOrCreateTag();
        BlockState blockState = NBTUtil.readBlockState(tagCompound.getCompound("block"));

        BlockSnapshot blocksnapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(world, pp);

        world.setBlockState(pp, blockState, Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
        if (ForgeEventFactory.onBlockPlace(player, blocksnapshot, Direction.UP)) {
            blocksnapshot.restore(true, false);
            return;
        }

        if (tagCompound.contains("tedata")) {
            CompoundNBT tc = (CompoundNBT) tagCompound.get("tedata");
            tc.putInt("x", pp.getX());
            tc.putInt("y", pp.getY());
            tc.putInt("z", pp.getZ());
            TileEntity tileEntity = TileEntity.create(tc);
            if (tileEntity != null) {
                world.getChunk(pp).addTileEntity(pp, tileEntity);
                tileEntity.markDirty();
                world.notifyBlockUpdate(pp, blockState, blockState, 3);
            }
        }

        tagCompound.remove("block");
        tagCompound.remove("tedata");
        tagCompound.remove("meta");
        stack.setTag(tagCompound);
    }

    private void pickup(ItemStack stack, PlayerEntity player, World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        double cost = checkPickup(player, world, pos, state, BuildingWandsConfiguration.maxHardness.get());
        if (cost < 0.0) {
            return;
        }

        if (!checkUsage(stack, player, (float) cost)) {
            return;
        }

        CompoundNBT tagCompound = stack.getOrCreateTag();
        ItemStack s = state.getBlock().getItem(world, pos, state);
        ITextComponent name;
        if (s.isEmpty()) {
            name = Tools.getBlockName(state.getBlock());
        } else {
            name = s.getDisplayName();
        }
        if (name == null) {
            Tools.error(player, "You cannot select this block!");
        } else {
            tagCompound.put("block", NBTUtil.writeBlockState(state));

            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity != null) {
                CompoundNBT tc = new CompoundNBT();
                tileEntity.write(tc);
                world.removeTileEntity(pos);
                tc.remove("x");
                tc.remove("y");
                tc.remove("z");
                tagCompound.put("tedata", tc);
            }
            world.setBlockState(pos, Blocks.AIR.getDefaultState());

            Tools.notify(player, new StringTextComponent("You took: ").appendSibling(name));
            registerUsage(stack, player, (float) cost);
        }
    }
}
