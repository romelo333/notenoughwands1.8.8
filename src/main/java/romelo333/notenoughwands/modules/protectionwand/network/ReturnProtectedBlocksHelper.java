package romelo333.notenoughwands.modules.protectionwand.network;

import net.minecraft.core.BlockPos;

import java.util.HashSet;
import java.util.Set;

public class ReturnProtectedBlocksHelper {
    public static Set<BlockPos> blocks = new HashSet<>();
    public static Set<BlockPos> childBlocks = new HashSet<>();

    public static void setProtectedBlocks(PacketReturnProtectedBlocks message) {
        blocks = message.getBlocks();
        childBlocks = message.getChildBlocks();
    }
}
