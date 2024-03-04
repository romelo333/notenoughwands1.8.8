package romelo333.notenoughwands;

import mcjty.lib.datagen.DataGen;
import mcjty.lib.modules.Modules;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.api.distmarker.Dist;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fml.common.Mod;
import net.neoforged.neoforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.fml.loading.FMLEnvironment;
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
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        Dist dist = FMLEnvironment.dist;

        instance = this;
        setupModules(bus, dist);

        Config.register(bus, modules);

        Registration.register(bus);

        bus.addListener(setup::init);
        bus.addListener(modules::init);
        bus.addListener(Config::onLoad);
        bus.addListener(this::onDataGen);

        if (dist.isClient()) {
            bus.addListener(modules::initClient);
            bus.addListener(ClientSetup::init);
            bus.addListener(ClientSetup::onRegisterKeyMappings);
        }
    }

    public static <T extends Item> Supplier<T> tab(Supplier<T> supplier) {
        return instance.setup.tab(supplier);
    }

    private void onDataGen(GatherDataEvent event) {
        DataGen datagen = new DataGen(MODID, event);
        modules.datagen(datagen);
        datagen.generate();
    }

    private void setupModules(IEventBus bus, Dist dist) {
        modules.register(new LightModule(bus, dist));
        modules.register(new WandsModule());
        modules.register(new ProtectionWandModule());
        modules.register(new BuildingWandsModule());
    }
}
