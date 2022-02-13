package romelo333.notenoughwands.modules.wands.Items;


import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import romelo333.notenoughwands.modules.wands.WandsConfiguration;
import romelo333.notenoughwands.varia.Tools;

import javax.annotation.Nullable;
import java.util.List;

public class FreezingWand extends GenericWand {
    public FreezingWand() {
        this.usageFactor(2.0f);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> list, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, list, flagIn);
        list.add(new TextComponent("Right click on creature to freeze creature."));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (!world.isClientSide) {

        }
        return InteractionResult.FAIL;
    }

    private void freezeMob(LivingEntity mob){
//        mob.addPotionEffect(new PotionEffect(FreezePotion.freezePotion, 200, 4));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!player.getCommandSenderWorld().isClientSide) {
            if (entity instanceof LivingEntity) {
                LivingEntity entityLivingBase = (LivingEntity) entity;
                if (entityLivingBase instanceof Player) {
                    Tools.error(player, "You cannot use this on players!");
                    return true;
                }
                if ((!WandsConfiguration.freezeAllowHostile.get()) && entityLivingBase instanceof Enemy) {
                    Tools.error(player, "It is not possible to freeze hostile mobs with this wand!");
                    return true;
                }
                if ((!WandsConfiguration.freezeAllowPassive.get()) && !(entityLivingBase instanceof Enemy)) {
                    Tools.error(player, "It is not possible to freeze passive mobs with this wand!");
                    return true;
                }

                float difficultyScale = (float) (entityLivingBase.getMaxHealth() * WandsConfiguration.freezeDifficultyMult.get() + WandsConfiguration.freezeDifficultyAdd.get());
                if (!checkUsage(stack, player, difficultyScale)) {
                    return true;
                }

                freezeMob(entityLivingBase);
                registerUsage(stack, player, difficultyScale);
            } else {
                Tools.error(player, "Please select a living entity!");
            }
        }
        return true;
    }
}
