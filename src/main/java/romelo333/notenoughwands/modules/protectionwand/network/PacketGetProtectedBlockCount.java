package romelo333.notenoughwands.modules.protectionwand.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import romelo333.notenoughwands.NotEnoughWands;
import romelo333.notenoughwands.modules.protectionwand.ProtectedBlocks;
import romelo333.notenoughwands.network.NEWPacketHandler;

public record PacketGetProtectedBlockCount(Integer protectionId) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(NotEnoughWands.MODID, "getprotectedblockcount");
    public static final CustomPacketPayload.Type<PacketGetProtectedBlockCount> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, PacketGetProtectedBlockCount> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PacketGetProtectedBlockCount::protectionId,
            PacketGetProtectedBlockCount::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static PacketGetProtectedBlockCount create(Integer protectionId) {
        return new PacketGetProtectedBlockCount(protectionId);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            Level world = player.getCommandSenderWorld();

            ProtectedBlocks protectedBlocks = ProtectedBlocks.getProtectedBlocks(world);
            PacketReturnProtectedBlockCount msg = new PacketReturnProtectedBlockCount(protectedBlocks.getProtectedBlockCount(protectionId));
            NEWPacketHandler.sendToPlayer(msg, (ServerPlayer) player);
        });
    }
}