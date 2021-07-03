package romelo333.notenoughwands.keys;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyBindings {

    public static KeyBinding wandModifier;
    public static KeyBinding wandSubMode;

    public static void init() {
        wandModifier = new KeyBinding("key.modifier", KeyConflictContext.IN_GAME, InputMappings.getKey("key.keyboard.equal"), "key.categories.notenoughwands");
        wandSubMode = new KeyBinding("key.submode", KeyConflictContext.IN_GAME, InputMappings.UNKNOWN, "key.categories.notenoughwands");
        ClientRegistry.registerKeyBinding(wandModifier);
        ClientRegistry.registerKeyBinding(wandSubMode);
    }
}
