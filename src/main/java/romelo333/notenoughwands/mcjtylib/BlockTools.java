package romelo333.notenoughwands.mcjtylib;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.block.BlockItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockTools {
    private static final Random random = new Random();

    public static void emptyInventoryInWorld(World world, int x, int y, int z, Block block, Inventory inventory) {
        for (int i = 0; i < inventory.getInvSize(); ++i) {
            ItemStack itemstack = inventory.getInvStack(i);
            spawnItemStack(world, x, y, z, itemstack);
            inventory.setInvStack(i, ItemStack.EMPTY);
        }
        //TODO: What was this?
        //world.func_147453_f(x, y, z, block);
    }

    public static void spawnItemStack(World world, int x, int y, int z, ItemStack itemstack) {
        if (!itemstack.isEmpty()) {
            float f = random.nextFloat() * 0.8F + 0.1F;
            float f1 = random.nextFloat() * 0.8F + 0.1F;
            ItemEntity entityitem;

            float f2 = random.nextFloat() * 0.8F + 0.1F;
            while (itemstack.getAmount() > 0) {
                int j = random.nextInt(21) + 10;

                if (j > itemstack.getAmount()) {
                    j = itemstack.getAmount();
                }

                int amount = -j;
                itemstack.addAmount(amount);
                entityitem = new ItemEntity(world, (x + f), (y + f1), (z + f2), new ItemStack(itemstack.getItem(), j));
                float f3 = 0.05F;
                entityitem.velocityX = ((float)random.nextGaussian() * f3);
                entityitem.velocityY = ((float)random.nextGaussian() * f3 + 0.2F);
                entityitem.velocityZ = ((float)random.nextGaussian() * f3);

                if (itemstack.hasTag()) {
                    entityitem.getStack().setTag(itemstack.getTag().copy());
                }
                world.spawnEntity(entityitem);
            }
        }
    }

    public static Block getBlock(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem) {
            return ((BlockItem) stack.getItem()).getBlock();
        } else {
            return null;
        }
    }

    public static String getModid(ItemStack stack) {
        if (!stack.isEmpty()) {
            return Registry.ITEM.getId(stack.getItem()).getNamespace();
        } else {
            return "";
        }
    }

    public static String getModidForBlock(Block block) {
        return Registry.BLOCK.getId(block).getNamespace();
    }

    public static String getReadableName(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return getReadableName(state.getBlock().getPickStack(world, pos, state));
    }

    public static String getReadableName(ItemStack stack) {
        return stack.getDisplayName().getFormattedText();
    }

    public static BlockState placeStackAt(PlayerEntity player, ItemStack blockStack, World world, BlockPos pos, @Nullable BlockState origState) {
        if (blockStack.getItem() instanceof BlockItem) {
            // @todo check!
            ItemUsageContext usageContext = new ItemUsageContext(player, blockStack, new BlockHitResult(new Vec3d(0, 0, 0), Direction.UP, pos, true));
            ItemPlacementContext context = new ItemPlacementContext(usageContext);

            BlockItem itemBlock = (BlockItem) blockStack.getItem();
            if (origState == null) {
                origState = itemBlock.getBlock().getPlacementState(context);
            }
            if (itemBlock.place(context) == ActionResult.SUCCESS) {
                blockStack.subtractAmount(1);
            }
            return origState;
        } else {
            player.setStackInHand(Hand.MAIN, blockStack);
            player.setPosition(pos.getX()+.5, pos.getY()+1.5, pos.getZ()+.5);
            ItemUsageContext usageContext = new ItemUsageContext(player, blockStack, new BlockHitResult(new Vec3d(0, 0, 0), Direction.UP, pos, true));
//            ItemUsageContext usageContext = new ItemUsageContext(player, blockStack, pos, Direction.UP, 0, 0, 0);
            blockStack.getItem().useOnBlock(usageContext);
            return world.getBlockState(pos);
        }
    }

}
