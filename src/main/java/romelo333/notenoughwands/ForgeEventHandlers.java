package romelo333.notenoughwands;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import romelo333.notenoughwands.modules.protectionwand.ProtectedBlocks;
import romelo333.notenoughwands.modules.protectionwand.ProtectionWandConfiguration;

import java.util.List;

public class ForgeEventHandlers {

    @SubscribeEvent
    public void onBlockBreakEvent (BlockEvent.BreakEvent event){
        LevelAccessor world = event.getLevel();
        if (world.isClientSide()) {
            return;
        }
        BlockPos pos = event.getPos();
        ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks((Level)world);
        if (protectedBlocks.isProtected((Level)world, pos)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onDetonate(ExplosionEvent.Detonate event) {
        Level world = event.getLevel();
        if (world.isClientSide) {
            return;
        }
        ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
        if (!protectedBlocks.hasProtections()) {
            return;
        }


        List<BlockPos> affectedBlocks = event.getAffectedBlocks();

        int i = 0;
        while (i < affectedBlocks.size()) {
            BlockPos block = affectedBlocks.get(i);
            if (protectedBlocks.isProtected(world, block)) {
                affectedBlocks.remove(i);
            } else {
                i++;
            }
        }
    }

//    @SubscribeEvent
//    public void onPlayerLeftClickEvent(PlayerInteractEvent.LeftClickBlock event) {
//        if (!Config.interactionProtection) {
//            // If full protection is enabled we check in the normal onPlayerInteractEvent
//            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(event.getLevel());
//            BlockPos pos = event.getPos();
//            if (protectedBlocks != null && protectedBlocks.isProtected(event.getLevel(), pos)) {
//                event.setCanceled(true);
//            }
//        }
//    }

    @SubscribeEvent
    public void onPlayerInteractEvent(PlayerInteractEvent.LeftClickBlock event) {
        protect(event, event);
    }

    @SubscribeEvent
    public void onPlayerInteractEvent(PlayerInteractEvent.RightClickBlock event) {
        protect(event, event);
    }

    private static void protect(PlayerInteractEvent event, ICancellableEvent cancellable) {
        Level world = event.getLevel();
        BlockPos pos = event.getPos();

        if (world.isClientSide) {
            // Client side.
            if (ProtectedBlocks.isProtectedClientSide(world, pos)) {
                cancellable.setCanceled(true);
            }
        } else {
            // Server side
            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
            if (protectedBlocks != null && protectedBlocks.isProtected(world, pos)) {
                if (ProtectionWandConfiguration.interactionProtection.get()) {
                    cancellable.setCanceled(true);
                } else {
                    // We still allow right click interaction.
                    if (event instanceof PlayerInteractEvent.LeftClickBlock) {
                        cancellable.setCanceled(true);
                    }
                }
                return;
            }
//
//            ItemStack heldItem = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
//            if (heldItem == null || heldItem.getItem() == null) {
//                return;
//            }
//            if (event.getEntityPlayer().isSneaking() && WrenchChecker.isAWrench(heldItem.getItem())) {
//                // If the block is protected we prevent sneak-wrenching it.
////            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(event.getLevel());
////            BlockPos pos = event.getPos();
//                if (protectedBlocks != null && protectedBlocks.isProtected(world, pos)) {
//                    event.setCanceled(true);
//                }
//            }

        }
    }

//    @SubscribeEvent
//    public void onlivingUpdate(LivingEvent.LivingUpdateEvent event){
//        PotionEffect effect = event.entityLiving.getActivePotionEffect(FreezePotion.freezePotion);
//        if (effect != null) {
//            event.setCanceled(true);
//        }
//    }
//
//    @SubscribeEvent
//    public void onlivingHurt(LivingHurtEvent event){
//        PotionEffect effect = event.entityLiving.getActivePotionEffect(FreezePotion.freezePotion);
//        if (effect != null) {
//            event.setCanceled(true);
//        }
//    }
}