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
        INSTANCE.registerMessage(PacketToggleMode.class, PacketToggleMode.class, nextID(), Side.SERVER);
        INSTANCE.registerMessage(PacketGetProtectedBlocks.class, PacketGetProtectedBlocks.class, nextID(), Side.SERVER);
        INSTANCE.registerMessage(PacketGetProtectedBlockCount.class, PacketGetProtectedBlockCount.class, nextID(), Side.SERVER);

        // Client side
        INSTANCE.registerMessage(PacketReturnProtectedBlocksHandler.class, PacketReturnProtectedBlocks.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(PacketReturnProtectedBlockCountHandler.class, PacketReturnProtectedBlockCount.class, nextID(), Side.CLIENT);
    }
}
