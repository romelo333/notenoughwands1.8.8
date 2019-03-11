package romelo333.notenoughwands.setup;


import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import romelo333.notenoughwands.ModItems;
import romelo333.notenoughwands.ModRenderers;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientRegistration {

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModRenderers.init();
        ModItems.initModels();
    }
}
