package romelo333.notenoughwands.modules.protectionwand.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import romelo333.notenoughwands.NotEnoughWands;

public record PacketReturnProtectedBlockCount(int count) implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(NotEnoughWands.MODID, "returnprotectedblockcount");
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

    public void handle() {
        ReturnProtectedBlockCountHelper.setProtectedBlocks(this);
    }
}
