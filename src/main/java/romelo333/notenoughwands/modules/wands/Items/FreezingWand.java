package romelo333.notenoughwands.modules.wands.Items;


import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import romelo333.notenoughwands.modules.wands.WandsConfiguration;
import romelo333.notenoughwands.varia.Tools;

import javax.annotation.Nullable;
import java.util.List;

public class FreezingWand extends GenericWand {
    public FreezingWand() {
        setup().loot(0).usageFactory(2.0f);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> list, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, list, flagIn);
        // @todo 1.15 better tooltips
        list.add(new StringTextComponent("Right click on creature to freeze creature."));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        if (!world.isRemote) {

        }
        return ActionResultType.FAIL;
    }

    private void freezeMob(LivingEntity mob){
//        mob.addPotionEffect(new PotionEffect(FreezePotion.freezePotion, 200, 4));
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        if (!player.getEntityWorld().isRemote) {
            if (entity instanceof LivingEntity) {
                LivingEntity entityLivingBase = (LivingEntity) entity;
                if (entityLivingBase instanceof PlayerEntity) {
                    Tools.error(player, "You cannot use this on players!");
                    return true;
                }
                if ((!WandsConfiguration.freezeAllowHostile.get()) && entityLivingBase instanceof IMob) {
                    Tools.error(player, "It is not possible to freeze hostile mobs with this wand!");
                    return true;
                }
                if ((!WandsConfiguration.freezeAllowPassive.get()) && !(entityLivingBase instanceof IMob)) {
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
