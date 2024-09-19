package romelo333.notenoughwands.modules.wands.Items;


import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.varia.ComponentFactory;
import mcjty.lib.varia.SoundTools;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import romelo333.notenoughwands.modules.wands.WandsConfiguration;
import romelo333.notenoughwands.modules.wands.WandsModule;
import romelo333.notenoughwands.varia.Tools;

import javax.annotation.Nullable;
import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class TeleportationWand extends GenericWand {

    private final TooltipBuilder tooltipBuilder = new TooltipBuilder()
            .info(key("message.notenoughwands.shiftmessage"))
            .infoShift(header());


    public TeleportationWand() {
        this.usageFactor(2.0f);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, list, flagIn);
        tooltipBuilder.makeTooltip(mcjty.lib.varia.Tools.getId(this), stack, list, flagIn);

        if (WandsConfiguration.teleportThroughWalls.get()) {
            list.add(ComponentFactory.translatable("message.notenoughwands.teleportation_wand.sneak1").withStyle(ChatFormatting.GOLD));
        } else {
            list.add(ComponentFactory.translatable("message.notenoughwands.teleportation_wand.sneak2").withStyle(ChatFormatting.GOLD));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            if (!checkUsage(stack, player, 1.0f)) {
                return InteractionResultHolder.pass(stack);
            }
            Vec3 lookVec = player.getLookAngle();
            Vec3 start = new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
            int distance = WandsConfiguration.maxdist.get();
            boolean gothrough = false;
            if (player.isShiftKeyDown()) {
                if (WandsConfiguration.teleportThroughWalls.get()) {
                    gothrough = true;
                }
                distance /= 2;
            }

            Vec3 end = start.add(lookVec.x * distance, lookVec.y * distance, lookVec.z * distance);
            HitResult position;
            if (gothrough) {
                position = null;
            } else {
                ClipContext context = new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player);
                position = world.clip(context);
            }
            if (position == null) {
                if (gothrough) {
                    // First check if the destination is safe
                    BlockPos blockPos = new BlockPos((int) end.x, (int) end.y, (int) end.z);
                    if (!(world.isEmptyBlock(blockPos) && world.isEmptyBlock(blockPos.above()))) {
                        Tools.error(player, "You will suffocate if you teleport there!");
                        return InteractionResultHolder.pass(stack);
                    }
                }
                player.teleportTo(end.x, end.y, end.z);
            } else {
                BlockHitResult result = (BlockHitResult) position;
                BlockPos blockPos = result.getBlockPos();
                int x = blockPos.getX();
                int y = blockPos.getY();
                int z = blockPos.getZ();
                if (world.isEmptyBlock(blockPos.above()) && world.isEmptyBlock(blockPos.above(2))) {
                    player.teleportTo(x+.5, y + 1, z+.5);
                } else {
                    switch (result.getDirection()) {
                        case DOWN -> player.teleportTo(x + .5, y - 2, z + .5);
                        case UP -> {
                            Tools.error(player, "You will suffocate if you teleport there!");
                            return InteractionResultHolder.pass(stack);
                        }
                        case NORTH -> player.teleportTo(x + .5, y, z - 1 + .5);
                        case SOUTH -> player.teleportTo(x + .5, y, z + 1 + .5);
                        case WEST -> player.teleportTo(x - 1 + .5, y, z + .5);
                        case EAST -> player.teleportTo(x + 1 + .5, y, z + .5);
                    }
                }
            }
            registerUsage(stack, player, 1.0f);
            if (WandsConfiguration.teleportVolume.get() >= 0.01) {
                SoundEvent teleport = WandsModule.TELEPORT_SOUND.get();
                SoundTools.playSound(player.getCommandSenderWorld(), teleport, player.getX(), player.getY(), player.getZ(), WandsConfiguration.teleportVolume.get(), 1.0f);
            }
        }
        return InteractionResultHolder.pass(stack);
    }
}
