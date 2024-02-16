package romelo333.notenoughwands.network;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.modules.protectionwand.network.*;

import static mcjty.lib.network.PlayPayloadContext.wrap;

public class NEWPacketHandler {
    private static SimpleChannel INSTANCE;

    public static void registerMessages(String name) {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(NotEnoughWands.MODID, name))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        // Server side
        net.registerMessage(id(), PacketToggleMode.class, PacketToggleMode::write, PacketToggleMode::create, wrap(PacketToggleMode::handle));
        net.registerMessage(id(), PacketToggleSubMode.class, PacketToggleSubMode::write, PacketToggleSubMode::create, wrap(PacketToggleSubMode::handle));
        net.registerMessage(id(), PacketGetProtectedBlocks.class, PacketGetProtectedBlocks::write, PacketGetProtectedBlocks::create, wrap(PacketGetProtectedBlocks::handle));
        net.registerMessage(id(), PacketGetProtectedBlockCount.class, PacketGetProtectedBlockCount::write, PacketGetProtectedBlockCount::create, wrap(PacketGetProtectedBlockCount::handle));
        net.registerMessage(id(), PacketGetProtectedBlocksAroundPlayer.class, PacketGetProtectedBlocksAroundPlayer::write, PacketGetProtectedBlocksAroundPlayer::create, wrap(PacketGetProtectedBlocksAroundPlayer::handle));

        // Client side
        net.registerMessage(id(), PacketReturnProtectedBlocks.class, PacketReturnProtectedBlocks::write, PacketReturnProtectedBlocks::create, wrap(PacketReturnProtectedBlocks::handle));
        net.registerMessage(id(), PacketReturnProtectedBlockCount.class, PacketReturnProtectedBlockCount::write, PacketReturnProtectedBlockCount::create, wrap(PacketReturnProtectedBlockCount::handle));
        net.registerMessage(id(), PacketReturnProtectedBlocksAroundPlayer.class, PacketReturnProtectedBlocksAroundPlayer::write, PacketReturnProtectedBlocksAroundPlayer::create, wrap(PacketReturnProtectedBlocksAroundPlayer::handle));
    }

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }

    public static <T> void sendToPlayer(T packet, Player player) {
        INSTANCE.sendTo(packet, ((ServerPlayer)player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static <T> void sendToServer(T packet) {
        INSTANCE.sendToServer(packet);
    }
}
