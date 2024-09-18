package romelo333.notenoughwands.network;


import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.modules.protectionwand.network.*;

public class NEWPacketHandler {

    public static void registerMessages(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(NotEnoughWands.MODID)
                .versioned("1.0")
                .optional();

        registrar.playToClient(PacketReturnProtectedBlocks.TYPE, PacketReturnProtectedBlocks.CODEC, PacketReturnProtectedBlocks::handle);
        registrar.playToClient(PacketReturnProtectedBlocksAroundPlayer.TYPE, PacketReturnProtectedBlocksAroundPlayer.CODEC, PacketReturnProtectedBlocksAroundPlayer::handle);
        registrar.playToClient(PacketReturnProtectedBlockCount.TYPE, PacketReturnProtectedBlockCount.CODEC, PacketReturnProtectedBlockCount::handle);

        registrar.playToServer(PacketGetProtectedBlockCount.TYPE, PacketGetProtectedBlockCount.CODEC, PacketGetProtectedBlockCount::handle);
        registrar.playToServer(PacketGetProtectedBlocks.TYPE, PacketGetProtectedBlocks.CODEC, PacketGetProtectedBlocks::handle);
        registrar.playToServer(PacketGetProtectedBlocksAroundPlayer.TYPE, PacketGetProtectedBlocksAroundPlayer.CODEC, PacketGetProtectedBlocksAroundPlayer::handle);
        registrar.playToServer(PacketToggleSubMode.TYPE, PacketToggleSubMode.CODEC, PacketToggleSubMode::handle);
        registrar.playToServer(PacketToggleMode.TYPE, PacketToggleMode.CODEC, PacketToggleMode::handle);
    }

    public static <T extends CustomPacketPayload> void sendToPlayer(T packet, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, packet);
    }

    public static <T extends CustomPacketPayload> void sendToServer(T packet) {
        PacketDistributor.sendToServer(packet);
    }
}
