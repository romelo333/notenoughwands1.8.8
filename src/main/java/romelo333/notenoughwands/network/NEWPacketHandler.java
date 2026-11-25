package romelo333.notenoughwands.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import romelo333.notenoughwands.modules.protectionwand.network.*;

public final class NEWPacketHandler {

    private NEWPacketHandler() {
    }

    public static void registerMessages() {
        PayloadTypeRegistry.clientboundPlay().register(PacketReturnProtectedBlocks.TYPE, PacketReturnProtectedBlocks.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(PacketReturnProtectedBlocksAroundPlayer.TYPE, PacketReturnProtectedBlocksAroundPlayer.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(PacketReturnProtectedBlockCount.TYPE, PacketReturnProtectedBlockCount.CODEC);

        PayloadTypeRegistry.serverboundPlay().register(PacketGetProtectedBlockCount.TYPE, PacketGetProtectedBlockCount.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PacketGetProtectedBlockCount.TYPE, (payload, context) -> payload.handle(context.player()));
        PayloadTypeRegistry.serverboundPlay().register(PacketGetProtectedBlocks.TYPE, PacketGetProtectedBlocks.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PacketGetProtectedBlocks.TYPE, (payload, context) -> payload.handle(context.player()));
        PayloadTypeRegistry.serverboundPlay().register(PacketGetProtectedBlocksAroundPlayer.TYPE, PacketGetProtectedBlocksAroundPlayer.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PacketGetProtectedBlocksAroundPlayer.TYPE, (payload, context) -> payload.handle(context.player()));
        PayloadTypeRegistry.serverboundPlay().register(PacketToggleSubMode.TYPE, PacketToggleSubMode.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PacketToggleSubMode.TYPE, (payload, context) -> payload.handle(context.player()));
        PayloadTypeRegistry.serverboundPlay().register(PacketToggleMode.TYPE, PacketToggleMode.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PacketToggleMode.TYPE, (payload, context) -> payload.handle(context.player()));
    }

    public static void registerClientMessages() {
        ClientPlayNetworking.registerGlobalReceiver(PacketReturnProtectedBlocks.TYPE, (payload, context) -> payload.handle());
        ClientPlayNetworking.registerGlobalReceiver(PacketReturnProtectedBlocksAroundPlayer.TYPE, (payload, context) -> payload.handle());
        ClientPlayNetworking.registerGlobalReceiver(PacketReturnProtectedBlockCount.TYPE, (payload, context) -> payload.handle());
    }

    public static void sendToPlayer(CustomPacketPayload packet, ServerPlayer player) {
        if (ServerPlayNetworking.canSend(player, packet.type())) {
            ServerPlayNetworking.send(player, packet);
        }
    }

    public static void sendToServer(CustomPacketPayload packet) {
        if (ClientPlayNetworking.canSend(packet.type())) {
            ClientPlayNetworking.send(packet);
        }
    }
}
