package romelo333.notenoughwands;

import mcjty.lib.datagen.DataGen;
import mcjty.lib.modules.Modules;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import romelo333.notenoughwands.modules.buildingwands.BuildingWandsModule;
import romelo333.notenoughwands.modules.lightwand.LightModule;
import romelo333.notenoughwands.modules.protectionwand.ProtectionWandModule;
import romelo333.notenoughwands.modules.wands.WandsModule;
import romelo333.notenoughwands.setup.ClientSetup;
import romelo333.notenoughwands.setup.Config;
import romelo333.notenoughwands.setup.ModSetup;
import romelo333.notenoughwands.setup.Registration;

import java.util.function.Supplier;

@Mod(NotEnoughWands.MODID)
public class NotEnoughWands {
    public static final String MODID = "notenoughwands";

    @SuppressWarnings("PublicField")
    public static ModSetup setup = new ModSetup();
    private Modules modules = new Modules();
    public static NotEnoughWands instance;

    public NotEnoughWands() {
        instance = this;
        setupModules();

        Config.register(modules);

        Registration.register();

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(setup::init);
        bus.addListener(modules::init);
        bus.addListener(Config::onLoad);
        bus.addListener(this::onDataGen);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(modules::initClient);
            bus.addListener(ClientSetup::init);
            bus.addListener(LightModule::onTextureStitch);
            bus.addListener(ClientSetup::onRegisterKeyMappings);
        });
    }

    public static <T extends Item> Supplier<T> tab(Supplier<T> supplier) {
        return instance.setup.tab(supplier);
    }

    private void onDataGen(GatherDataEvent event) {
        DataGen datagen = new DataGen(MODID, event);
        modules.datagen(datagen);
        datagen.generate();
    }

    private void setupModules() {
        modules.register(new LightModule());
        modules.register(new WandsModule());
        modules.register(new ProtectionWandModule());
        modules.register(new BuildingWandsModule());
    }
}
