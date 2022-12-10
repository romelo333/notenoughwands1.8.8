package romelo333.notenoughwands.modules.wands.Items;


import mcjty.lib.varia.ComponentFactory;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import romelo333.notenoughwands.modules.wands.WandsConfiguration;
import romelo333.notenoughwands.varia.Tools;

import javax.annotation.Nullable;
import java.util.List;

public class PotionWand extends GenericWand {

    public PotionWand() {
        this.usageFactor(2.0f);
    }

    private String getEffectName(MobEffectInstance potioneffect){
        String s1 = I18n.get(potioneffect.getDescriptionId()).trim();
        if (potioneffect.getAmplifier() > 0) {
            s1 = s1 + " " + I18n.get("potion.potency." + potioneffect.getAmplifier()).trim();
        }
        if (potioneffect.getDuration() > 20) {
            // @todo 1.15
//            s1 = s1 + " (" + Potion.getPotionDurationString(potioneffect, potioneffect.getDuration()) + ")";
        }
        return s1;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flagIn) {
        super.appendHoverText(stack, world, list, flagIn);
        // @todo 1.15 better tooltips
        list.add(ComponentFactory.literal("Left click on creature to apply effect"));
        CompoundTag tagCompound = stack.getTag();
        if (tagCompound==null){
            list.add(ComponentFactory.literal(ChatFormatting.YELLOW+"No effects. Combine with potion"));
            list.add(ComponentFactory.literal(ChatFormatting.YELLOW+"in crafting table to add effect"));
            return;
        }
        ListTag effects = (ListTag) tagCompound.get("effects");
        if (effects == null || effects.isEmpty()){
            list.add(ComponentFactory.literal(ChatFormatting.YELLOW+"No effects. Combine with potion"));
            list.add(ComponentFactory.literal(ChatFormatting.YELLOW+"in crafting table to add effect"));
            return;
        }
        list.add(ComponentFactory.literal(ChatFormatting.YELLOW+"Combine with empty bottle"));
        list.add(ComponentFactory.literal(ChatFormatting.YELLOW+"to clear effects"));
        int mode = getMode(stack);
        for (int i=0;i<effects.size();i++) {
            CompoundTag effecttag = effects.getCompound(i);
            MobEffectInstance effect = MobEffectInstance.load(effecttag);
            if (i==mode){
                list.add(ComponentFactory.literal("    + " + ChatFormatting.GREEN + getEffectName(effect)));
            } else {
                list.add(ComponentFactory.literal("    " + ChatFormatting.GRAY+getEffectName(effect)));
            }
        }
    }

    @Override
    public void toggleMode(Player player, ItemStack stack) {
        int mode = getMode(stack);
        mode++;
        CompoundTag tagCompound = stack.getTag();
        if (tagCompound==null){
            return;
        }
        ListTag effects = (ListTag) tagCompound.get("effects");
        if (effects == null || effects.isEmpty()){
            return;
        }
        if (mode >= effects.size()) {
            mode = 0;
        }
        CompoundTag effecttag = effects.getCompound(mode);
        MobEffectInstance effect = MobEffectInstance.load(effecttag);
        Tools.notify(player, ComponentFactory.literal("Switched to " + getEffectName(effect) + " mode"));
        stack.getOrCreateTag().putInt("mode", mode);
    }

    private int getMode(ItemStack stack) {
        return stack.getOrCreateTag().getInt("mode");
    }


    private void addeffect(LivingEntity entity, ItemStack wand, Player player){
        CompoundTag tagCompound = wand.getTag();
        if (tagCompound==null){
            Tools.error(player, "There are no effects in this wand!");
            return;
        }
        ListTag effects = (ListTag) tagCompound.get("effects");
        if (effects == null || effects.isEmpty()){
            Tools.error(player, "There are no effects in this wand!");
            return;
        }
        CompoundTag effecttag = effects.getCompound(getMode(wand));
        MobEffectInstance effect = MobEffectInstance.load(effecttag);
        entity.addEffect(effect);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!player.getCommandSenderWorld().isClientSide) {
            if (entity instanceof LivingEntity entityLivingBase) {
                if ((!WandsConfiguration.potionAllowHostile.get()) && entityLivingBase instanceof Enemy) {
                    Tools.error(player, "It is not possible to add effects to hostile mobs with this wand!");
                    return true;
                }
                if ((!WandsConfiguration.potionAllowPassive.get()) && !(entityLivingBase instanceof Enemy)) {
                    Tools.error(player, "It is not possible to add effects to passive mobs with this wand!");
                    return true;
                }

                float difficultyScale = (float) (entityLivingBase.getMaxHealth() * WandsConfiguration.potionDifficultyMult.get() + WandsConfiguration.potionDifficultyAdd.get());
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
