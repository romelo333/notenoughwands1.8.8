package romelo333.notenoughwands.keys;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import romelo333.notenoughwands.network.NEWPacketHandler;
import romelo333.notenoughwands.network.PacketToggleMode;
import romelo333.notenoughwands.network.PacketToggleSubMode;

public class KeyInputHandler {

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().player == null) {
            return;
        }
        while (KeyBindings.wandModifier.consumeClick()) {
            NEWPacketHandler.sendToServer(new PacketToggleMode());
        }
        while (KeyBindings.wandSubMode.consumeClick()) {
            NEWPacketHandler.sendToServer(new PacketToggleSubMode());
        }
    }
}
