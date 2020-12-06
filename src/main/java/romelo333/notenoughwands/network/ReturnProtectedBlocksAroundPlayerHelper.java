package romelo333.notenoughwands.network;

import mcjty.lib.varia.DimensionId;
import romelo333.notenoughwands.ProtectedBlocks;

import java.util.Objects;

public class ReturnProtectedBlocksAroundPlayerHelper {

    public static void setProtectedBlocks(DimensionId dimension, PacketReturnProtectedBlocksAroundPlayer message) {
        if (!Objects.equals(ProtectedBlocks.clientSideWorld, dimension)) {
            ProtectedBlocks.clientSideProtectedBlocks.clear();
            ProtectedBlocks.clientSideWorld = dimension;
        }
        ProtectedBlocks.clientSideProtectedBlocks.putAll(message.getBlocks());
    }
}
