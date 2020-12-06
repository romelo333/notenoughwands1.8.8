package romelo333.notenoughwands.Items;


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
import romelo333.notenoughwands.ConfigSetup;
import romelo333.notenoughwands.setup.Configuration;
import romelo333.notenoughwands.varia.Tools;

import javax.annotation.Nullable;
import java.util.List;

public class FreezingWand extends GenericWand {
    private boolean allowPassive = true;
    private boolean allowHostile = true;
    private float difficultyMult = 0.0f;
    private float diffcultyAdd = 1.0f;

    public FreezingWand() {
        setup().xpUsage(10).loot(0);
    }

    @Override
    public void initConfig(Configuration cfg) {
        super.initConfig(cfg, 200, 100000, 100, 200000, 50, 500000);
        allowPassive =  cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_allowPassive", allowPassive, "Allow freeze passive mobs").getBoolean();
        allowHostile =  cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_allowHostile", allowHostile, "Allow freeze hostile mobs").getBoolean();
        difficultyMult = (float) cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_difficultyMult", difficultyMult, "Multiply the HP of a mob with this number to get the difficulty scale that affects XP/RF usage (a final result of 1.0 means that the default XP/RF is used)").getDouble();
        diffcultyAdd = (float) cfg.get(ConfigSetup.CATEGORY_WANDS, getConfigPrefix() + "_diffcultyAdd", diffcultyAdd, "Add this to the HP * difficultyMult to get the final difficulty scale that affects XP/RF usage (a final result of 1.0 means that the default XP/RF is used)").getDouble();
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
                if ((!allowHostile) && entityLivingBase instanceof IMob) {
                    Tools.error(player, "It is not possible to freeze hostile mobs with this wand!");
                    return true;
                }
                if ((!allowPassive) && !(entityLivingBase instanceof IMob)) {
                    Tools.error(player, "It is not possible to freeze passive mobs with this wand!");
                    return true;
                }

                float difficultyScale = entityLivingBase.getMaxHealth() * difficultyMult + diffcultyAdd;
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
