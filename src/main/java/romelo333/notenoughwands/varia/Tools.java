package romelo333.notenoughwands.varia;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class Tools {
    public static void error(EntityPlayer player, String msg) {
        player.sendStatusMessage(new TextComponentString(TextFormatting.RED + msg), false);
    }

    public static void notify(EntityPlayer player, String msg) {
        player.sendStatusMessage(new TextComponentString(TextFormatting.GREEN + msg), false);
    }

    public static boolean consumeInventoryItem(Item item, int meta, InventoryPlayer inv, EntityPlayer player) {
        if (player.capabilities.isCreativeMode) {
            return true;
        }
        int i = finditem(item, meta, inv);

        if (i < 0) {
            return false;
        } else {
            ItemStack stackInSlot = inv.getStackInSlot(i);
            int amount = -1;
            stackInSlot.grow(amount);
            stackInSlot = stackInSlot;
            if (stackInSlot.getCount() == 0) {
                inv.setInventorySlotContents(i, ItemStack.EMPTY);
            }

            return true;
        }
    }

    public static void giveItem(World world, EntityPlayer player, Block block, int meta, int cnt, BlockPos pos) {
        ItemStack oldStack = new ItemStack(block, cnt, meta);
        giveItem(world, player, pos, oldStack);
    }

    public static void giveItem(World world, EntityPlayer player, BlockPos pos, ItemStack oldStack) {
        if (!player.inventory.addItemStackToInventory(oldStack)) {
            // Not enough room. Spawn item in world.
            EntityItem entityItem = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), oldStack);
            world.spawnEntity(entityItem);
        }
    }

    public static int finditem(Item item, int meta, InventoryPlayer inv) {
        for (int i = 0; i < 36; ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == item && meta == stack.getItemDamage()) {
                return i;
            }
        }

        return -1;
    }

    public static NBTTagCompound getTagCompound(ItemStack stack) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null){
            tagCompound = new NBTTagCompound();
            stack.setTagCompound(tagCompound);
        }
        return tagCompound;
    }

    public static String getBlockName(Block block, int meta) {
        ItemStack s = new ItemStack(block,1,meta);
        if (s.getItem() == null) {
            return null;
        }
        return s.getDisplayName();
    }

    public static int getPlayerXP(EntityPlayer player) {
        return (int)(getExperienceForLevel(player.experienceLevel) + (player.experience * player.xpBarCap()));
    }

    public static boolean addPlayerXP(EntityPlayer player, int amount) {
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
        SoundEvent event = SoundEvent.REGISTRY.getObject(new ResourceLocation(soundName));
        playSound(worldObj, event, x, y, z, volume, pitch);
    }

    public static void playSound(World worldObj, SoundEvent soundEvent, double x, double y, double z, double volume, double pitch) {
        SPacketSoundEffect soundEffect = new SPacketSoundEffect(soundEvent, SoundCategory.BLOCKS, x, y, z, (float) volume, (float) pitch);

        for (int j = 0; j < worldObj.playerEntities.size(); ++j) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)worldObj.playerEntities.get(j);
            double d7 = x - entityplayermp.posX;
            double d8 = y - entityplayermp.posY;
            double d9 = z - entityplayermp.posZ;
            double d10 = d7 * d7 + d8 * d8 + d9 * d9;

            if (d10 <= 256.0D) {
                entityplayermp.connection.sendPacket(soundEffect);
            }
        }
    }

}
