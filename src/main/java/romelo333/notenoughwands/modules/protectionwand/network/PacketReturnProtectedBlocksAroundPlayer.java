package romelo333.notenoughwands.modules.protectionwand.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import romelo333.notenoughwands.NotEnoughWands;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public record PacketReturnProtectedBlocksAroundPlayer(Map<ChunkPos, Set<BlockPos>> blocks) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(NotEnoughWands.MODID, "returnprotectedblocksaroundplayer");

    public static PacketReturnProtectedBlocksAroundPlayer create(FriendlyByteBuf buf) {
        int size = buf.readInt();
        Map<ChunkPos, Set<BlockPos>> blocks = new HashMap<>(size);
        for (int i = 0 ; i < size ; i++) {
            ChunkPos chunkpos = new ChunkPos(buf.readInt(), buf.readInt());

            int size2 = buf.readInt();
            Set<BlockPos> positions = new HashSet<>(size2);
            for (int j = 0 ; j < size2 ; j++) {
                positions.add(buf.readBlockPos());
            }
            blocks.put(chunkpos, positions);
        }
        return new PacketReturnProtectedBlocksAroundPlayer(blocks);
    }

    public static PacketReturnProtectedBlocksAroundPlayer create(Map<ChunkPos, Set<BlockPos>> blocks) {
        return new PacketReturnProtectedBlocksAroundPlayer(blocks);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(blocks.size());
        for (Map.Entry<ChunkPos, Set<BlockPos>> entry : blocks.entrySet()) {
            Set<BlockPos> positions = entry.getValue();
            buf.writeInt(entry.getKey().x);
            buf.writeInt(entry.getKey().z);
            buf.writeInt(positions.size());
            for (BlockPos block : positions) {
                buf.writeBlockPos(block);
            }
        }
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public Map<ChunkPos, Set<BlockPos>> getBlocks() {
        return blocks;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ReturnProtectedBlocksAroundPlayerHelper.setProtectedBlocks(Level.OVERWORLD, this);
                    //McJtyLib.proxy.getClientWorld().dimension(), this); // @todo 1.15 no need for proxy here!
        });
    }
}