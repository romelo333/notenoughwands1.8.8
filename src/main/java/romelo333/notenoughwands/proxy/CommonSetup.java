package romelo333.notenoughwands.proxy;

import mcjty.lib.network.PacketHandler;
import mcjty.lib.setup.DefaultCommonSetup;
import net.minecraft.item.ItemStack;
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

import java.io.File;

public class CommonSetup extends DefaultCommonSetup {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
        ModItems.init();

        mainConfig = new Configuration(new File(modConfigDir, "notenoughwands.cfg"));
        readMainConfig();
        FreezePotion.freezePotion = new FreezePotion();

        SimpleNetworkWrapper network = PacketHandler.registerMessages(NotEnoughWands.MODID, "notenoughwands");
        NEWPacketHandler.registerMessages(network);
    }

    @Override
    public void createTabs() {
        createTab("NotEnoughWands", new ItemStack(ModItems.teleportationWand));
    }

    private void readMainConfig() {
        Configuration cfg = mainConfig;
        try {
            cfg.load();
            cfg.addCustomCategoryComment(Config.CATEGORY_GENERAL, "General configuration");
            cfg.addCustomCategoryComment(Config.CATEGORY_WANDS, "Wand configuration");
            Config.init(cfg);
        } catch (Exception e1) {
            getLogger().log(Level.ERROR, "Problem loading config file!", e1);
        } finally {
            if (mainConfig.hasChanged()) {
                mainConfig.save();
            }
        }
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        ModCrafting.init(); // @todo still has to be fixed
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
        if (mainConfig.hasChanged()) {
            mainConfig.save();
        }
        mainConfig = null;
        WrenchChecker.init();
    }

}
