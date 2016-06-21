package romelo333.notenoughwands.network;


import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
    public static SimpleNetworkWrapper INSTANCE;
    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void registerMessages(String channelName) {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);

        // Server side
        INSTANCE.registerMessage(PacketToggleMode.Handler.class, PacketToggleMode.class, nextID(), Side.SERVER);
        INSTANCE.registerMessage(PacketGetProtectedBlocks.Handler.class, PacketGetProtectedBlocks.class, nextID(), Side.SERVER);
        INSTANCE.registerMessage(PacketGetProtectedBlockCount.Handler.class, PacketGetProtectedBlockCount.class, nextID(), Side.SERVER);
        INSTANCE.registerMessage(PacketGetProtectedBlocksAroundPlayer.Handler.class, PacketGetProtectedBlocksAroundPlayer.class, nextID(), Side.SERVER);

        // Client side
        INSTANCE.registerMessage(PacketReturnProtectedBlocks.Handler.class, PacketReturnProtectedBlocks.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(PacketReturnProtectedBlockCount.Handler.class, PacketReturnProtectedBlockCount.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(PacketReturnProtectedBlocksAroundPlayer.Handler.class, PacketReturnProtectedBlocksAroundPlayer.class, nextID(), Side.CLIENT);
    }
}
