package romelo333.notenoughwands;

import com.google.common.collect.ObjectArrays;
import net.minecraft.client.settings.GameOptions;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {

    public static KeyBinding wandModifier;
    public static KeyBinding wandSubMode;

    public static void init() {
        wandModifier = new KeyBinding("key.modifier", InputUtil.Type.KEY_KEYBOARD, GLFW.GLFW_KEY_EQUAL, "key.categories.gameplay");
        wandSubMode = new KeyBinding("key.submode", InputUtil.Type.KEY_KEYBOARD, -1, "key.categories.gameplay");
    }

    public static void registerBindings(GameOptions options) {
        options.keysAll = ObjectArrays.concat(options.keysAll, wandModifier);
        options.keysAll = ObjectArrays.concat(options.keysAll, wandSubMode);
    }
}
