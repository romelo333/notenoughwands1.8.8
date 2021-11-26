package romelo333.notenoughwands.modules.protectionwand.network;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import romelo333.notenoughwands.modules.protectionwand.ProtectedBlocks;

import java.util.Objects;

public class ReturnProtectedBlocksAroundPlayerHelper {

    public static void setProtectedBlocks(RegistryKey<World> dimension, PacketReturnProtectedBlocksAroundPlayer message) {
        if (!Objects.equals(ProtectedBlocks.clientSideWorld, dimension)) {
            ProtectedBlocks.clientSideProtectedBlocks.clear();
            ProtectedBlocks.clientSideWorld = dimension;
        }
        ProtectedBlocks.clientSideProtectedBlocks.putAll(message.getBlocks());
    }
}
