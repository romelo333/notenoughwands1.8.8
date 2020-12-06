package romelo333.notenoughwands.setup;

import mcjty.lib.setup.DefaultModSetup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import romelo333.notenoughwands.ConfigSetup;
import romelo333.notenoughwands.ForgeEventHandlers;
import romelo333.notenoughwands.ModItems;
import romelo333.notenoughwands.network.NEWPacketHandler;

public class ModSetup extends DefaultModSetup {

    public ModSetup() {
        createTab("notenoughwands", () -> new ItemStack(Registration.WAND.get()));  // @todo 1.15
    }

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);

        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
        NEWPacketHandler.registerMessages("notenoughwands");

        ModItems.init();
        ConfigSetup.init();

//        GenericWand.setupConfig(ConfigSetup.getMainConfig()); @todo 1.15
    }

    @Override
    protected void setupModCompat() {

    }

}
