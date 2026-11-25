package romelo333.notenoughwands.keys;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import romelo333.notenoughwands.NotEnoughWands;

public class KeyBindings {

    public static KeyMapping wandModifier;
    public static KeyMapping wandSubMode;

    public static void init() {
        KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(NotEnoughWands.MODID, "keys"));
        wandModifier = new KeyMapping("key.modifier", KeyConflictContext.IN_GAME, InputConstants.Type.KEYBOARD.getOrCreate(InputConstants.KEY_EQUALS), category);
        wandSubMode = new KeyMapping("key.submode", KeyConflictContext.IN_GAME, InputConstants.UNKNOWN, category);
    }
}
