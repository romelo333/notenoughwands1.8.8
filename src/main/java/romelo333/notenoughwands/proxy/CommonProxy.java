package romelo333.notenoughwands.proxy;

import mcjty.lib.McJtyLib;
import mcjty.lib.network.PacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.Level;
import romelo333.notenoughwands.*;
import romelo333.notenoughwands.network.NEWPacketHandler;
import romelo333.notenoughwands.varia.WrenchChecker;

public abstract class CommonProxy {

    private Configuration mainConfig;

    public void preInit(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
        McJtyLib.preInit(e);
        ModItems.init();

        mainConfig = NotEnoughWands.config;
        readMainConfig();
        FreezePotion.freezePotion = new FreezePotion();

        SimpleNetworkWrapper network = PacketHandler.registerMessages(NotEnoughWands.MODID, "notenoughwands");
        NEWPacketHandler.registerMessages(network);
    }

    private void readMainConfig() {
        Configuration cfg = mainConfig;
        try {
            cfg.load();
            cfg.addCustomCategoryComment(Config.CATEGORY_GENERAL, "General configuration");
            cfg.addCustomCategoryComment(Config.CATEGORY_WANDS, "Wand configuration");
            Config.init(cfg);
        } catch (Exception e1) {
            NotEnoughWands.logger.log(Level.ERROR, "Problem loading config file!", e1);
        } finally {
            if (mainConfig.hasChanged()) {
                mainConfig.save();
            }
        }
    }

    public void init(FMLInitializationEvent e) {
        ModCrafting.init(); // @todo still has to be fixed
    }

    public void postInit(FMLPostInitializationEvent e) {
        if (mainConfig.hasChanged()) {
            mainConfig.save();
        }
        mainConfig = null;
        WrenchChecker.init();
    }

}
