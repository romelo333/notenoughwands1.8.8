package romelo333.notenoughwands.modules.protectionwand.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import romelo333.notenoughwands.NotEnoughWands;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public record PacketReturnProtectedBlocksAroundPlayer(Map<ChunkPos, Set<BlockPos>> blocks) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(NotEnoughWands.MODID, "returnprotectedblocksaroundplayer");
    public static final CustomPacketPayload.Type<PacketReturnProtectedBlocksAroundPlayer> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, PacketReturnProtectedBlocksAroundPlayer> CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, NeoForgeStreamCodecs.CHUNK_POS, ByteBufCodecs.collection(HashSet::new, BlockPos.STREAM_CODEC)), PacketReturnProtectedBlocksAroundPlayer::blocks,
            PacketReturnProtectedBlocksAroundPlayer::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static PacketReturnProtectedBlocksAroundPlayer create(Map<ChunkPos, Set<BlockPos>> blocks) {
        return new PacketReturnProtectedBlocksAroundPlayer(blocks);
    }

    public Map<ChunkPos, Set<BlockPos>> getBlocks() {
        return blocks;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ReturnProtectedBlocksAroundPlayerHelper.setProtectedBlocks(Level.OVERWORLD, this);
                    //McJtyLib.proxy.getClientWorld().dimension(), this); // @todo 1.15 no need for proxy here!
        });
    }
}