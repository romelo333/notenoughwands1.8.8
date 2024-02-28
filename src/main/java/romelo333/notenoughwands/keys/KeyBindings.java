package romelo333.notenoughwands.keys;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

public class KeyBindings {

    public static KeyMapping wandModifier;
    public static KeyMapping wandSubMode;

    public static void init() {
        wandModifier = new KeyMapping("key.modifier", KeyConflictContext.IN_GAME, InputConstants.getKey("key.keyboard.equal"), "key.categories.notenoughwands");
        wandSubMode = new KeyMapping("key.submode", KeyConflictContext.IN_GAME, InputConstants.UNKNOWN, "key.categories.notenoughwands");
    }
}
