package romelo333.notenoughwands.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Level;
import romelo333.notenoughwands.*;
import romelo333.notenoughwands.Items.GenericWand;
import romelo333.notenoughwands.network.PacketHandler;
import romelo333.notenoughwands.varia.WrenchChecker;

public abstract class CommonProxy {

    private Configuration mainConfig;

    public void preInit(FMLPreInitializationEvent e) {
        mainConfig = NotEnoughWands.config;
        ModItems.init();
        ModBlocks.init();
        readMainConfig();
        ModCrafting.init();
        GenericWand.setupChestLoot();
        FreezePotion.freezePotion = new FreezePotion();
        PacketHandler.registerMessages("assets/notenoughwands");
    }

    private void readMainConfig() {
        Configuration cfg = mainConfig;
        try {
            cfg.load();
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
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
    }

    public void postInit(FMLPostInitializationEvent e) {
        if (mainConfig.hasChanged()) {
            mainConfig.save();
        }
        mainConfig = null;
        WrenchChecker.init();
    }

}
