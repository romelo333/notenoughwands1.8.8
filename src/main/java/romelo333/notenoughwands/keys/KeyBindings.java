package romelo333.notenoughwands.keys;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import romelo333.notenoughwands.NotEnoughWands;

public class KeyBindings {

    public static KeyMapping wandModifier;
    public static KeyMapping wandSubMode;

    public static void init() {
        KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(NotEnoughWands.MODID, "keys"));
        wandModifier = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.modifier", InputConstants.Type.KEYBOARD, InputConstants.KEY_EQUALS, category));
        wandSubMode = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.submode", InputConstants.Type.KEYBOARD, InputConstants.UNKNOWN.getValue(), category));
    }
}
