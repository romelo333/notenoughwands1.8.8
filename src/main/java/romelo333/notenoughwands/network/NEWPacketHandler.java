package romelo333.notenoughwands.network;


import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.modules.protectionwand.network.*;

public class NEWPacketHandler {
    public static SimpleChannel INSTANCE;

    public static void registerMessages(String name) {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(NotEnoughWands.MODID, name))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        // Server side
        net.registerMessage(id(), PacketToggleMode.class, PacketToggleMode::toBytes, PacketToggleMode::new, PacketToggleMode::handle);
        net.registerMessage(id(), PacketToggleSubMode.class, PacketToggleSubMode::toBytes, PacketToggleSubMode::new, PacketToggleSubMode::handle);
        net.registerMessage(id(), PacketGetProtectedBlocks.class, PacketGetProtectedBlocks::toBytes, PacketGetProtectedBlocks::new, PacketGetProtectedBlocks::handle);
        net.registerMessage(id(), PacketGetProtectedBlockCount.class, PacketGetProtectedBlockCount::toBytes, PacketGetProtectedBlockCount::new, PacketGetProtectedBlockCount::handle);
        net.registerMessage(id(), PacketGetProtectedBlocksAroundPlayer.class, PacketGetProtectedBlocksAroundPlayer::toBytes, PacketGetProtectedBlocksAroundPlayer::new, PacketGetProtectedBlocksAroundPlayer::handle);

        // Client side
        net.registerMessage(id(), PacketReturnProtectedBlocks.class, PacketReturnProtectedBlocks::toBytes, PacketReturnProtectedBlocks::new, PacketReturnProtectedBlocks::handle);
        net.registerMessage(id(), PacketReturnProtectedBlockCount.class, PacketReturnProtectedBlockCount::toBytes, PacketReturnProtectedBlockCount::new, PacketReturnProtectedBlockCount::handle);
        net.registerMessage(id(), PacketReturnProtectedBlocksAroundPlayer.class, PacketReturnProtectedBlocksAroundPlayer::toBytes, PacketReturnProtectedBlocksAroundPlayer::new, PacketReturnProtectedBlocksAroundPlayer::handle);
    }

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }
}
