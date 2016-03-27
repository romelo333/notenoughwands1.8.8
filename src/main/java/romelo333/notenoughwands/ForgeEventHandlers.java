package romelo333.notenoughwands;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import romelo333.notenoughwands.varia.WrenchChecker;

import java.util.List;

public class ForgeEventHandlers {
    @SubscribeEvent
    public void onBlockBreakEvent (BlockEvent.BreakEvent event){
        World world = event.getWorld();
        if (world.isRemote) {
            return;
        }
        BlockPos pos = event.getPos();
        ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
        if (protectedBlocks.isProtected(world, pos)) {
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

    @SubscribeEvent
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        ItemStack heldItem = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
        if (heldItem == null || heldItem.getItem() == null) {
            return;
        }
        if (event.getEntityPlayer().isSneaking() && WrenchChecker.isAWrench(heldItem.getItem())) {
            // If the block is protected we prevent sneak-wrenching it.
            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(event.getWorld());
            BlockPos pos = event.getPos();
            if (protectedBlocks != null && protectedBlocks.isProtected(event.getWorld(), pos)) {
                event.setCanceled(true);
            }
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