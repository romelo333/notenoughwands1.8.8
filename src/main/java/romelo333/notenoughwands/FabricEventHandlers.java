package romelo333.notenoughwands;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import romelo333.notenoughwands.modules.buildingwands.items.DisplacementWand;
import romelo333.notenoughwands.modules.buildingwands.items.MovingWand;
import romelo333.notenoughwands.modules.protectionwand.ProtectedBlocks;
import romelo333.notenoughwands.modules.protectionwand.ProtectionWandConfiguration;
import romelo333.notenoughwands.modules.wands.Items.CapturingWand;

public final class FabricEventHandlers {

    private FabricEventHandlers() {
    }

    public static void register() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> !isProtected(world, pos));
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) ->
                isProtected(world, pos) ? InteractionResult.FAIL : InteractionResult.PASS);
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            if (isProtected(world, hit.getBlockPos()) && (world.isClientSide() || ProtectionWandConfiguration.interactionProtection.get())) {
                return InteractionResult.FAIL;
            }
            if (player.isSpectator()) {
                return InteractionResult.PASS;
            }
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getItem() instanceof MovingWand wand) {
                return world.isClientSide() ? InteractionResult.SUCCESS : wand.onItemUseFirst(stack, new UseOnContext(player, hand, hit));
            }
            if (stack.getItem() instanceof DisplacementWand wand) {
                return world.isClientSide() ? InteractionResult.SUCCESS : wand.onItemUseFirst(stack, new UseOnContext(player, hand, hit));
            }
            return InteractionResult.PASS;
        });
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
            if (!player.isSpectator() && player.getItemInHand(hand).getItem() instanceof CapturingWand wand
                    && wand.onLeftClickEntity(player.getItemInHand(hand), player, entity)) {
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        });
    }

    private static boolean isProtected(Level world, BlockPos pos) {
        return world.isClientSide() ? ProtectedBlocks.isProtectedClientSide(world, pos)
                : ProtectedBlocks.getProtectedBlocks(world).isProtected(world, pos);
    }
}
