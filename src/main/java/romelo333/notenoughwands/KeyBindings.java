package romelo333.notenoughwands;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class KeyBindings {

    public static KeyBinding wandModifier;
    public static KeyBinding wandSubMode;

    public static void init() {
        wandModifier = new KeyBinding("key.modifier", InputUtil.Type.KEY_KEYBOARD, 0 /*@todo fabric: Keyboard.KEY_EQUALS*/, "key.categories.notenoughwands");
        wandSubMode = new KeyBinding("key.submode", InputUtil.Type.KEY_KEYBOARD, 0 /*@todo fabric: Keyboard.KEY_NONE*/, "key.categories.notenoughwands");
        // @todo fabric
//        ClientRegistry.registerKeyBinding(wandModifier);
//        ClientRegistry.registerKeyBinding(wandSubMode);
    }
}
