package romelo333.notenoughwands;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import romelo333.notenoughwands.Items.GenericWand;
import romelo333.notenoughwands.blocks.LightBlock;
import romelo333.notenoughwands.blocks.LightTE;

import java.util.List;

public class ForgeEventHandlers {

    @SubscribeEvent
    public void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        ModSounds.init(event.getRegistry());
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        ModBlocks.lightBlock = new LightBlock();
        event.getRegistry().register(ModBlocks.lightBlock);
        GameRegistry.registerTileEntity(LightTE.class, "LightTileEntity");
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(ModItems.wandCore);
        event.getRegistry().register(ModItems.advancedWandCore);

        for (GenericWand wand : GenericWand.getWands()) {
            event.getRegistry().register(wand);
        }
    }

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
                if (Config.interactionProtection) {
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
//            ItemStack heldItem = event.getPlayerEntity().getHeldItem(EnumHand.MAIN_HAND);
//            if (heldItem == null || heldItem.getItem() == null) {
//                return;
//            }
//            if (event.getPlayerEntity().isSneaking() && WrenchChecker.isAWrench(heldItem.getItem())) {
//                // If the block is protected we prevent sneak-wrenching it.
////            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(event.getWorld());
////            BlockPos pos = event.getPos();
//                if (protectedBlocks != null && protectedBlocks.isProtected(world, pos)) {
//                    event.setCanceled(true);
//                }
//            }

        }
    }

    @SubscribeEvent
    public void onLootLoad(LootTableLoadEvent event) {
        if (event.getName().equals(LootTableList.CHESTS_ABANDONED_MINESHAFT) ||
                event.getName().equals(LootTableList.CHESTS_IGLOO_CHEST) ||
                event.getName().equals(LootTableList.CHESTS_DESERT_PYRAMID) ||
                event.getName().equals(LootTableList.CHESTS_JUNGLE_TEMPLE) ||
                event.getName().equals(LootTableList.CHESTS_NETHER_BRIDGE) ||
                event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON) ||
                event.getName().equals(LootTableList.CHESTS_VILLAGE_BLACKSMITH)) {
            LootPool main = event.getTable().getPool("main");
            // Safety, check if the main lootpool is still present
            if (main != null) {
                GenericWand.setupChestLoot(main);
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