package romelo333.notenoughwands.varia;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Tools {
    public static void error(PlayerEntity player, String msg) {
        player.displayClientMessage(new StringTextComponent(TextFormatting.RED + msg), false);
    }

    public static void notify(PlayerEntity player, IFormattableTextComponent msg) {
        player.displayClientMessage(msg.withStyle(TextFormatting.GREEN), false);
    }

    // PlaceStackAt from a perspective of a wand
    @Nullable
    public static BlockState placeStackAt(PlayerEntity player, ItemStack blockStack, World world, BlockPos pos, @Nullable BlockState origState) {
        ItemStack old = player.getItemInHand(Hand.MAIN_HAND);
        player.setItemInHand(Hand.MAIN_HAND, blockStack);

        BlockRayTraceResult trace = new BlockRayTraceResult(new Vector3d(0, 0, 0), Direction.UP, pos, false);
        BlockItemUseContext context = new BlockItemUseContext(new ItemUseContext(player, Hand.MAIN_HAND, trace));
        if (blockStack.getItem() instanceof BlockItem) {
            BlockItem itemBlock = (BlockItem) blockStack.getItem();
            if (origState == null) {
                origState = itemBlock.getBlock().getStateForPlacement(context);
                if (origState == null) {
                    // Cannot place!
                    return null;
                }
            }
            if (itemBlock.place(context).consumesAction()) {
//                blockStack.shrink(1);
            }
            player.setItemInHand(Hand.MAIN_HAND, old);
            return origState;
        } else {
            player.setPos(pos.getX()+.5, pos.getY()+1.5, pos.getZ()+.5);
            blockStack.getItem().useOn(context);
            player.setItemInHand(Hand.MAIN_HAND, old);
            return world.getBlockState(pos);
        }
    }


    @Nonnull
    public static ItemStack consumeInventoryItem(ItemStack item, PlayerInventory inv, PlayerEntity player) {
        if (player.abilities.instabuild) {
            return item;
        }
        int i = finditem(item, inv);

        if (i < 0) {
            return ItemStack.EMPTY;
        } else {
            ItemStack stackInSlot = inv.getItem(i);
            ItemStack result = stackInSlot.copy();
            result.setCount(1);
            int amount = -1;
            stackInSlot.grow(amount);
            if (stackInSlot.getCount() == 0) {
                inv.setItem(i, ItemStack.EMPTY);
            }

            return result;
        }
    }

    public static void giveItem(PlayerEntity player, Block block, int cnt) {
        giveItem(player, new ItemStack(block, cnt));
    }

    public static void giveItem(PlayerEntity player, ItemStack stack) {
        ItemHandlerHelper.giveItemToPlayer(player, stack);
    }

    public static int finditem(ItemStack item, PlayerInventory inv) {
        for (int i = 0; i < 36; ++i) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty() && ItemHandlerHelper.canItemStacksStack(item, stack)) {
                return i;
            }
        }

        return -1;
    }

    public static ITextComponent getBlockName(Block block) {
        ItemStack s = new ItemStack(block, 1);
        if (s.getItem() == null) {
            return new StringTextComponent("<null>");
        }
        return s.getHoverName();
    }

    public static int getPlayerXP(PlayerEntity player) {
        return (int)(getExperienceForLevel(player.experienceLevel) + (player.experienceProgress * player.getXpNeededForNextLevel()));
    }

    public static boolean addPlayerXP(PlayerEntity player, int amount) {
        int experience = getPlayerXP(player) + amount;
        if (experience < 0) {
            return false;
        }
        player.totalExperience = experience;
        player.experienceLevel = getLevelForExperience(experience);
        int expForLevel = getExperienceForLevel(player.experienceLevel);
        player.experienceProgress = (experience - expForLevel) / (float)player.getXpNeededForNextLevel();
        return true;
    }

    public static int getExperienceForLevel(int level) {
        if (level == 0) { return 0; }
        if (level > 0 && level < 16) {
            return level * 17;
        } else if (level > 15 && level < 31) {
            return (int)(1.5 * Math.pow(level, 2) - 29.5 * level + 360);
        } else {
            return (int)(3.5 * Math.pow(level, 2) - 151.5 * level + 2220);
        }
    }

    public static int getXpToNextLevel(int level) {
        int levelXP = getLevelForExperience(level);
        int nextXP = getExperienceForLevel(level + 1);
        return nextXP - levelXP;
    }

    public static int getLevelForExperience(int experience) {
        int i = 0;
        while (getExperienceForLevel(i) <= experience) {
            i++;
        }
        return i - 1;
    }
}
