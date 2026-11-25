package romelo333.notenoughwands.modules.buildingwands;

import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.DeferredHolder;
import mcjty.lib.setup.DeferredItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import romelo333.notenoughwands.modules.buildingwands.data.BuildingWandData;
import romelo333.notenoughwands.modules.buildingwands.data.DisplacementWandData;
import romelo333.notenoughwands.modules.buildingwands.data.MovingWandData;
import romelo333.notenoughwands.modules.buildingwands.data.SwappingWandData;
import romelo333.notenoughwands.modules.buildingwands.items.BuildingWand;
import romelo333.notenoughwands.modules.buildingwands.items.DisplacementWand;
import romelo333.notenoughwands.modules.buildingwands.items.MovingWand;
import romelo333.notenoughwands.modules.buildingwands.items.SwappingWand;
import romelo333.notenoughwands.modules.wands.WandsModule;
import romelo333.notenoughwands.setup.Config;

import static mcjty.lib.datagen.DataGen.has;
import static romelo333.notenoughwands.NotEnoughWands.tab;
import static romelo333.notenoughwands.setup.Registration.COMPONENTS;
import static romelo333.notenoughwands.setup.Registration.ITEMS;

public class BuildingWandsModule implements IModule {

    public static final DeferredItem<Item> BUILDING_WAND = ITEMS.register("building_wand", tab(BuildingWand::new));
    public static final DeferredItem<Item> DISPLACEMENT_WAND = ITEMS.register("displacement_wand", tab(DisplacementWand::new));
    public static final DeferredItem<Item> MOVING_WAND = ITEMS.register("moving_wand", tab(MovingWand::new));
    public static final DeferredItem<Item> SWAPPING_WAND = ITEMS.register("swapping_wand", tab(SwappingWand::new));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BuildingWandData>> BUILDINGWAND_DATA = COMPONENTS.registerComponentType(
            "buildingwand",
            builder -> builder
                    .persistent(BuildingWandData.CODEC)
                    .networkSynchronized(BuildingWandData.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DisplacementWandData>> DISPLACEMENTWAND_DATA = COMPONENTS.registerComponentType(
            "displacementwand",
            builder -> builder
                    .persistent(DisplacementWandData.CODEC)
                    .networkSynchronized(DisplacementWandData.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MovingWandData>> MOVINGWAND_DATA = COMPONENTS.registerComponentType(
            "movingwand",
            builder -> builder
                    .persistent(MovingWandData.CODEC)
                    .networkSynchronized(MovingWandData.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SwappingWandData>> SWAPPINGWAND_DATA = COMPONENTS.registerComponentType(
            "swappingwand",
            builder -> builder
                    .persistent(SwappingWandData.CODEC)
                    .networkSynchronized(SwappingWandData.STREAM_CODEC));

    @Override
    public void init() {

    }

    @Override
    public void initClient() {

    }

    @Override
    public void initConfig() {
        BuildingWandsConfiguration.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
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
