package romelo333.notenoughwands.varia;

import mcjty.lib.varia.ComponentFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Tools {
    public static void error(Player player, String msg) {
        player.displayClientMessage(ComponentFactory.literal(ChatFormatting.RED + msg), false);
    }

    public static void notify(Player player, MutableComponent msg) {
        player.displayClientMessage(msg.withStyle(ChatFormatting.GREEN), false);
    }

    // PlaceStackAt from a perspective of a wand
    @Nullable
    public static BlockState placeStackAt(Player player, ItemStack blockStack, Level world, BlockPos pos, @Nullable BlockState origState) {
        ItemStack old = player.getItemInHand(InteractionHand.MAIN_HAND);
        player.setItemInHand(InteractionHand.MAIN_HAND, blockStack);

        BlockHitResult trace = new BlockHitResult(new Vec3(0, 0, 0), Direction.UP, pos, false);
        BlockPlaceContext context = new BlockPlaceContext(new UseOnContext(player, InteractionHand.MAIN_HAND, trace));
        if (blockStack.getItem() instanceof BlockItem itemBlock) {
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
            player.setItemInHand(InteractionHand.MAIN_HAND, old);
            return origState;
        } else {
            player.setPos(pos.getX()+.5, pos.getY()+1.5, pos.getZ()+.5);
            blockStack.getItem().useOn(context);
            player.setItemInHand(InteractionHand.MAIN_HAND, old);
            return world.getBlockState(pos);
        }
    }


    @Nonnull
    public static ItemStack consumeInventoryItem(ItemStack item, Inventory inv, Player player) {
        if (player.isCreative()) {
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

    public static void giveItem(Player player, Block block, int cnt) {
        giveItem(player, new ItemStack(block, cnt));
    }

    public static void giveItem(Player player, ItemStack stack) {
        ItemHandlerHelper.giveItemToPlayer(player, stack);
    }

    public static int finditem(ItemStack item, Inventory inv) {
        for (int i = 0; i < 36; ++i) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty() && ItemStack.isSameItemSameComponents(item, stack)) {
                return i;
            }
        }

        return -1;
    }

    public static Component getBlockName(Block block) {
        ItemStack s = new ItemStack(block, 1);
        if (s.getItem() == null) {
            return ComponentFactory.literal("<null>");
        }
        return s.getHoverName();
    }

    public static int getPlayerXP(Player player) {
        return (int)(getExperienceForLevel(player.experienceLevel) + (player.experienceProgress * player.getXpNeededForNextLevel()));
    }

    public static boolean addPlayerXP(Player player, int amount) {
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
