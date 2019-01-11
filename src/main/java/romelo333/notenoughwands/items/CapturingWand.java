package romelo333.notenoughwands.items;


import net.minecraft.client.item.TooltipOptions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import romelo333.notenoughwands.Config;
import romelo333.notenoughwands.Configuration;
import romelo333.notenoughwands.varia.Tools;

import java.util.List;

public class CapturingWand extends GenericWand {
    private boolean allowPassive = true;
    private boolean allowHostile = true;
    private float difficultyMult = 0.0f;
    private float diffcultyAdd = 1.0f;

    public CapturingWand() {
        super(100);
        setup("capturing_wand").xpUsage(10).loot(3);
    }

    @Override
    public void initConfig(Configuration cfg) {
        super.initConfig(cfg, 200, 100000, 100, 200000, 40, 500000);
        allowPassive =  cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_allowPassive", allowPassive, "Allow capturing passive mobs").getBoolean();
        allowHostile =  cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_allowHostile", allowHostile, "Allow capturing hostile mobs").getBoolean();
        difficultyMult = (float) cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_difficultyMult", difficultyMult, "Multiply the HP of a mob with this number to get the difficulty scale that affects XP/RF usage (a final result of 1.0 means that the default XP/RF is used)").getDouble();
        diffcultyAdd = (float) cfg.get(Config.CATEGORY_WANDS, getConfigPrefix() + "_diffcultyAdd", diffcultyAdd, "Add this to the HP * difficultyMult to get the final difficulty scale that affects XP/RF usage (a final result of 1.0 means that the default XP/RF is used)").getDouble();
    }


    @Override
    public void buildTooltip(ItemStack stack, World player, List<TextComponent> list, TooltipOptions b) {
        super.buildTooltip(stack, player, list, b);
        CompoundTag tagCompound = stack.getTag();
        if (tagCompound != null) {
            if (tagCompound.containsKey("mob")) {
                String type = tagCompound.getString("type");
                String name = null;
                try {
                    name = Class.forName(type).getSimpleName();
                } catch (ClassNotFoundException e) {
                    name = "?";
                }
                list.add(new StringTextComponent(TextFormat.GREEN + "Captured mob: " + name));
            }
        }
        list.add(new StringTextComponent("Left click on creature to capture it."));
        list.add(new StringTextComponent("Right click on block to respawn creature."));
    }



    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemStack stack = context.getItemStack();
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        PlayerEntity player = context.getPlayer();
        if (!world.isClient) {
            CompoundTag tagCompound = Tools.getTagCompound(stack);
            if (tagCompound.containsKey("mob")) {
                Tag mobCompound = tagCompound.getTag("mob");
                String type = tagCompound.getString("type");
                LivingEntity entityLivingBase = createEntity(player, world, type);
                if (entityLivingBase == null) {
                    Tools.error(player, "Something went wrong trying to spawn creature!");
                    return ActionResult.FAILURE;
                }
                entityLivingBase.fromTag((CompoundTag) mobCompound);
                entityLivingBase.setPosition(pos.getX()+.5, pos.getY()+1, pos.getZ()+.5);
                // @todo fabric
//                entityLivingBase.setPositionAndRotations(pos.getX()+.5, pos.getY()+1, pos.getZ()+.5, 0, 0);
                tagCompound.remove("mob");
                tagCompound.remove("type");
                world.spawnEntity(entityLivingBase);
            } else {
                Tools.error(player, "There is no mob captured in this wand!");
            }
        }
        return ActionResult.SUCCESS;
    }

    private LivingEntity createEntity(PlayerEntity player, World world, String type) {
        LivingEntity entityLivingBase;
        try {
            entityLivingBase = (LivingEntity) Class.forName(type).getConstructor(World.class).newInstance(world);
        } catch (Exception e) {
            entityLivingBase = null;
        }
        return entityLivingBase;
    }


    public boolean captureMob(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (!player.getEntityWorld().isClient) {
            if (entity != null) {
                if (Tools.getTagCompound(stack).containsKey("mob")) {
                    Tools.error(player, "There is already a mob in this wand!");
                    return true;
                }
                if (entity instanceof PlayerEntity) {
                    Tools.error(player, "I don't think that player would appreciate being captured!");
                    return true;
                }

                if ((!allowHostile) && entity instanceof Monster) {
                    Tools.error(player, "It is not possible to capture hostile mobs with this wand!");
                    return true;
                }
                if ((!allowPassive) && !(entity instanceof Monster)) {
                    Tools.error(player, "It is not possible to capture passive mobs with this wand!");
                    return true;
                }
                double cost = BlackListSettings.getBlacklistEntity(entity);
                if (cost <= 0.001f) {
                    Tools.error(player, "It is illegal to take this entity");
                    return true;
                }

                float difficultyScale = (float) (entity.getHealthMaximum() * cost * difficultyMult + diffcultyAdd);
                if (!checkUsage(stack, player, difficultyScale)) {
                    return true;
                }

                CompoundTag tagCompound = new CompoundTag();
                entity.toTag(tagCompound);
                Tools.getTagCompound(stack).put("mob", tagCompound);
                Tools.getTagCompound(stack).putString("type", entity.getClass().getCanonicalName());
                player.getEntityWorld().removeEntity(entity);

                registerUsage(stack, player, difficultyScale);
            } else {
                Tools.error(player, "Please select a living entity!");
            }
        }
        return true;
    }
}
