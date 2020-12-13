package romelo333.notenoughwands.varia;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class Tools {
    public static void error(PlayerEntity player, String msg) {
        player.sendStatusMessage(new StringTextComponent(TextFormatting.RED + msg), false);
    }

    public static void notify(PlayerEntity player, ITextComponent msg) {
        player.sendStatusMessage(msg.applyTextStyle(TextFormatting.GREEN), false);
    }

    @Nonnull
    public static ItemStack consumeInventoryItem(Item item, PlayerInventory inv, PlayerEntity player) {
        // @todo 1.15 check
        if (player.abilities.isCreativeMode) {
            return new ItemStack(item, 1);
        }
        int i = finditem(item, inv);

        if (i < 0) {
            return ItemStack.EMPTY;
        } else {
            ItemStack stackInSlot = inv.getStackInSlot(i);
            ItemStack result = stackInSlot.copy();
            result.setCount(1);
            int amount = -1;
            stackInSlot.grow(amount);
            if (stackInSlot.getCount() == 0) {
                inv.setInventorySlotContents(i, ItemStack.EMPTY);
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

    public static int finditem(Item item, PlayerInventory inv) {
        for (int i = 0; i < 36; ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == item) {
                return i;
            }
        }

        return -1;
    }

    public static ITextComponent getBlockName(Block block) {
        ItemStack s = new ItemStack(block, 1);
        if (s.getItem() == null) {
            return null;
        }
        return s.getDisplayName();
    }

    public static int getPlayerXP(PlayerEntity player) {
        return (int)(getExperienceForLevel(player.experienceLevel) + (player.experience * player.xpBarCap()));
    }

    public static boolean addPlayerXP(PlayerEntity player, int amount) {
        int experience = getPlayerXP(player) + amount;
        if (experience < 0) {
            return false;
        }
        player.experienceTotal = experience;
        player.experienceLevel = getLevelForExperience(experience);
        int expForLevel = getExperienceForLevel(player.experienceLevel);
        player.experience = (experience - expForLevel) / (float)player.xpBarCap();
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

    // Server side: play a sound to all nearby players
    public static void playSound(World worldObj, String soundName, double x, double y, double z, double volume, double pitch) {
        SoundEvent event = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(soundName));
        playSound(worldObj, event, x, y, z, volume, pitch);
    }

    public static void playSound(World worldObj, SoundEvent soundEvent, double x, double y, double z, double volume, double pitch) {
        // @todo 1.15
//        SPacketSoundEffect soundEffect = new SPacketSoundEffect(soundEvent, SoundCategory.BLOCKS, x, y, z, (float) volume, (float) pitch);
//
//        for (int j = 0; j < worldObj.playerEntities.size(); ++j) {
//            EntityPlayerMP entityplayermp = (EntityPlayerMP)worldObj.playerEntities.get(j);
//            double d7 = x - entityplayermp.posX;
//            double d8 = y - entityplayermp.posY;
//            double d9 = z - entityplayermp.posZ;
//            double d10 = d7 * d7 + d8 * d8 + d9 * d9;
//
//            if (d10 <= 256.0D) {
//                entityplayermp.connection.sendPacket(soundEffect);
//            }
//        }
    }

}
