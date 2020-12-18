package romelo333.notenoughwands.modules.wands;

import mcjty.lib.modules.IModule;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.modules.wands.Items.*;
import romelo333.notenoughwands.setup.Config;

import static romelo333.notenoughwands.setup.Registration.ITEMS;
import static romelo333.notenoughwands.setup.Registration.SOUNDS;

public class WandsModule implements IModule {

    public static final RegistryObject<Item> WAND_CORE = ITEMS.register("wand_core", WandCore::new);
    public static final RegistryObject<Item> ADVANCED_WAND_CORE = ITEMS.register("advanced_wand_core", AdvancedWandCore::new);

    public static final RegistryObject<Item> ACCELERATION_WAND = ITEMS.register("acceleration_wand", AccelerationWand::new);
    public static final RegistryObject<Item> CAPTURING_WAND = ITEMS.register("capturing_wand", CapturingWand::new);
    public static final RegistryObject<Item> TELEPORTATION_WAND = ITEMS.register("teleportation_wand", TeleportationWand::new);
    //    public static final RegistryObject<Item> FREEZING_WAND = ITEMS.register("freezing_wand", FreezingWand::new);
    //    public static final RegistryObject<Item> POTION_WAND = ITEMS.register("potion_wand", PotionWand::new);

    public static final RegistryObject<SoundEvent> TELEPORT_SOUND = SOUNDS.register("teleport", () -> new SoundEvent(new ResourceLocation(NotEnoughWands.MODID, "teleport")));

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {

    }

    @Override
    public void initConfig() {
        WandsConfiguration.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }
}
