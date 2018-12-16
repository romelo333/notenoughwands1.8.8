package romelo333.notenoughwands;

import net.fabricmc.api.ClientModInitializer;

public class ClientInit implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModRenderers.init();
        KeyBindings.init();
    }
}
