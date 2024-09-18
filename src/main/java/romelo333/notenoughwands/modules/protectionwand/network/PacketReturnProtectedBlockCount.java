package romelo333.notenoughwands.modules.protectionwand.network;


import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import romelo333.notenoughwands.NotEnoughWands;

public record PacketReturnProtectedBlockCount(int count) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(NotEnoughWands.MODID, "returnprotectedblockcount");
    public static final CustomPacketPayload.Type<PacketReturnProtectedBlockCount> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, PacketReturnProtectedBlockCount> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PacketReturnProtectedBlockCount::getCount, PacketReturnProtectedBlockCount::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static PacketReturnProtectedBlockCount create(int count) {
        return new PacketReturnProtectedBlockCount(count);
    }

    public int getCount() {
        return count;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ReturnProtectedBlockCountHelper.setProtectedBlocks(this);
        });
    }
}