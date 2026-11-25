package romelo333.notenoughwands;

import mcjty.lib.datagen.DataGen;
import mcjty.lib.modules.Modules;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.transfer.energy.ItemAccessEnergyHandler;
import romelo333.notenoughwands.modules.buildingwands.BuildingWandsModule;
import romelo333.notenoughwands.modules.lightwand.LightModule;
import romelo333.notenoughwands.modules.protectionwand.ProtectionWandModule;
import romelo333.notenoughwands.modules.wands.Items.GenericWand;
import romelo333.notenoughwands.modules.wands.WandsModule;
import romelo333.notenoughwands.network.NEWPacketHandler;
import romelo333.notenoughwands.setup.ClientSetup;
import romelo333.notenoughwands.setup.Config;
import romelo333.notenoughwands.setup.ModSetup;
import romelo333.notenoughwands.setup.Registration;
import romelo333.notenoughwands.varia.WandRenderTypes;

import java.util.function.Function;

@Mod(NotEnoughWands.MODID)
public class NotEnoughWands {
    public static final String MODID = "notenoughwands";

    @SuppressWarnings("PublicField")
    public static ModSetup setup = new ModSetup();
    private Modules modules = new Modules();
    public static NotEnoughWands instance;

    public NotEnoughWands(ModContainer container, IEventBus bus, Dist dist) {

        instance = this;
        setupModules(bus, dist);

        Config.register(container, bus, modules);

        Registration.register(bus);

        bus.addListener(setup::init);
        bus.addListener(modules::init);
        bus.addListener(Config::onLoad);
        bus.addListener(Config::onReload);
        bus.addListener(this::onDataGen);
        bus.addListener(NEWPacketHandler::registerMessages);
        bus.addListener(this::onRegisterCapabilities);

        if (dist.isClient()) {
            bus.addListener(modules::initClient);
            bus.addListener(ClientSetup::init);
            bus.addListener(ClientSetup::onRegisterKeyMappings);
            bus.addListener(WandRenderTypes::registerPipelines);
        }
    }

    public static <T extends Item> Function<Item.Properties, T> tab(Function<Item.Properties, T> factory) {
        return properties -> {
            T item = factory.apply(properties);
            setup.addTabItem(item::getDefaultInstance);
            return item;
        };
    }

    private void onDataGen(GatherDataEvent.Client event) {
        DataGen datagen = new DataGen(MODID, event);
        modules.datagen(datagen, event.getLookupProvider());
        datagen.generate();
    }

    private void setupModules(IEventBus bus, Dist dist) {
        modules.register(new LightModule(bus, dist));
        modules.register(new WandsModule());
        modules.register(new ProtectionWandModule());
        modules.register(new BuildingWandsModule());
    }

    private void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        Registration.ITEMS.getRegister().getEntries().forEach(entry -> {
            Item item = entry.get();
            if (item instanceof GenericWand wand) {
                event.registerItem(Capabilities.Energy.ITEM,
                        (stack, access) -> new ItemAccessEnergyHandler(access, Registration.ENERGY_COMPONENT.get(), wand.calculateMaxPower()),
                        item);
            }
        });
    }
}
