package romelo333.notenoughwands;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import romelo333.notenoughwands.modules.protectionwand.ProtectedBlocks;
import romelo333.notenoughwands.modules.protectionwand.ProtectionWandConfiguration;

import java.util.List;

public class ForgeEventHandlers {

    @SubscribeEvent
    public void onBlockBreakEvent (BlockEvent.BreakEvent event){
        IWorld world = event.getWorld();
        if (world.isRemote()) {
            return;
        }
        BlockPos pos = event.getPos();
        ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks((World)world);
        if (protectedBlocks.isProtected((World)world, pos)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onDetonate(ExplosionEvent.Detonate event) {
        World world = event.getWorld();
        if (world.isRemote) {
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
//            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(event.getWorld());
//            BlockPos pos = event.getPos();
//            if (protectedBlocks != null && protectedBlocks.isProtected(event.getWorld(), pos)) {
//                event.setCanceled(true);
//            }
//        }
//    }

    @SubscribeEvent
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (!event.isCancelable()) {
            return;
        }

        World world = event.getWorld();
        BlockPos pos = event.getPos();

        if (world.isRemote) {
            // Client side.
            if (ProtectedBlocks.isProtectedClientSide(world, pos)) {
                event.setCanceled(true);
            }
        } else {
            // Server side
            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
            if (protectedBlocks != null && protectedBlocks.isProtected(world, pos)) {
                if (ProtectionWandConfiguration.interactionProtection.get()) {
                    event.setCanceled(true);
                } else {
                    // We still allow right click interaction.
                    if (event instanceof PlayerInteractEvent.LeftClickBlock) {
                        event.setCanceled(true);
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
////            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(event.getWorld());
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