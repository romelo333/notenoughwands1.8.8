package romelo333.notenoughwands.network;


import mcjty.lib.network.PacketHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NEWPacketHandler {
    public static SimpleNetworkWrapper INSTANCE;

    public static void registerMessages(SimpleNetworkWrapper network) {
        INSTANCE = network;

        // Server side
        INSTANCE.registerMessage(PacketToggleMode.Handler.class, PacketToggleMode.class, PacketHandler.nextID(), Side.SERVER);
        INSTANCE.registerMessage(PacketToggleSubMode.Handler.class, PacketToggleSubMode.class, PacketHandler.nextID(), Side.SERVER);
        INSTANCE.registerMessage(PacketGetProtectedBlocks.Handler.class, PacketGetProtectedBlocks.class, PacketHandler.nextID(), Side.SERVER);
        INSTANCE.registerMessage(PacketGetProtectedBlockCount.Handler.class, PacketGetProtectedBlockCount.class, PacketHandler.nextID(), Side.SERVER);
        INSTANCE.registerMessage(PacketGetProtectedBlocksAroundPlayer.Handler.class, PacketGetProtectedBlocksAroundPlayer.class, PacketHandler.nextID(), Side.SERVER);

        // Client side
        INSTANCE.registerMessage(PacketReturnProtectedBlocks.Handler.class, PacketReturnProtectedBlocks.class, PacketHandler.nextID(), Side.CLIENT);
        INSTANCE.registerMessage(PacketReturnProtectedBlockCount.Handler.class, PacketReturnProtectedBlockCount.class, PacketHandler.nextID(), Side.CLIENT);
        INSTANCE.registerMessage(PacketReturnProtectedBlocksAroundPlayer.Handler.class, PacketReturnProtectedBlocksAroundPlayer.class, PacketHandler.nextID(), Side.CLIENT);
    }
}
