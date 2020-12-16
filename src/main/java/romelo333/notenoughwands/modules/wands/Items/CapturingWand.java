package romelo333.notenoughwands.modules.wands.Items;


import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;
import romelo333.notenoughwands.modules.buildingwands.BlackListSettings;
import romelo333.notenoughwands.modules.wands.WandsConfiguration;
import romelo333.notenoughwands.varia.Tools;

import javax.annotation.Nullable;
import java.util.List;

public class CapturingWand extends GenericWand {

    public CapturingWand() {
        setup().usageFactor(3.0f);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> list, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, list, flagIn);
        CompoundNBT tagCompound = stack.getTag();
        // @todo 1.15 tooltips
        if (tagCompound != null) {
            if (tagCompound.contains("mob")) {
                String type = tagCompound.getString("type");
                if (!type.isEmpty()) {
                    EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(type));
                    if (entityType != null) {
                        list.add(new StringTextComponent(TextFormatting.GREEN + "Captured mob: ").appendSibling(entityType.getName()));
                    }
                }
            }
        }
        list.add(new StringTextComponent("Left click on creature to capture it."));
        list.add(new StringTextComponent("Right click on block to respawn creature."));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();
        World world = context.getWorld();
        ItemStack stack = player.getHeldItem(hand);
        BlockPos pos = context.getPos();
        if (!world.isRemote) {
            CompoundNBT tagCompound = stack.getOrCreateTag();
            if (tagCompound.contains("mob")) {
                INBT mobCompound = tagCompound.get("mob");
                String type = tagCompound.getString("type");
                LivingEntity entityLivingBase = createEntity(player, world, type);
                if (entityLivingBase == null) {
                    Tools.error(player, "Something went wrong trying to spawn creature!");
                    return ActionResultType.FAIL;
                }
                entityLivingBase.read((CompoundNBT) mobCompound);
                entityLivingBase.setLocationAndAngles(pos.getX()+.5, pos.getY()+1, pos.getZ()+.5, 0, 0);
                tagCompound.remove("mob");
                tagCompound.remove("type");
                world.addEntity(entityLivingBase);
            } else {
                Tools.error(player, "There is no mob captured in this wand!");
            }
        }
        return ActionResultType.SUCCESS;
    }

    private LivingEntity createEntity(PlayerEntity player, World world, String type) {
        EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(type));
        if (entityType != null) {
            return (LivingEntity) entityType.create(world);
        }
        return null;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        if (!player.getEntityWorld().isRemote) {
            if (entity instanceof LivingEntity) {
                if (stack.getOrCreateTag().contains("mob")) {
                    Tools.error(player, "There is already a mob in this wand!");
                    return true;
                }
                LivingEntity entityLivingBase = (LivingEntity) entity;
                if (entityLivingBase instanceof PlayerEntity) {
                    Tools.error(player, "I don't think that player would appreciate being captured!");
                    return true;
                }

                if ((!WandsConfiguration.allowHostile.get()) && entityLivingBase instanceof IMob) {
                    Tools.error(player, "It is not possible to capture hostile mobs with this wand!");
                    return true;
                }
                if ((!WandsConfiguration.allowPassive.get()) && !(entityLivingBase instanceof IMob)) {
                    Tools.error(player, "It is not possible to capture passive mobs with this wand!");
                    return true;
                }
                double cost = BlackListSettings.getBlacklistEntity(entity);
                if (cost <= 0.001f) {
                    Tools.error(player, "It is illegal to take this entity");
                    return true;
                }

                float difficultyScale = (float) (entityLivingBase.getMaxHealth() * cost * WandsConfiguration.difficultyMult.get() + WandsConfiguration.difficultyAdd.get());
                if (!checkUsage(stack, player, difficultyScale)) {
                    return true;
                }

                CompoundNBT tagCompound = new CompoundNBT();
                entityLivingBase.writeAdditional(tagCompound);  // @todo 1.15 is this right?
                stack.getOrCreateTag().put("mob", tagCompound);
                stack.getOrCreateTag().putString("type", entity.getType().getRegistryName().toString());
                ((ServerWorld)player.getEntityWorld()).removeEntity(entity);

                registerUsage(stack, player, difficultyScale);
            } else {
                Tools.error(player, "Please select a living entity!");
            }
        }
        return true;
    }
}
