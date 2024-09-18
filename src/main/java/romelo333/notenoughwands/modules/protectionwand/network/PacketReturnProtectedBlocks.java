package romelo333.notenoughwands.modules.protectionwand.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import romelo333.notenoughwands.NotEnoughWands;

import java.util.HashSet;
import java.util.Set;

public record PacketReturnProtectedBlocks(Set<BlockPos> blocks, Set<BlockPos> childBlocks) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(NotEnoughWands.MODID, "returnprotectedblocks");
    public static final CustomPacketPayload.Type<PacketReturnProtectedBlocks> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, PacketReturnProtectedBlocks> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC.apply(ByteBufCodecs.collection(HashSet::new)), PacketReturnProtectedBlocks::blocks,
            BlockPos.STREAM_CODEC.apply(ByteBufCodecs.collection(HashSet::new)), PacketReturnProtectedBlocks::childBlocks,
            PacketReturnProtectedBlocks::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static PacketReturnProtectedBlocks create(Set<BlockPos> blocks, Set<BlockPos> childBlocks) {
        return new PacketReturnProtectedBlocks(blocks, childBlocks);
    }

    public Set<BlockPos> getBlocks() {
        return blocks;
    }

    public Set<BlockPos> getChildBlocks() {
        return childBlocks;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ReturnProtectedBlocksHelper.setProtectedBlocks(this);
        });
    }
}