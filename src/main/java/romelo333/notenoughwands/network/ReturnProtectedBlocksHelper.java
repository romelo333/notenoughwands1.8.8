package romelo333.notenoughwands.network;

import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Set;

public class ReturnProtectedBlocksHelper {
    public static Set<BlockPos> blocks = new HashSet<BlockPos>();
    public static Set<BlockPos> childBlocks = new HashSet<BlockPos>();

    public static void setProtectedBlocks(PacketReturnProtectedBlocks message) {
        blocks = message.getBlocks();
        childBlocks = message.getChildBlocks();
    }
}
