package romelo333.notenoughwands.proxy;

import mcjty.lib.setup.DefaultCommonSetup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import romelo333.notenoughwands.ConfigSetup;
import romelo333.notenoughwands.ForgeEventHandlers;
import romelo333.notenoughwands.ModItems;
import romelo333.notenoughwands.network.NEWPacketHandler;
import romelo333.notenoughwands.varia.WrenchChecker;

public class CommonSetup extends DefaultCommonSetup {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());

        NEWPacketHandler.registerMessages("notenoughwands");

        ConfigSetup.init();
        ModItems.init();
    }

    @Override
    public void createTabs() {
        createTab("NotEnoughWands", new ItemStack(ModItems.teleportationWand));
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
        ConfigSetup.postInit();
        WrenchChecker.init();
    }

}
