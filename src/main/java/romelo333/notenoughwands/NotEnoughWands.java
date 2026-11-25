package romelo333.notenoughwands;

import mcjty.lib.modules.Modules;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.item.Item;
import romelo333.notenoughwands.modules.buildingwands.BuildingWandsModule;
import romelo333.notenoughwands.modules.lightwand.LightModule;
import romelo333.notenoughwands.modules.protectionwand.ProtectionWandModule;
import romelo333.notenoughwands.modules.wands.WandsModule;
import romelo333.notenoughwands.network.NEWPacketHandler;
import romelo333.notenoughwands.setup.Config;
import romelo333.notenoughwands.setup.ModSetup;
import romelo333.notenoughwands.setup.Registration;

import java.util.function.Function;

public class NotEnoughWands implements ModInitializer {
    public static final String MODID = "notenoughwands";
    public static final ModSetup setup = new ModSetup();
    public static final Modules MODULES = new Modules();

    @Override
    public void onInitialize() {
        MODULES.register(new LightModule());
        MODULES.register(new WandsModule());
        MODULES.register(new ProtectionWandModule());
        MODULES.register(new BuildingWandsModule());
        Config.register(MODULES);
        Registration.register();
        setup.init();
        MODULES.init();
        NEWPacketHandler.registerMessages();
        FabricEventHandlers.register();
    }

    public static <T extends Item> Function<Item.Properties, T> tab(Function<Item.Properties, T> factory) {
        return properties -> {
            T item = factory.apply(properties);
            setup.addTabItem(item::getDefaultInstance);
            return item;
        };
    }
}
