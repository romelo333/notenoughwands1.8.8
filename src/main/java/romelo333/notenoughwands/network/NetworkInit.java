package romelo333.notenoughwands.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.util.PacketByteBuf;

public class NetworkInit implements ModInitializer {

    @Override
    public void onInitialize() {
        System.out.println("############ Set up networking #############");

        // Server side
        ServerSidePacketRegistry.INSTANCE.register(PacketGetProtectedBlocks.GET_PROTECTED_BLOCKS, new PacketGetProtectedBlocks.Handler());
        ServerSidePacketRegistry.INSTANCE.register(PacketGetProtectedBlockCount.GET_PROTECTED_BLOCK_COUNT, new PacketGetProtectedBlockCount.Handler());
        ServerSidePacketRegistry.INSTANCE.register(PacketToggleMode.TOGGLE_MODE, new PacketToggleMode.Handler());
        ServerSidePacketRegistry.INSTANCE.register(PacketToggleSubMode.TOGGLE_SUB_MODE, new PacketToggleSubMode.Handler());

        // Client side
        ClientSidePacketRegistry.INSTANCE.register(PacketReturnProtectedBlocks.RETURN_PROTECTED_BLOCKS, new PacketReturnProtectedBlocks.Handler());
        ClientSidePacketRegistry.INSTANCE.register(PacketReturnProtectedBlockCount.RETURN_PROTECTED_BLOCK_COUNT, new PacketReturnProtectedBlockCount.Handler());
    }

    @Environment(EnvType.CLIENT)
    public static void sendToServer(IPacket packet) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        packet.toBytes(buf);
        MinecraftClient.getInstance().getNetworkHandler().getClientConnection().send(new CustomPayloadC2SPacket(packet.getId(), buf));

    }

    @Environment(EnvType.SERVER)
    public static void sendToClient(IPacket packet, ServerPlayerEntity player) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        packet.toBytes(buf);
        player.networkHandler.sendPacket(new CustomPayloadS2CPacket(packet.getId(), buf));
    }
}
