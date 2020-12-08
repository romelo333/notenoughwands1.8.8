package romelo333.notenoughwands.modules.protectionwand;

import mcjty.lib.modules.IModule;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import romelo333.notenoughwands.modules.protectionwand.items.ProtectionWand;
import romelo333.notenoughwands.setup.Config;

import static romelo333.notenoughwands.setup.Registration.ITEMS;

public class ProtectionWandModule implements IModule {

    public static final RegistryObject<ProtectionWand> PROTECTION_WAND = ITEMS.register("protection_wand", () -> new ProtectionWand(false));
    public static final RegistryObject<ProtectionWand> MASTER_PROTECTION_WAND = ITEMS.register("master_protection_wand", () -> new ProtectionWand(true));

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {

    }

    @Override
    public void initConfig() {
        ProtectionWandConfiguration.init(Config.SERVER_BUILDER, Config.CLIENT_BUILDER);
    }
}
