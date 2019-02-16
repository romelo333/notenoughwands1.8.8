package romelo333.notenoughwands.varia;

import net.minecraft.block.Block;
import net.minecraft.client.network.packet.PlaySoundS2CPacket;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class Tools {
    public static void error(PlayerEntity player, String msg) {
        player.addChatMessage(new StringTextComponent(TextFormat.RED + msg), false);
    }

    public static void notify(PlayerEntity player, String msg) {
        player.addChatMessage(new StringTextComponent(TextFormat.GREEN + msg), false);
    }

    @Nonnull
    public static ItemStack consumeInventoryItem(Item item, PlayerInventory inv, PlayerEntity player) {
        if (player.isCreative()) {
            return new ItemStack(item, 1);
        }
        int i = finditem(item, inv);

        if (i < 0) {
            return ItemStack.EMPTY;
        } else {
            ItemStack stackInSlot = inv.getInvStack(i);
            ItemStack result = stackInSlot.copy();
            result.setAmount(1);
            int amount = -1;
            stackInSlot.addAmount(amount);
            if (stackInSlot.getAmount() == 0) {
                inv.setInvStack(i, ItemStack.EMPTY);
            }

            return result;
        }
    }

    public static void giveItem(World world, PlayerEntity player, Block block, int cnt, BlockPos pos) {
        ItemStack oldStack = new ItemStack(block, cnt);
        giveItem(world, player, pos, oldStack);
    }

    public static void giveItem(World world, PlayerEntity player, BlockPos pos, ItemStack oldStack) {
        if (!player.inventory.insertStack(oldStack)) {
            // Not enough room. Spawn item in world.
            ItemEntity entityItem = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), oldStack);
            world.spawnEntity(entityItem);
        }
    }

    public static int finditem(Item item, PlayerInventory inv) {
        for (int i = 0; i < 36; ++i) {
            ItemStack stack = inv.getInvStack(i);
            if (!stack.isEmpty() && stack.getItem() == item) {
                return i;
            }
        }

        return -1;
    }

    public static CompoundTag getTagCompound(ItemStack stack) {
        CompoundTag tagCompound = stack.getTag();
        if (tagCompound == null){
            tagCompound = new CompoundTag();
            stack.setTag(tagCompound);
        }
        return tagCompound;
    }

    public static String getBlockName(Block block) {
        ItemStack s = new ItemStack(block,1);
        if (s.getItem() == null) {
            return null;
        }
        return s.getDisplayName().getFormattedText();
    }

    public static int getPlayerXP(PlayerEntity player) {
        return (int)(getExperienceForLevel(player.experienceLevel) + (player.experience * player.method_7349()));   // @todo fabric xpBarCap()
    }

    public static boolean addPlayerXP(PlayerEntity player, int amount) {
        int experience = getPlayerXP(player) + amount;
        if (experience < 0) {
            return false;
        }
        player.experience = experience;
        player.experienceLevel = getLevelForExperience(experience);
        int expForLevel = getExperienceForLevel(player.experienceLevel);
        player.experienceBarProgress = (experience - expForLevel) / (float)player.method_7349();   // @todo fabric xpBarCap()
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
        SoundEvent event = Registry.SOUND_EVENT.get(new Identifier(soundName));
        playSound(worldObj, event, x, y, z, volume, pitch);
    }

    public static void playSound(World worldObj, SoundEvent soundEvent, double x, double y, double z, double volume, double pitch) {
        PlaySoundS2CPacket soundEffect = new PlaySoundS2CPacket(soundEvent, SoundCategory.BLOCK, x, y, z, (float) volume, (float) pitch);

        for (int j = 0; j < worldObj.players.size(); ++j) {
            ServerPlayerEntity PlayerEntitymp = (ServerPlayerEntity) worldObj.players.get(j);
            double d7 = x - PlayerEntitymp.x;
            double d8 = y - PlayerEntitymp.y;
            double d9 = z - PlayerEntitymp.z;
            double d10 = d7 * d7 + d8 * d8 + d9 * d9;

            if (d10 <= 256.0D) {
                PlayerEntitymp.networkHandler.sendPacket(soundEffect);
            }
        }
    }

}
