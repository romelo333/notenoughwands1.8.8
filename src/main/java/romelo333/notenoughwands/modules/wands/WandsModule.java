package romelo333.notenoughwands.modules.wands;

import mcjty.lib.modules.IModule;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import romelo333.notenoughwands.modules.lightwand.items.IlluminationWand;
import romelo333.notenoughwands.modules.wands.Items.*;
import romelo333.notenoughwands.setup.Config;

import static romelo333.notenoughwands.setup.Registration.ITEMS;

public class WandsModule implements IModule {

    public static final RegistryObject<Item> ACCELERATION_WAND = ITEMS.register("acceleration_wand", AccelerationWand::new);
    public static final RegistryObject<Item> BUILDING_WAND = ITEMS.register("building_wand", BuildingWand::new);
    public static final RegistryObject<Item> CAPTURING_WAND = ITEMS.register("capturing_wand", CapturingWand::new);
    public static final RegistryObject<Item> DISPLACEMENT_WAND = ITEMS.register("displacement_wand", DisplacementWand::new);
    public static final RegistryObject<Item> MOVING_WAND = ITEMS.register("moving_wand", MovingWand::new);
//    public static final RegistryObject<Item> FREEZING_WAND = ITEMS.register("freezing_wand", FreezingWand::new);
//    public static final RegistryObject<Item> POTION_WAND = ITEMS.register("potion_wand", PotionWand::new);
    public static final RegistryObject<Item> SWAPPING_WAND = ITEMS.register("swapping_wand", SwappingWand::new);
    public static final RegistryObject<Item> TELEPORTATION_WAND = ITEMS.register("teleportation_wand", TeleportationWand::new);

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
