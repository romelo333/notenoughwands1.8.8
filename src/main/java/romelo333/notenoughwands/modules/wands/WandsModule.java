package romelo333.notenoughwands.modules.wands;

import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.modules.wands.Items.*;
import romelo333.notenoughwands.setup.Config;

import static mcjty.lib.datagen.DataGen.has;
import static romelo333.notenoughwands.NotEnoughWands.tab;
import static romelo333.notenoughwands.setup.Registration.ITEMS;
import static romelo333.notenoughwands.setup.Registration.SOUNDS;

public class WandsModule implements IModule {

    public static final RegistryObject<Item> WAND_CORE = ITEMS.register("wand_core", WandCore::new);
    public static final RegistryObject<Item> ADVANCED_WAND_CORE = ITEMS.register("advanced_wand_core", tab(AdvancedWandCore::new));

    public static final RegistryObject<Item> ACCELERATION_WAND = ITEMS.register("acceleration_wand", tab(AccelerationWand::new));
    public static final RegistryObject<Item> CAPTURING_WAND = ITEMS.register("capturing_wand", tab(CapturingWand::new));
    public static final RegistryObject<Item> TELEPORTATION_WAND = ITEMS.register("teleportation_wand", tab(TeleportationWand::new));
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

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.itemBuilder(WAND_CORE)
                        .handheldItem("item/wand_core")
                        .shaped(builder -> builder.shaped(WAND_CORE.get())
                                        .define('X', Items.BLAZE_ROD)
                                        .define('n', Items.GOLD_NUGGET)
                                        .unlockedBy("rod", has(Items.BLAZE_ROD)),
                                "Xn ", "nXn", " nX"
                        ),
                Dob.itemBuilder(ADVANCED_WAND_CORE)
                        .handheldItem("item/advanced_wand_core")
                        .shaped(builder -> builder.shaped(ADVANCED_WAND_CORE.get())
                                        .define('t', Items.GHAST_TEAR)
                                        .define('x', Items.NETHER_STAR)
                                        .define('w', WAND_CORE.get())
                                        .unlockedBy("rod", has(Items.BLAZE_ROD)),
                                " x ", "twt", " d "
                        ),
                Dob.itemBuilder(ACCELERATION_WAND)
                        .handheldItem("item/acceleration_wand")
                        .shaped(builder -> builder.shaped(ACCELERATION_WAND.get())
                                        .define('x', Items.CLOCK)
                                        .define('w', ADVANCED_WAND_CORE.get())
                                        .unlockedBy("core", has(WAND_CORE.get())),
                                "xr ", "rw ", "  w"
                        ),
                Dob.itemBuilder(CAPTURING_WAND)
                        .handheldItem("item/capturing_wand")
                        .shaped(builder -> builder.shaped(CAPTURING_WAND.get())
                                        .define('x', Items.ROTTEN_FLESH)
                                        .define('w', ADVANCED_WAND_CORE.get())
                                        .unlockedBy("core", has(WAND_CORE.get())),
                                "dx ", "xw ", "  w"
                        ),
                Dob.itemBuilder(TELEPORTATION_WAND)
                        .handheldItem("item/teleportation_wand")
                        .shaped(builder -> builder.shaped(TELEPORTATION_WAND.get())
                                        .define('w', WAND_CORE.get())
                                        .unlockedBy("core", has(WAND_CORE.get())),
                                "oo ", "ow ", "  w"
                        )
        );
    }
}
