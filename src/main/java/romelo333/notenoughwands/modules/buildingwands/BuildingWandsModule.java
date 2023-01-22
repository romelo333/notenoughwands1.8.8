package romelo333.notenoughwands.modules.buildingwands;

import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import romelo333.notenoughwands.modules.buildingwands.items.BuildingWand;
import romelo333.notenoughwands.modules.buildingwands.items.DisplacementWand;
import romelo333.notenoughwands.modules.buildingwands.items.MovingWand;
import romelo333.notenoughwands.modules.buildingwands.items.SwappingWand;
import romelo333.notenoughwands.modules.wands.WandsModule;
import romelo333.notenoughwands.setup.Config;

import static mcjty.lib.datagen.DataGen.has;
import static romelo333.notenoughwands.NotEnoughWands.tab;
import static romelo333.notenoughwands.setup.Registration.ITEMS;

public class BuildingWandsModule implements IModule {

    public static final RegistryObject<Item> BUILDING_WAND = ITEMS.register("building_wand", tab(BuildingWand::new));
    public static final RegistryObject<Item> DISPLACEMENT_WAND = ITEMS.register("displacement_wand", tab(DisplacementWand::new));
    public static final RegistryObject<Item> MOVING_WAND = ITEMS.register("moving_wand", tab(MovingWand::new));
    public static final RegistryObject<Item> SWAPPING_WAND = ITEMS.register("swapping_wand", tab(SwappingWand::new));

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

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.itemBuilder(BUILDING_WAND)
                        .handheldItem("item/building_wand")
                        .shaped(builder -> builder
                                        .define('x', Items.BRICK)
                                        .define('w', WandsModule.WAND_CORE.get())
                                        .unlockedBy("core", has(WandsModule.WAND_CORE.get())),
                                "xx ", "xw ", "  w"
                        ),
                Dob.itemBuilder(DISPLACEMENT_WAND)
                        .handheldItem("item/displacement_wand")
                        .shaped(builder -> builder
                                        .define('x', Items.BRICK)
                                        .define('w', WandsModule.WAND_CORE.get())
                                        .unlockedBy("core", has(WandsModule.WAND_CORE.get())),
                                "ox ", "xw ", "  w"
                        ),
                Dob.itemBuilder(MOVING_WAND)
                        .handheldItem("item/moving_wand")
                        .shaped(builder -> builder
                                        .define('w', WandsModule.WAND_CORE.get())
                                        .unlockedBy("core", has(WandsModule.WAND_CORE.get())),
                                "ro ", "ow ", "  w"
                        ),
                Dob.itemBuilder(SWAPPING_WAND)
                        .handheldItem("item/swapping_wand")
                        .shaped(builder -> builder
                                        .define('x', Items.GLOWSTONE)
                                        .define('w', WandsModule.WAND_CORE.get())
                                        .unlockedBy("core", has(WandsModule.WAND_CORE.get())),
                                "Rx ", "xw ", "  w"
                        )
        );
    }
}
