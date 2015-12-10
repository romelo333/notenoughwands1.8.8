package romelo333.notenoughwands.network;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ReturnProtectedBlockCountHelper {
    public static int count = 0;

    public static void setProtectedBlocks(PacketReturnProtectedBlockCount message) {
        count = message.getCount();
    }
}
