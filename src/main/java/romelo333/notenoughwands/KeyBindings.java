package romelo333.notenoughwands;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class KeyBindings {

    public static KeyBinding wandModifier;
    public static KeyBinding wandSubMode;

    public static void init() {
        wandModifier = new KeyBinding("key.modifier", KeyConflictContext.IN_GAME, Keyboard.KEY_EQUALS, "key.categories.notenoughwands");
        wandSubMode = new KeyBinding("key.submode", KeyConflictContext.IN_GAME, Keyboard.KEY_NONE, "key.categories.notenoughwands");
        ClientRegistry.registerKeyBinding(wandModifier);
        ClientRegistry.registerKeyBinding(wandSubMode);
    }
}
