package romelo333.notenoughwands.proxy;

import romelo333.notenoughwands.ModCrafting;
import romelo333.notenoughwands.ModItems;

public abstract class CommonProxy {

    public void preInit() {
        ModItems.init();

//        mainConfig = NotEnoughWands.config;
//        readMainConfig();
//        FreezePotion.freezePotion = new FreezePotion();

//        SimpleNetworkWrapper network = PacketHandler.registerMessages(NotEnoughWands.MODID, "notenoughwands");
//        NEWPacketHandler.registerMessages(network);
    }

    private void readMainConfig() {
//        Configuration cfg = mainConfig;
//        try {
//            cfg.load();
//            cfg.addCustomCategoryComment(Config.CATEGORY_GENERAL, "General configuration");
//            cfg.addCustomCategoryComment(Config.CATEGORY_WANDS, "Wand configuration");
//            Config.init(cfg);
//        } catch (Exception e1) {
//            NotEnoughWands.logger.log(Level.ERROR, "Problem loading config file!", e1);
//        } finally {
//            if (mainConfig.hasChanged()) {
//                mainConfig.save();
//            }
//        }
    }

    public void init() {
        ModCrafting.init(); // @todo still has to be fixed
    }

    public void postInit() {
//        if (mainConfig.hasChanged()) {
//            mainConfig.save();
//        }
//        mainConfig = null;
//        WrenchChecker.init();
    }

}
