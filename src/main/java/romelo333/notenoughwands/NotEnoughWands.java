package romelo333.notenoughwands;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class NotEnoughWands implements ModInitializer {
    public static final String MODID = "notenoughwands";
    public static final String VERSION = "1.7.5";
    public static final String MIN_FORGE11_VER = "13.19.0.2176";
    public static final String MIN_COFH_VER = "2.0.0";
    public static final String MIN_MCJTYLIB_VER = "3.0.0";

    public static Logger logger;
    public static File mainConfigDir;
    public static File modConfigDir;

    @Override
    public void onInitialize() {
        ModItems.init();
        ModBlocks.init();
        ModSounds.init();
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player.getStackInHand(hand).getItem() == ModItems.capturingWand && entity instanceof LivingEntity && !world.isClient()) {
                ModItems.capturingWand.captureMob(player.getStackInHand(hand), player, (LivingEntity) entity, hand);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
    }

    // @todo fabric
//    public static Configuration config;
//    public static CreativeTabs tabNew = new CreativeTabs("NotEnoughWands") {
//        @Override
//        public ItemStack getTabIconItem() {
//            return new ItemStack(ModItems.teleportationWand);
//        }
//    };
}
