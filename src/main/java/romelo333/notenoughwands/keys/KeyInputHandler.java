package romelo333.notenoughwands.keys;

import romelo333.notenoughwands.network.NEWPacketHandler;
import romelo333.notenoughwands.network.PacketToggleMode;
import romelo333.notenoughwands.network.PacketToggleSubMode;

public class KeyInputHandler {

    public static void onClientTick() {
        while (KeyBindings.wandModifier.consumeClick()) {
            NEWPacketHandler.sendToServer(new PacketToggleMode());
        }
        while (KeyBindings.wandSubMode.consumeClick()) {
            NEWPacketHandler.sendToServer(new PacketToggleSubMode());
        }
    }
}
