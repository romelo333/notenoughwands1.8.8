package romelo333.notenoughwands.setup;


import net.minecraftforge.client.event.ModelRegistryEvent;
import romelo333.notenoughwands.ModItems;
import romelo333.notenoughwands.ModRenderers;

public class ClientRegistration {

    // @todo 1.15 call me somewhere
    public static void registerModels(ModelRegistryEvent event) {
        ModRenderers.init();
        ModItems.initModels();
    }
}
