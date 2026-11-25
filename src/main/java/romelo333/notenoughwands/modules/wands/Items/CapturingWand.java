package romelo333.notenoughwands.modules.wands.Items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.varia.ComponentFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import romelo333.notenoughwands.modules.wands.WandsConfiguration;
import romelo333.notenoughwands.modules.wands.WandsModule;
import romelo333.notenoughwands.modules.wands.data.CapturingWandData;
import romelo333.notenoughwands.varia.Tools;

import java.util.function.Consumer;

import static mcjty.lib.builder.TooltipBuilder.*;

public class CapturingWand extends GenericWand {

    public CapturingWand(Properties properties) {
        super(properties);
        this.usageFactor(3.0f);
    }


    private final TooltipBuilder tooltipBuilder = new TooltipBuilder()
            .info(key("message.notenoughwands.shiftmessage"))
            .infoShift(header(), gold());


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> list, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, display, list, flagIn);
        Tools.appendTooltip(tooltipBuilder, mcjty.lib.varia.Tools.getId(this), stack, list, flagIn);

        CapturingWandData data = stack.getOrDefault(WandsModule.CAPTURINGWAND_DATA.get(), CapturingWandData.DEFAULT);
        if (data.type() != null) {
            EntityType<?> entityType = mcjty.lib.varia.Tools.getEntity(data.type());
            if (entityType != null) {
                list.accept(ComponentFactory.literal(ChatFormatting.GREEN + "Captured mob: ").append(entityType.getDescription()));
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
        if (!world.isClientSide()) {
            CapturingWandData data = stack.getOrDefault(WandsModule.CAPTURINGWAND_DATA.get(), CapturingWandData.DEFAULT);
            if (data.type() != null) {
                CompoundTag mobCompound = data.tag();
                Identifier type = data.type();
                LivingEntity entityLivingBase = createEntity(world, type);
                if (entityLivingBase == null) {
                    Tools.error(player, "Something went wrong trying to spawn creature!");
                    return InteractionResult.FAIL;
                }
                entityLivingBase.load(TagValueInput.create(ProblemReporter.DISCARDING, world.registryAccess(), mobCompound));
                entityLivingBase.snapTo(pos.getX()+.5, pos.getY()+1, pos.getZ()+.5, 0, 0);
                stack.set(WandsModule.CAPTURINGWAND_DATA.get(), CapturingWandData.DEFAULT);
                world.addFreshEntity(entityLivingBase);
            } else {
                Tools.error(player, "There is no mob captured in this wand!");
            }
        }
        return InteractionResult.SUCCESS;
    }

    private LivingEntity createEntity(Level world, Identifier type) {
        EntityType<?> entityType = mcjty.lib.varia.Tools.getEntity(type);
        if (entityType != null) {
            return entityType.create(world, EntitySpawnReason.SPAWN_ITEM_USE) instanceof LivingEntity living ? living : null;
        }
        return null;
    }

    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!player.level().isClientSide()) {
            if (entity instanceof LivingEntity entityLivingBase) {
                CapturingWandData data = stack.getOrDefault(WandsModule.CAPTURINGWAND_DATA.get(), CapturingWandData.DEFAULT);
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

                TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, player.registryAccess());
                entityLivingBase.saveWithoutId(output);
                CompoundTag tagCompound = output.buildResult();
                tagCompound.remove("UUID");
                stack.set(WandsModule.CAPTURINGWAND_DATA.get(), new CapturingWandData(mcjty.lib.varia.Tools.getId(entity.getType()), tagCompound));
                entity.remove(Entity.RemovalReason.DISCARDED);
//                ((ServerLevel)player.level()).removeEntity(entity);

                registerUsage(stack, player, difficultyScale);
            } else {
                Tools.error(player, "Please select a living entity!");
            }
        }
        return true;
    }
}
