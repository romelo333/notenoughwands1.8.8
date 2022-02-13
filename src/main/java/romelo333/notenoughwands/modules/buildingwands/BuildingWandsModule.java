package romelo333.notenoughwands.modules.buildingwands;

import mcjty.lib.modules.IModule;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import romelo333.notenoughwands.modules.buildingwands.items.BuildingWand;
import romelo333.notenoughwands.modules.buildingwands.items.DisplacementWand;
import romelo333.notenoughwands.modules.buildingwands.items.MovingWand;
import romelo333.notenoughwands.modules.buildingwands.items.SwappingWand;
import romelo333.notenoughwands.setup.Config;

import static romelo333.notenoughwands.setup.Registration.ITEMS;

public class BuildingWandsModule implements IModule {

    public static final RegistryObject<Item> BUILDING_WAND = ITEMS.register("building_wand", BuildingWand::new);
    public static final RegistryObject<Item> DISPLACEMENT_WAND = ITEMS.register("displacement_wand", DisplacementWand::new);
    public static final RegistryObject<Item> MOVING_WAND = ITEMS.register("moving_wand", MovingWand::new);
    public static final RegistryObject<Item> SWAPPING_WAND = ITEMS.register("swapping_wand", SwappingWand::new);

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {

    }

    @Override
    public void initConfig() {
        BuildingWandsConfiguration.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }
}
