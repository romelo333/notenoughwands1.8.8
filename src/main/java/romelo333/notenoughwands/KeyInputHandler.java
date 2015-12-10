package romelo333.notenoughwands;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import romelo333.notenoughwands.network.PacketHandler;
import romelo333.notenoughwands.network.PacketToggleMode;

public class KeyInputHandler {

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (KeyBindings.wandModifier.isPressed()) {
            PacketHandler.INSTANCE.sendToServer(new PacketToggleMode());
        }
    }
}
