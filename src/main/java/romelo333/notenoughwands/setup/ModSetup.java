package romelo333.notenoughwands.setup;

import mcjty.lib.setup.DefaultModSetup;
import net.neoforged.neoforge.common.MinecraftForge;
import net.neoforged.neoforge.fml.event.lifecycle.FMLCommonSetupEvent;
import romelo333.notenoughwands.ForgeEventHandlers;
import romelo333.notenoughwands.network.NEWPacketHandler;

public class ModSetup extends DefaultModSetup {

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);

        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
        NEWPacketHandler.registerMessages();
    }

    @Override
    protected void setupModCompat() {

    }

}
