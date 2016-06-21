package romelo333.notenoughwands.network;

import romelo333.notenoughwands.ProtectedBlocks;

public class ReturnProtectedBlocksAroundPlayerHelper {

    public static void setProtectedBlocks(int dimension, PacketReturnProtectedBlocksAroundPlayer message) {
        if (ProtectedBlocks.clientSideWorld != dimension) {
            ProtectedBlocks.clientSideProtectedBlocks.clear();
            ProtectedBlocks.clientSideWorld = dimension;
        }
        ProtectedBlocks.clientSideProtectedBlocks.putAll(message.getBlocks());
    }
}
