package romelo333.notenoughwands.keys;

import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import romelo333.notenoughwands.network.NEWPacketHandler;
import romelo333.notenoughwands.network.PacketToggleMode;
import romelo333.notenoughwands.network.PacketToggleSubMode;

public class KeyInputHandler {

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (KeyBindings.wandModifier.isPressed()) {
            NEWPacketHandler.INSTANCE.sendToServer(new PacketToggleMode());
        } else if (KeyBindings.wandSubMode.isPressed()) {
            NEWPacketHandler.INSTANCE.sendToServer(new PacketToggleSubMode());
        }
    }
}
