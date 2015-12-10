package romelo333.notenoughwands;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class KeyBindings {

    public static KeyBinding wandModifier;

    public static void init() {
        wandModifier = new KeyBinding("key.modifier", Keyboard.KEY_EQUALS, "key.categories.notenoughwands");
        ClientRegistry.registerKeyBinding(wandModifier);
    }
}
