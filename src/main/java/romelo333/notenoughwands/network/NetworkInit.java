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
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import romelo333.notenoughwands.NotEnoughWands;

public class NetworkInit implements ModInitializer {

    public static final Identifier GET_PROTECTED_BLOCKS = new Identifier(NotEnoughWands.MODID, "get_protected_blocks");
    public static final Identifier RETURN_PROTECTED_BLOCKS = new Identifier(NotEnoughWands.MODID, "return_protected_blocks");
    public static final Identifier GET_PROTECTED_BLOCK_COUNT = new Identifier(NotEnoughWands.MODID, "get_protected_block_count");
    public static final Identifier RETURN_PROTECTED_BLOCK_COUNT = new Identifier(NotEnoughWands.MODID, "return_protected_block_count");
    public static final Identifier TOGGLE_MODE = new Identifier(NotEnoughWands.MODID, "toggle_mode");
    public static final Identifier TOGGLE_SUB_MODE = new Identifier(NotEnoughWands.MODID, "toggle_sub_mode");

    @Override
    public void onInitialize() {
        System.out.println("############ Set up networking #############");

        // Server side
        CustomPayloadPacketRegistry.SERVER.register(GET_PROTECTED_BLOCKS, new PacketGetProtectedBlocks.Handler());
        CustomPayloadPacketRegistry.SERVER.register(GET_PROTECTED_BLOCK_COUNT, new PacketGetProtectedBlockCount.Handler());
        CustomPayloadPacketRegistry.SERVER.register(TOGGLE_MODE, new PacketToggleMode.Handler());
        CustomPayloadPacketRegistry.SERVER.register(TOGGLE_SUB_MODE, new PacketToggleSubMode.Handler());

        // Client side
        CustomPayloadPacketRegistry.CLIENT.register(RETURN_PROTECTED_BLOCKS, new PacketReturnProtectedBlocks.Handler());
        CustomPayloadPacketRegistry.CLIENT.register(RETURN_PROTECTED_BLOCK_COUNT, new PacketReturnProtectedBlockCount.Handler());

    }

    @Environment(EnvType.CLIENT)
    public static void toggleMode(PacketToggleMode packet) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        packet.toBytes(buf);
        MinecraftClient.getInstance().getNetworkHandler().getClientConnection().sendPacket(new CustomPayloadServerPacket(TOGGLE_MODE, buf));
    }

    @Environment(EnvType.CLIENT)
    public static void toggleSubMode(PacketToggleSubMode packet) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        packet.toBytes(buf);
        MinecraftClient.getInstance().getNetworkHandler().getClientConnection().sendPacket(new CustomPayloadServerPacket(TOGGLE_SUB_MODE, buf));
    }


    @Environment(EnvType.SERVER)
    public static void returnProtectedBlocks(PacketReturnProtectedBlocks packet, ServerPlayerEntity player) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        packet.toBytes(buf);
        player.networkHandler.sendPacket(new CustomPayloadClientPacket(RETURN_PROTECTED_BLOCKS, buf));
    }

    @Environment(EnvType.CLIENT)
    public static void getProtectedBlocks(PacketGetProtectedBlocks packet) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        packet.toBytes(buf);
        MinecraftClient.getInstance().getNetworkHandler().getClientConnection().sendPacket(new CustomPayloadServerPacket(GET_PROTECTED_BLOCKS, buf));
    }


    @Environment(EnvType.SERVER)
    public static void returnProtectedBlockCount(PacketReturnProtectedBlockCount packet, ServerPlayerEntity player) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        packet.toBytes(buf);
        player.networkHandler.sendPacket(new CustomPayloadClientPacket(RETURN_PROTECTED_BLOCK_COUNT, buf));
    }

    @Environment(EnvType.CLIENT)
    public static void getProtectedBlockCount(PacketGetProtectedBlockCount packet) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        packet.toBytes(buf);
        MinecraftClient.getInstance().getNetworkHandler().getClientConnection().sendPacket(new CustomPayloadServerPacket(GET_PROTECTED_BLOCK_COUNT, buf));
    }
}
