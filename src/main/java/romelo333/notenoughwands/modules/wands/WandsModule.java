package romelo333.notenoughwands.modules.wands;

import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.varia.SoundTools;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.modules.wands.Items.*;
import romelo333.notenoughwands.modules.wands.data.AccelerationWandData;
import romelo333.notenoughwands.modules.wands.data.CapturingWandData;
import romelo333.notenoughwands.setup.Config;

import java.util.function.Supplier;

import static mcjty.lib.datagen.DataGen.has;
import static romelo333.notenoughwands.NotEnoughWands.tab;
import static romelo333.notenoughwands.setup.Registration.*;

public class WandsModule implements IModule {

    public static final DeferredItem<Item> WAND_CORE = ITEMS.register("wand_core", WandCore::new);
    public static final DeferredItem<Item> ADVANCED_WAND_CORE = ITEMS.register("advanced_wand_core", tab(AdvancedWandCore::new));

    public static final DeferredItem<Item> ACCELERATION_WAND = ITEMS.register("acceleration_wand", tab(AccelerationWand::new));
    public static final DeferredItem<Item> CAPTURING_WAND = ITEMS.register("capturing_wand", tab(CapturingWand::new));
    public static final DeferredItem<Item> TELEPORTATION_WAND = ITEMS.register("teleportation_wand", tab(TeleportationWand::new));
    //    public static final Supplier<Item> FREEZING_WAND = ITEMS.register("freezing_wand", FreezingWand::new);
    //    public static final Supplier<Item> POTION_WAND = ITEMS.register("potion_wand", PotionWand::new);

    public static final Supplier<SoundEvent> TELEPORT_SOUND = SOUNDS.register("teleport", () -> SoundTools.createSoundEvent(ResourceLocation.fromNamespaceAndPath(NotEnoughWands.MODID, "teleport")));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CapturingWandData>> CAPTURINGWAND_DATA = COMPONENTS.registerComponentType(
            "capturingwand",
            builder -> builder
                    .persistent(CapturingWandData.CODEC)
                    .networkSynchronized(CapturingWandData.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<AccelerationWandData>> ACCELERATIONWAND_DATA = COMPONENTS.registerComponentType(
            "accelerationwandwand",
            builder -> builder
                    .persistent(AccelerationWandData.CODEC)
                    .networkSynchronized(AccelerationWandData.STREAM_CODEC));

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {

    }

    @Override
    public void initConfig(IEventBus bus) {
        WandsConfiguration.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.itemBuilder(WAND_CORE)
                        .handheldItem("item/wand_core")
                        .shaped(builder -> builder
                                        .define('X', Items.BLAZE_ROD)
                                        .define('n', Items.GOLD_NUGGET)
                                        .unlockedBy("rod", has(Items.BLAZE_ROD)),
                                "Xn ", "nXn", " nX"
                        ),
                Dob.itemBuilder(ADVANCED_WAND_CORE)
                        .handheldItem("item/advanced_wand_core")
                        .shaped(builder -> builder
                                        .define('t', Items.GHAST_TEAR)
                                        .define('x', Items.NETHER_STAR)
                                        .define('w', WAND_CORE.get())
                                        .unlockedBy("rod", has(Items.BLAZE_ROD)),
                                " x ", "twt", " d "
                        ),
                Dob.itemBuilder(ACCELERATION_WAND)
                        .handheldItem("item/acceleration_wand")
                        .shaped(builder -> builder
                                        .define('x', Items.CLOCK)
                                        .define('w', ADVANCED_WAND_CORE.get())
                                        .unlockedBy("core", has(WAND_CORE.get())),
                                "xr ", "rw ", "  w"
                        ),
                Dob.itemBuilder(CAPTURING_WAND)
                        .handheldItem("item/capturing_wand")
                        .shaped(builder -> builder
                                        .define('x', Items.ROTTEN_FLESH)
                                        .define('w', ADVANCED_WAND_CORE.get())
                                        .unlockedBy("core", has(WAND_CORE.get())),
                                "dx ", "xw ", "  w"
                        ),
                Dob.itemBuilder(TELEPORTATION_WAND)
                        .handheldItem("item/teleportation_wand")
                        .shaped(builder -> builder
                                        .define('w', WAND_CORE.get())
                                        .unlockedBy("core", has(WAND_CORE.get())),
                                "oo ", "ow ", "  w"
                        )
        );
    }
}
