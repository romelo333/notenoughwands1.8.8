package romelo333.notenoughwands.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.networking.CustomPayloadPacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.packet.CustomPayloadClientPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.packet.CustomPayloadServerPacket;
import net.minecraft.util.PacketByteBuf;

public class NetworkInit implements ModInitializer {

    @Override
    public void onInitialize() {
        System.out.println("############ Set up networking #############");

        // Server side
        CustomPayloadPacketRegistry.SERVER.register(PacketGetProtectedBlocks.GET_PROTECTED_BLOCKS, new PacketGetProtectedBlocks.Handler());
        CustomPayloadPacketRegistry.SERVER.register(PacketGetProtectedBlockCount.GET_PROTECTED_BLOCK_COUNT, new PacketGetProtectedBlockCount.Handler());
        CustomPayloadPacketRegistry.SERVER.register(PacketToggleMode.TOGGLE_MODE, new PacketToggleMode.Handler());
        CustomPayloadPacketRegistry.SERVER.register(PacketToggleSubMode.TOGGLE_SUB_MODE, new PacketToggleSubMode.Handler());

        // Client side
        CustomPayloadPacketRegistry.CLIENT.register(PacketReturnProtectedBlocks.RETURN_PROTECTED_BLOCKS, new PacketReturnProtectedBlocks.Handler());
        CustomPayloadPacketRegistry.CLIENT.register(PacketReturnProtectedBlockCount.RETURN_PROTECTED_BLOCK_COUNT, new PacketReturnProtectedBlockCount.Handler());
    }

    @Environment(EnvType.CLIENT)
    public static void sendToServer(IPacket packet) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        packet.toBytes(buf);
        MinecraftClient.getInstance().getNetworkHandler().getClientConnection().sendPacket(new CustomPayloadServerPacket(packet.getId(), buf));

    }

    @Environment(EnvType.SERVER)
    public static void sendToClient(IPacket packet, ServerPlayerEntity player) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        packet.toBytes(buf);
        player.networkHandler.sendPacket(new CustomPayloadClientPacket(packet.getId(), buf));
    }
}
