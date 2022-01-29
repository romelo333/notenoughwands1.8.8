package romelo333.notenoughwands.modules.wands.Items;


import mcjty.lib.builder.TooltipBuilder;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.registries.ForgeRegistries;
import romelo333.notenoughwands.modules.wands.WandsConfiguration;
import romelo333.notenoughwands.varia.Tools;

import javax.annotation.Nullable;
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
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> list, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, list, flagIn);
        tooltipBuilder.makeTooltip(getRegistryName(), stack, list, flagIn);

        CompoundTag tagCompound = stack.getTag();
        if (tagCompound != null) {
            if (tagCompound.contains("mob")) {
                String type = tagCompound.getString("type");
                if (!type.isEmpty()) {
                    EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(type));
                    if (entityType != null) {
                        list.add(new TextComponent(ChatFormatting.GREEN + "Captured mob: ").append(entityType.getDescription()));
                    }
                }
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
            CompoundTag tagCompound = stack.getOrCreateTag();
            if (tagCompound.contains("mob")) {
                Tag mobCompound = tagCompound.get("mob");
                String type = tagCompound.getString("type");
                LivingEntity entityLivingBase = createEntity(player, world, type);
                if (entityLivingBase == null) {
                    Tools.error(player, "Something went wrong trying to spawn creature!");
                    return InteractionResult.FAIL;
                }
                entityLivingBase.load((CompoundTag) mobCompound);
                entityLivingBase.moveTo(pos.getX()+.5, pos.getY()+1, pos.getZ()+.5, 0, 0);
                tagCompound.remove("mob");
                tagCompound.remove("type");
                world.addFreshEntity(entityLivingBase);
            } else {
                Tools.error(player, "There is no mob captured in this wand!");
            }
        }
        return InteractionResult.SUCCESS;
    }

    private LivingEntity createEntity(Player player, Level world, String type) {
        EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(type));
        if (entityType != null) {
            return (LivingEntity) entityType.create(world);
        }
        return null;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!player.getCommandSenderWorld().isClientSide) {
            if (entity instanceof LivingEntity) {
                if (stack.getOrCreateTag().contains("mob")) {
                    Tools.error(player, "There is already a mob in this wand!");
                    return true;
                }
                LivingEntity entityLivingBase = (LivingEntity) entity;
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
                stack.getOrCreateTag().put("mob", tagCompound);
                stack.getOrCreateTag().putString("type", entity.getType().getRegistryName().toString());
                ((ServerLevel)player.getCommandSenderWorld()).removeEntity(entity);

                registerUsage(stack, player, difficultyScale);
            } else {
                Tools.error(player, "Please select a living entity!");
            }
        }
        return true;
    }
}
