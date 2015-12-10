package romelo333.notenoughwands.network;

import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashSet;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class ReturnProtectedBlocksHelper {
    public static Set<BlockPos> blocks = new HashSet<BlockPos>();
    public static Set<BlockPos> childBlocks = new HashSet<BlockPos>();

    public static void setProtectedBlocks(PacketReturnProtectedBlocks message) {
        blocks = message.getBlocks();
        childBlocks = message.getChildBlocks();
    }
}
