package romelo333.notenoughwands.network;

public class ReturnProtectedBlockCountHelper {
    public static int count = 0;

    public static void setProtectedBlocks(PacketReturnProtectedBlockCount message) {
        count = message.getCount();
    }
}
