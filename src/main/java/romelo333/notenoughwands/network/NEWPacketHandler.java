package romelo333.notenoughwands.network;


import mcjty.lib.network.IPayloadRegistrar;
import mcjty.lib.network.Networking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.modules.protectionwand.network.*;

public class NEWPacketHandler {

    private static IPayloadRegistrar registrar;

    public static void registerMessages() {
        registrar = Networking.registrar(NotEnoughWands.MODID)
                .versioned("1.0")
                .optional();

        // Server side
        registrar.play(PacketToggleMode.class, PacketToggleMode::create, handler -> handler.server(PacketToggleMode::handle));
        registrar.play(PacketToggleSubMode.class, PacketToggleSubMode::create, handler -> handler.server(PacketToggleSubMode::handle));
        registrar.play(PacketGetProtectedBlocks.class, PacketGetProtectedBlocks::create, handler -> handler.server(PacketGetProtectedBlocks::handle));
        registrar.play(PacketGetProtectedBlockCount.class, PacketGetProtectedBlockCount::create, handler -> handler.server(PacketGetProtectedBlockCount::handle));
        registrar.play(PacketGetProtectedBlocksAroundPlayer.class, PacketGetProtectedBlocksAroundPlayer::create, handler -> handler.server(PacketGetProtectedBlocksAroundPlayer::handle));

        // Client side
        registrar.play(PacketReturnProtectedBlocks.class, PacketReturnProtectedBlocks::create, handler -> handler.client(PacketReturnProtectedBlocks::handle));
        registrar.play(PacketReturnProtectedBlockCount.class, PacketReturnProtectedBlockCount::create, handler -> handler.client(PacketReturnProtectedBlockCount::handle));
        registrar.play(PacketReturnProtectedBlocksAroundPlayer.class, PacketReturnProtectedBlocksAroundPlayer::create, handler -> handler.client(PacketReturnProtectedBlocksAroundPlayer::handle));
    }

    public static <T> void sendToPlayer(T packet, Player player) {
        registrar.getChannel().sendTo(packet, ((ServerPlayer)player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static <T> void sendToServer(T packet) {
        registrar.getChannel().sendToServer(packet);
    }
}
