package romelo333.notenoughwands;

import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {

    public static FabricKeyBinding wandModifier;
    public static FabricKeyBinding wandSubMode;

    public static void init() {
        wandModifier = FabricKeyBinding.Builder.create(new Identifier(NotEnoughWands.MODID, "key.modifier"), InputUtil.Type.KEY_KEYBOARD, GLFW.GLFW_KEY_EQUAL, "key.categories.gameplay").build();
        wandSubMode = FabricKeyBinding.Builder.create(new Identifier(NotEnoughWands.MODID, "key.submode"), InputUtil.Type.KEY_KEYBOARD, -1, "key.categories.gameplay").build();
//        wandModifier = new KeyBinding("key.modifier", InputUtil.Type.KEY_KEYBOARD, GLFW.GLFW_KEY_EQUAL, "key.categories.gameplay");
//        wandSubMode = new KeyBinding("key.submode", InputUtil.Type.KEY_KEYBOARD, -1, "key.categories.gameplay");
        KeyBindingRegistry.INSTANCE.register(wandModifier);
        KeyBindingRegistry.INSTANCE.register(wandSubMode);
    }

    public static void registerBindings(GameOptions options) {
//        options.keysAll = ObjectArrays.concat(options.keysAll, wandModifier);
//        options.keysAll = ObjectArrays.concat(options.keysAll, wandSubMode);
    }
}
