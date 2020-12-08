package romelo333.notenoughwands.modules.protectionwand.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.varia.DimensionId;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class PacketReturnProtectedBlocksAroundPlayer {
    private Map<ChunkPos, Set<BlockPos>> blocks;

    public void fromBytes(PacketBuffer buf) {
        int size = buf.readInt();
        blocks = new HashMap<ChunkPos, Set<BlockPos>> (size);
        for (int i = 0 ; i < size ; i++) {
            ChunkPos chunkpos = new ChunkPos(buf.readInt(), buf.readInt());

            int size2 = buf.readInt();
            Set<BlockPos> positions = new HashSet<>(size2);
            for (int j = 0 ; j < size2 ; j++) {
                positions.add(new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()));
            }
            blocks.put(chunkpos, positions);
        }
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(blocks.size());
        for (Map.Entry<ChunkPos, Set<BlockPos>> entry : blocks.entrySet()) {
            Set<BlockPos> positions = entry.getValue();
            buf.writeInt(entry.getKey().x);
            buf.writeInt(entry.getKey().z);
            buf.writeInt(positions.size());
            for (BlockPos block : positions) {
                buf.writeInt(block.getX());
                buf.writeInt(block.getY());
                buf.writeInt(block.getZ());
            }
        }
    }

    public Map<ChunkPos, Set<BlockPos>> getBlocks() {
        return blocks;
    }

    public PacketReturnProtectedBlocksAroundPlayer() {
    }

    public PacketReturnProtectedBlocksAroundPlayer(PacketBuffer buf) {
        fromBytes(buf);
    }

    public PacketReturnProtectedBlocksAroundPlayer(Map<ChunkPos, Set<BlockPos>> blocks) {
        this.blocks = blocks;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ReturnProtectedBlocksAroundPlayerHelper.setProtectedBlocks(
                    DimensionId.fromWorld(McJtyLib.proxy.getClientWorld()), this); // @todo 1.15 no need for proxy here!
        });
        ctx.setPacketHandled(true);
    }
}