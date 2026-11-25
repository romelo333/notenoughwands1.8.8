package romelo333.notenoughwands.setup;

import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents;
import mcjty.lib.modules.Modules;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.modules.protectionwand.ProtectionWandConfiguration;
import romelo333.notenoughwands.modules.wands.WandsConfiguration;

public class Config {

    public static final String CATEGORY_GENERAL = "general";

    public static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

    public static ModConfigSpec SERVER_CONFIG;
    public static ModConfigSpec CLIENT_CONFIG;

    public static void register(Modules modules) {
        SERVER_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
        CLIENT_BUILDER.comment("General settings").push(CATEGORY_GENERAL);

        modules.initConfig();

        SERVER_BUILDER.pop();
        CLIENT_BUILDER.pop();

        SERVER_CONFIG = SERVER_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();

        ModConfigEvents.loading(NotEnoughWands.MODID).register(Config::refresh);
        ModConfigEvents.reloading(NotEnoughWands.MODID).register(Config::refresh);
        ConfigRegistry.INSTANCE.register(NotEnoughWands.MODID, ModConfig.Type.CLIENT, CLIENT_CONFIG);
        ConfigRegistry.INSTANCE.register(NotEnoughWands.MODID, ModConfig.Type.SERVER, SERVER_CONFIG);
    }

    private static void refresh(ModConfig config) {
        if (config.getSpec() == SERVER_CONFIG) {
            ProtectionWandConfiguration.cachedClientSideProtection = ProtectionWandConfiguration.clientSideProtection.get();
            WandsConfiguration.setWandUsage(WandsConfiguration.wandUsage.get());
        }
    }
}
