package romelo333.notenoughwands.modules.wands.Items;


import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.varia.ComponentFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import romelo333.notenoughwands.modules.wands.WandsConfiguration;
import romelo333.notenoughwands.modules.wands.WandsModule;
import romelo333.notenoughwands.modules.wands.data.CapturingWandData;
import romelo333.notenoughwands.varia.Tools;

import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.*;

public class CapturingWand extends GenericWand {

    public CapturingWand() {
        this.usageFactor(3.0f);
    }


    private final TooltipBuilder tooltipBuilder = new TooltipBuilder()
            .info(key("message.notenoughwands.shiftmessage"))
            .infoShift(header(), gold());


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, list, flagIn);
        tooltipBuilder.makeTooltip(mcjty.lib.varia.Tools.getId(this), stack, list, flagIn);

        CapturingWandData data = stack.getOrDefault(WandsModule.CAPTURINGWAND_DATA, CapturingWandData.DEFAULT);
        if (data.type() != null) {
            EntityType<?> entityType = mcjty.lib.varia.Tools.getEntity(data.type());
            if (entityType != null) {
                list.add(ComponentFactory.literal(ChatFormatting.GREEN + "Captured mob: ").append(entityType.getDescription()));
            }
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        Level world = context.getLevel();
        ItemStack stack = player.getItemInHand(hand);
        BlockPos pos = context.getClickedPos();
        if (!world.isClientSide) {
            CapturingWandData data = stack.getOrDefault(WandsModule.CAPTURINGWAND_DATA, CapturingWandData.DEFAULT);
            if (data.type() != null) {
                Tag mobCompound = data.tag();
                ResourceLocation type = data.type();
                LivingEntity entityLivingBase = createEntity(player, world, type);
                if (entityLivingBase == null) {
                    Tools.error(player, "Something went wrong trying to spawn creature!");
                    return InteractionResult.FAIL;
                }
                entityLivingBase.load((CompoundTag) mobCompound);
                entityLivingBase.moveTo(pos.getX()+.5, pos.getY()+1, pos.getZ()+.5, 0, 0);
                stack.set(WandsModule.CAPTURINGWAND_DATA, CapturingWandData.DEFAULT);
                world.addFreshEntity(entityLivingBase);
            } else {
                Tools.error(player, "There is no mob captured in this wand!");
            }
        }
        return InteractionResult.SUCCESS;
    }

    private LivingEntity createEntity(Player player, Level world, ResourceLocation type) {
        EntityType<?> entityType = mcjty.lib.varia.Tools.getEntity(type);
        if (entityType != null) {
            return (LivingEntity) entityType.create(world);
        }
        return null;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!player.getCommandSenderWorld().isClientSide) {
            if (entity instanceof LivingEntity entityLivingBase) {
                CapturingWandData data = stack.getOrDefault(WandsModule.CAPTURINGWAND_DATA, CapturingWandData.DEFAULT);
                if (data.type() != null) {
                    Tools.error(player, "There is already a mob in this wand!");
                    return true;
                }
                if (entityLivingBase instanceof Player) {
                    Tools.error(player, "I don't think that player would appreciate being captured!");
                    return true;
                }

                if ((!WandsConfiguration.allowHostile.get()) && entityLivingBase instanceof Enemy) {
                    Tools.error(player, "It is not possible to capture hostile mobs with this wand!");
                    return true;
                }
                if ((!WandsConfiguration.allowPassive.get()) && !(entityLivingBase instanceof Enemy)) {
                    Tools.error(player, "It is not possible to capture passive mobs with this wand!");
                    return true;
                }
                double cost = WandsConfiguration.getEntityCost(entity);
                if (cost <= 0.001f) {
                    Tools.error(player, "It is illegal to take this entity");
                    return true;
                }

                float difficultyScale = (float) (entityLivingBase.getMaxHealth() * cost * WandsConfiguration.difficultyMult.get() + WandsConfiguration.difficultyAdd.get());
                if (!checkUsage(stack, player, difficultyScale)) {
                    return true;
                }

                CompoundTag tagCompound = new CompoundTag();
                entityLivingBase.addAdditionalSaveData(tagCompound);  // @todo 1.15 is this right?
                stack.set(WandsModule.CAPTURINGWAND_DATA, new CapturingWandData(mcjty.lib.varia.Tools.getId(entity.getType()), tagCompound));
                entity.remove(Entity.RemovalReason.DISCARDED);
//                ((ServerLevel)player.getCommandSenderWorld()).removeEntity(entity);

                registerUsage(stack, player, difficultyScale);
            } else {
                Tools.error(player, "Please select a living entity!");
            }
        }
        return true;
    }
}
