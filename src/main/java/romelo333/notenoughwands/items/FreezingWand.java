package romelo333.notenoughwands.items;


import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import romelo333.notenoughwands.Config;
import romelo333.notenoughwands.Configuration;
import romelo333.notenoughwands.varia.Tools;

import java.util.List;

public class FreezingWand extends GenericWand {
    private boolean allowPassive = true;
    private boolean allowHostile = true;
    private float difficultyMult = 0.0f;
    private float diffcultyAdd = 1.0f;

    public FreezingWand() {
        super(100);
        setup("freezing_wand").xpUsage(10).loot(0);
    }

    @Override
    public void initConfig(Configuration cfg) {
        super.initConfig(cfg, 200, 100000, 100, 200000, 50, 500000);
        allowPassive =  cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_allowPassive", allowPassive, "Allow freeze passive mobs").getBoolean();
        allowHostile =  cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_allowHostile", allowHostile, "Allow freeze hostile mobs").getBoolean();
        difficultyMult = (float) cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_difficultyMult", difficultyMult, "Multiply the HP of a mob with this number to get the difficulty scale that affects XP/RF usage (a final result of 1.0 means that the default XP/RF is used)").getDouble();
        diffcultyAdd = (float) cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_diffcultyAdd", diffcultyAdd, "Add this to the HP * difficultyMult to get the final difficulty scale that affects XP/RF usage (a final result of 1.0 means that the default XP/RF is used)").getDouble();
    }

    @Override
    public void buildTooltip(ItemStack stack, World player, List<Component> list, TooltipContext b) {
        super.buildTooltip(stack, player, list, b);
        list.add(new TextComponent("Right click on creature to freeze creature."));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (!world.isClient) {

        }
        return ActionResult.FAIL;
    }

    private void freezeMob(LivingEntity mob){
//        mob.addPotionEffect(new PotionEffect(FreezePotion.freezePotion, 200, 4));
    }

    @Override
    public boolean interactWithEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (!player.getEntityWorld().isClient) {
            if (entity != null) {
                if (entity instanceof PlayerEntity) {
                    Tools.error(player, "You cannot use this on players!");
                    return true;
                }
                if ((!allowHostile) && entity instanceof Monster) {
                    Tools.error(player, "It is not possible to freeze hostile mobs with this wand!");
                    return true;
                }
                if ((!allowPassive) && !(entity instanceof Monster)) {
                    Tools.error(player, "It is not possible to freeze passive mobs with this wand!");
                    return true;
                }

                float difficultyScale = entity.getHealthMaximum() * difficultyMult + diffcultyAdd;
                if (!checkUsage(stack, player, difficultyScale)) {
                    return true;
                }

                freezeMob(entity);
                registerUsage(stack, player, difficultyScale);
            } else {
                Tools.error(player, "Please select a living entity!");
            }
        }
        return true;
    }
}
