package romelo333.notenoughwands.network;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import romelo333.notenoughwands.varia.Coordinate;

import java.util.HashSet;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class ReturnProtectedBlocksHelper {
    public static Set<Coordinate> blocks = new HashSet<Coordinate>();
    public static Set<Coordinate> childBlocks = new HashSet<Coordinate>();

    public static void setProtectedBlocks(PacketReturnProtectedBlocks message) {
        blocks = message.getBlocks();
        childBlocks = message.getChildBlocks();
    }
}
