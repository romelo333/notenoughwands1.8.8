package romelo333.notenoughwands.Items;


import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import romelo333.notenoughwands.Config;
import romelo333.notenoughwands.varia.Tools;

import java.util.List;

public class PotionWand extends GenericWand {
    private boolean allowPassive = true;
    private boolean allowHostile = true;
    private float difficultyMult = 0.0f;
    private float diffcultyAdd = 1.0f;

    public PotionWand() {
        setup("potion_wand").xpUsage(10).loot(3);
    }

    @Override
    public void initConfig(Configuration cfg) {
        super.initConfig(cfg, 200, 100000, 100, 200000, 50, 500000);
        allowPassive =  cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_allowPassive", allowPassive, "Allow freeze passive mobs").getBoolean();
        allowHostile =  cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_allowHostile", allowHostile, "Allow freeze hostile mobs").getBoolean();
        difficultyMult = (float) cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_difficultyMult", difficultyMult, "Multiply the HP of a mob with this number to get the difficulty scale that affects XP/RF usage (a final result of 1.0 means that the default XP/RF is used)").getDouble();
        diffcultyAdd = (float) cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_diffcultyAdd", diffcultyAdd, "Add this to the HP * difficultyMult to get the final difficulty scale that affects XP/RF usage (a final result of 1.0 means that the default XP/RF is used)").getDouble();
    }

    private String getEffectName(PotionEffect potioneffect){
        String s1 = I18n.format(potioneffect.getEffectName()).trim();
        if (potioneffect.getAmplifier() > 0) {
            s1 = s1 + " " + I18n.format("potion.potency." + potioneffect.getAmplifier()).trim();
        }
        if (potioneffect.getDuration() > 20) {
            s1 = s1 + " (" + Potion.getPotionDurationString(potioneffect, potioneffect.getDuration()) + ")";
        }
        return s1;
    }

    @Override
    public void addInformation(ItemStack stack, World player, List<String> list, ITooltipFlag b) {
        super.addInformation(stack, player, list, b);
        list.add("Left click on creature to apply effect");
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound==null){
            list.add(TextFormatting.YELLOW+"No effects. Combine with potion");
            list.add(TextFormatting.YELLOW+"in crafting table to add effect");
            return;
        }
        NBTTagList effects = (NBTTagList) tagCompound.getTag("effects");
        if (effects == null || effects.tagCount()==0){
            list.add(TextFormatting.YELLOW+"No effects. Combine with potion");
            list.add(TextFormatting.YELLOW+"in crafting table to add effect");
            return;
        }
        list.add(TextFormatting.YELLOW+"Combine with empty bottle");
        list.add(TextFormatting.YELLOW+"to clear effects");
        int mode = getMode(stack);
        for (int i=0;i<effects.tagCount();i++) {
            NBTTagCompound effecttag = effects.getCompoundTagAt(i);
            PotionEffect effect = PotionEffect.readCustomPotionEffectFromNBT(effecttag);
            if (i==mode){
                list.add("    + " + TextFormatting.GREEN + getEffectName(effect));
            } else {
                list.add("    " + TextFormatting.GRAY+getEffectName(effect));
            }
        }
    }

    @Override
    public void toggleMode(EntityPlayer player, ItemStack stack) {
        int mode = getMode(stack);
        mode++;
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound==null){
            return;
        }
        NBTTagList effects = (NBTTagList) tagCompound.getTag("effects");
        if (effects == null || effects.tagCount()==0){
            return;
        }
        if (mode >= effects.tagCount()) {
            mode = 0;
        }
        NBTTagCompound effecttag = effects.getCompoundTagAt(mode);
        PotionEffect effect = PotionEffect.readCustomPotionEffectFromNBT(effecttag);
        Tools.notify(player, "Switched to " + getEffectName(effect) + " mode");
        Tools.getTagCompound(stack).setInteger("mode", mode);
    }

    private int getMode(ItemStack stack) {
        return Tools.getTagCompound(stack).getInteger("mode");
    }


    private void addeffect(EntityLivingBase entity, ItemStack wand, EntityPlayer player){
        NBTTagCompound tagCompound = wand.getTagCompound();
        if (tagCompound==null){
            Tools.error(player, "There are no effects in this wand!");
            return;
        }
        NBTTagList effects = (NBTTagList) tagCompound.getTag("effects");
        if (effects == null || effects.tagCount()==0){
            Tools.error(player, "There are no effects in this wand!");
            return;
        }
        NBTTagCompound effecttag = effects.getCompoundTagAt(getMode(wand));
        PotionEffect effect = PotionEffect.readCustomPotionEffectFromNBT(effecttag);
        entity.addPotionEffect(effect);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (!player.getEntityWorld().isRemote) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                if ((!allowHostile) && entityLivingBase instanceof IMob) {
                    Tools.error(player, "It is not possible to add effects to hostile mobs with this wand!");
                    return true;
                }
                if ((!allowPassive) && !(entityLivingBase instanceof IMob)) {
                    Tools.error(player, "It is not possible to add effects to passive mobs with this wand!");
                    return true;
                }

                float difficultyScale = entityLivingBase.getMaxHealth() * difficultyMult + diffcultyAdd;
                if (!checkUsage(stack, player, difficultyScale)) {
                    return true;
                }

                addeffect(entityLivingBase, stack, player);
                registerUsage(stack, player, difficultyScale);
            } else {
                Tools.error(player, "Please select a living bindings!");
            }
        }
        return true;
    }
}
