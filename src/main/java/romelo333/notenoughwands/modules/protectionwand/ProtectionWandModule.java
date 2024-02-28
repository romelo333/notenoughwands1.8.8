package romelo333.notenoughwands.modules.protectionwand;

import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.setup.DeferredItem;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.eventbus.api.IEventBus;
import net.neoforged.neoforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.fml.event.lifecycle.FMLCommonSetupEvent;
import romelo333.notenoughwands.modules.protectionwand.items.ProtectionWand;
import romelo333.notenoughwands.modules.wands.WandsModule;
import romelo333.notenoughwands.setup.Config;

import static mcjty.lib.datagen.DataGen.has;
import static romelo333.notenoughwands.NotEnoughWands.tab;
import static romelo333.notenoughwands.setup.Registration.ITEMS;

public class ProtectionWandModule implements IModule {

    public static final DeferredItem<ProtectionWand> PROTECTION_WAND = ITEMS.register("protection_wand", tab(() -> new ProtectionWand(false)));
    public static final DeferredItem<ProtectionWand> MASTER_PROTECTION_WAND = ITEMS.register("master_protection_wand", tab(() -> new ProtectionWand(true)));

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {

    }

    @Override
    public void initConfig(IEventBus bus) {
        ProtectionWandConfiguration.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }

    @Override
    public void initDatagen(DataGen dataGen) {
        dataGen.add(
                Dob.itemBuilder(PROTECTION_WAND)
                        .handheldItem("item/protection_wand")
                        .shaped(builder -> builder
                                        .define('x', Items.COMPARATOR)
                                        .define('w', WandsModule.ADVANCED_WAND_CORE.get())
                                        .unlockedBy("core", has(WandsModule.WAND_CORE.get())),
                                "xo ", "ow ", "  w"
                        ),
                Dob.itemBuilder(MASTER_PROTECTION_WAND)
                        .handheldItem("item/master_protection_wand")
        );
    }
}
