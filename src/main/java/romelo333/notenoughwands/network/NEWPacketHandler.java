package romelo333.notenoughwands.network;


import mcjty.lib.network.PacketHandler;
import mcjty.lib.thirteen.ChannelBuilder;
import mcjty.lib.thirteen.SimpleChannel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import romelo333.notenoughwands.NotEnoughWands;

public class NEWPacketHandler {
    public static SimpleNetworkWrapper INSTANCE;

    public static void registerMessages(String name) {
        SimpleChannel net = ChannelBuilder
                .named(new ResourceLocation(NotEnoughWands.MODID, name))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net.getNetwork();

        // Server side
        net.registerMessageServer(id(), PacketToggleMode.class, PacketToggleMode::toBytes, PacketToggleMode::new, PacketToggleMode::handle);
        net.registerMessageServer(id(), PacketToggleSubMode.class, PacketToggleSubMode::toBytes, PacketToggleSubMode::new, PacketToggleSubMode::handle);
        net.registerMessageServer(id(), PacketGetProtectedBlocks.class, PacketGetProtectedBlocks::toBytes, PacketGetProtectedBlocks::new, PacketGetProtectedBlocks::handle);
        net.registerMessageServer(id(), PacketGetProtectedBlockCount.class, PacketGetProtectedBlockCount::toBytes, PacketGetProtectedBlockCount::new, PacketGetProtectedBlockCount::handle);
        net.registerMessageServer(id(), PacketGetProtectedBlocksAroundPlayer.class, PacketGetProtectedBlocksAroundPlayer::toBytes, PacketGetProtectedBlocksAroundPlayer::new, PacketGetProtectedBlocksAroundPlayer::handle);

        // Client side
        net.registerMessageClient(id(), PacketReturnProtectedBlocks.class, PacketReturnProtectedBlocks::toBytes, PacketReturnProtectedBlocks::new, PacketReturnProtectedBlocks::handle);
        net.registerMessageClient(id(), PacketReturnProtectedBlockCount.class, PacketReturnProtectedBlockCount::toBytes, PacketReturnProtectedBlockCount::new, PacketReturnProtectedBlockCount::handle);
        net.registerMessageClient(id(), PacketReturnProtectedBlocksAroundPlayer.class, PacketReturnProtectedBlocksAroundPlayer::toBytes, PacketReturnProtectedBlocksAroundPlayer::new, PacketReturnProtectedBlocksAroundPlayer::handle);
    }

    private static int id() {
        return PacketHandler.nextPacketID();
    }
}
