package romelo333.notenoughwands.setup;

import mcjty.lib.setup.DefaultModSetup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import romelo333.notenoughwands.ForgeEventHandlers;
import romelo333.notenoughwands.modules.wands.WandsModule;
import romelo333.notenoughwands.network.NEWPacketHandler;

public class ModSetup extends DefaultModSetup {

    public ModSetup() {
        createTab("notenoughwands", () -> new ItemStack(WandsModule.WAND_CORE.get()));
    }

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);

        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
        NEWPacketHandler.registerMessages("notenoughwands");
    }

    @Override
    protected void setupModCompat() {

    }

}
