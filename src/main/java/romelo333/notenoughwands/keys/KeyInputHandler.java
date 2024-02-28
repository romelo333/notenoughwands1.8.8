package romelo333.notenoughwands.keys;

import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.eventbus.api.SubscribeEvent;
import romelo333.notenoughwands.network.NEWPacketHandler;
import romelo333.notenoughwands.network.PacketToggleMode;
import romelo333.notenoughwands.network.PacketToggleSubMode;

public class KeyInputHandler {

    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event) {
        if (KeyBindings.wandModifier.consumeClick()) {
            NEWPacketHandler.sendToServer(new PacketToggleMode());
        } else if (KeyBindings.wandSubMode.consumeClick()) {
            NEWPacketHandler.sendToServer(new PacketToggleSubMode());
        }
    }
}
