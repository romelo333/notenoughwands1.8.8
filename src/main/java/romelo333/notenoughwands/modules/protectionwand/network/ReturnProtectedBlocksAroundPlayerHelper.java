package romelo333.notenoughwands.modules.protectionwand.network;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import romelo333.notenoughwands.modules.protectionwand.ProtectedBlocks;

import java.util.Objects;

public class ReturnProtectedBlocksAroundPlayerHelper {

    public static void setProtectedBlocks(ResourceKey<Level> dimension, PacketReturnProtectedBlocksAroundPlayer message) {
        if (!Objects.equals(ProtectedBlocks.clientSideWorld, dimension)) {
            ProtectedBlocks.clientSideProtectedBlocks.clear();
            ProtectedBlocks.clientSideWorld = dimension;
        }
        ProtectedBlocks.clientSideProtectedBlocks.putAll(message.getBlocks());
    }
}
