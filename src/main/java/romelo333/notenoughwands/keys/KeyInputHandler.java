package romelo333.notenoughwands.keys;

import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import romelo333.notenoughwands.network.NEWPacketHandler;
import romelo333.notenoughwands.network.PacketToggleMode;
import romelo333.notenoughwands.network.PacketToggleSubMode;

public class KeyInputHandler {

    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event) {
        if (KeyBindings.wandModifier.consumeClick()) {
            NEWPacketHandler.INSTANCE.sendToServer(new PacketToggleMode());
        } else if (KeyBindings.wandSubMode.consumeClick()) {
            NEWPacketHandler.INSTANCE.sendToServer(new PacketToggleSubMode());
        }
    }
}
