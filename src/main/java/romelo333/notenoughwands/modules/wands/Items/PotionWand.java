package romelo333.notenoughwands.modules.wands.Items;


import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import romelo333.notenoughwands.setup.Configuration;
import romelo333.notenoughwands.varia.Tools;

import javax.annotation.Nullable;
import java.util.List;

public class PotionWand extends GenericWand {
    private boolean allowPassive = true;
    private boolean allowHostile = true;
    private float difficultyMult = 0.0f;
    private float diffcultyAdd = 1.0f;

    public PotionWand() {
        setup().loot(3).usageFactory(2.0f);
    }

    @Override
    public void initConfig(Configuration cfg) {
        // @todo 1.15 config
//        allowPassive =  cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_allowPassive", allowPassive, "Allow freeze passive mobs").getBoolean();
//        allowHostile =  cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_allowHostile", allowHostile, "Allow freeze hostile mobs").getBoolean();
//        difficultyMult = (float) cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_difficultyMult", difficultyMult, "Multiply the HP of a mob with this number to get the difficulty scale that affects XP/RF usage (a final result of 1.0 means that the default XP/RF is used)").getDouble();
//        diffcultyAdd = (float) cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_diffcultyAdd", diffcultyAdd, "Add this to the HP * difficultyMult to get the final difficulty scale that affects XP/RF usage (a final result of 1.0 means that the default XP/RF is used)").getDouble();
    }

    private String getEffectName(EffectInstance potioneffect){
        String s1 = I18n.format(potioneffect.getEffectName()).trim();
        if (potioneffect.getAmplifier() > 0) {
            s1 = s1 + " " + I18n.format("potion.potency." + potioneffect.getAmplifier()).trim();
        }
        if (potioneffect.getDuration() > 20) {
            // @todo 1.15
//            s1 = s1 + " (" + Potion.getPotionDurationString(potioneffect, potioneffect.getDuration()) + ")";
        }
        return s1;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flagIn) {
        super.addInformation(stack, world, list, flagIn);
        // @todo 1.15 better tooltips
        list.add(new StringTextComponent("Left click on creature to apply effect"));
        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound==null){
            list.add(new StringTextComponent(TextFormatting.YELLOW+"No effects. Combine with potion"));
            list.add(new StringTextComponent(TextFormatting.YELLOW+"in crafting table to add effect"));
            return;
        }
        ListNBT effects = (ListNBT) tagCompound.get("effects");
        if (effects == null || effects.size()==0){
            list.add(new StringTextComponent(TextFormatting.YELLOW+"No effects. Combine with potion"));
            list.add(new StringTextComponent(TextFormatting.YELLOW+"in crafting table to add effect"));
            return;
        }
        list.add(new StringTextComponent(TextFormatting.YELLOW+"Combine with empty bottle"));
        list.add(new StringTextComponent(TextFormatting.YELLOW+"to clear effects"));
        int mode = getMode(stack);
        for (int i=0;i<effects.size();i++) {
            CompoundNBT effecttag = effects.getCompound(i);
            EffectInstance effect = EffectInstance.read(effecttag);
            if (i==mode){
                list.add(new StringTextComponent("    + " + TextFormatting.GREEN + getEffectName(effect)));
            } else {
                list.add(new StringTextComponent("    " + TextFormatting.GRAY+getEffectName(effect)));
            }
        }
    }

    @Override
    public void toggleMode(PlayerEntity player, ItemStack stack) {
        int mode = getMode(stack);
        mode++;
        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound==null){
            return;
        }
        ListNBT effects = (ListNBT) tagCompound.get("effects");
        if (effects == null || effects.size()==0){
            return;
        }
        if (mode >= effects.size()) {
            mode = 0;
        }
        CompoundNBT effecttag = effects.getCompound(mode);
        EffectInstance effect = EffectInstance.read(effecttag);
        Tools.notify(player, "Switched to " + getEffectName(effect) + " mode");
        stack.getOrCreateTag().putInt("mode", mode);
    }

    private int getMode(ItemStack stack) {
        return stack.getOrCreateTag().getInt("mode");
    }


    private void addeffect(LivingEntity entity, ItemStack wand, PlayerEntity player){
        CompoundNBT tagCompound = wand.getTag();
        if (tagCompound==null){
            Tools.error(player, "There are no effects in this wand!");
            return;
        }
        ListNBT effects = (ListNBT) tagCompound.get("effects");
        if (effects == null || effects.size()==0){
            Tools.error(player, "There are no effects in this wand!");
            return;
        }
        CompoundNBT effecttag = effects.getCompound(getMode(wand));
        EffectInstance effect = EffectInstance.read(effecttag);
        entity.addPotionEffect(effect);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        if (!player.getEntityWorld().isRemote) {
            if (entity instanceof LivingEntity) {
                LivingEntity entityLivingBase = (LivingEntity) entity;
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
                Tools.error(player, "Please select a living entity!");
            }
        }
        return true;
    }
}
